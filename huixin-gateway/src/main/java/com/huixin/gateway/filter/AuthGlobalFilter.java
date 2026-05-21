package com.huixin.gateway.filter;

import com.huixin.common.enums.ResultCode;
import com.huixin.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 全局JWT鉴权过滤器
 *
 * @author Huixin Blog
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    /**
     * 白名单路径前缀（不含尾部斜杠，内部做规范化匹配）
     */
    private static final List<String> WHITELIST_PREFIXES = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh"
    );

    private static final String AUTH_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEADER_USER_ID  = "X-User-Id";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_ROLE     = "X-Role";

    /** 防头注入：需要被点赞检查的路径正则 */
    private static final Pattern LIKED_PATH = Pattern.compile("^/api/stats/article/\\d+/liked$");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        // ========== 安全：清除客户端可能伪造的内部请求头 ==========
        ServerHttpRequest cleanedRequest = request.mutate()
                .headers(headers -> {
                    headers.remove(HEADER_USER_ID);
                    headers.remove(HEADER_USERNAME);
                    headers.remove(HEADER_ROLE);
                })
                .build();
        exchange = exchange.mutate().request(cleanedRequest).build();

        // 1. OPTIONS预检放行（CORS）
        if (method == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // 2. 白名单放行
        if (isWhitelisted(path, method)) {
            log.debug("[白名单放行] {} {}", method, path);
            return chain.filter(exchange);
        }

        // 3. 获取Token
        String authHeader = request.getHeaders().getFirst(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("[缺少Token] {} {}", method, path);
            return unauthorized(exchange, "请先登录");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // 4. 验证Token
        if (!JwtUtil.validateToken(token)) {
            log.warn("[Token无效] {} {}", method, path);
            return unauthorized(exchange, "登录已过期，请重新登录");
        }

        // 5. 提取用户信息
        Claims claims = JwtUtil.parseToken(token);
        Long userId = claims.get(JwtUtil.CLAIM_USER_ID, Long.class);
        String username = claims.get(JwtUtil.CLAIM_USERNAME, String.class);
        Integer role = claims.get(JwtUtil.CLAIM_ROLE, Integer.class);

        if (userId == null) {
            log.warn("[Token信息缺失] {} {}", method, path);
            return unauthorized(exchange, "Token信息无效");
        }

        // 6. 注入请求头传递给下游（防止 null 传递）
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(HEADER_USER_ID, userId.toString())
                .header(HEADER_USERNAME, username != null ? username : "")
                .header(HEADER_ROLE, role != null ? role.toString() : "0")
                .build();

        log.debug("[鉴权通过] userId={}, username={}, role={}, path={}", userId, username, role, path);

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -100;
    }

    /* ==================== 内部方法 ==================== */

    /**
     * 判断路径是否在白名单中
     */
    private boolean isWhitelisted(String path, HttpMethod method) {
        // 路径规范化：确保以 / 结尾用于前缀匹配
        String normalizedPath = path.endsWith("/") ? path : path + "/";

        // 前缀白名单（精确路径 + 前缀匹配）
        for (String prefix : WHITELIST_PREFIXES) {
            if (path.equals(prefix) || path.startsWith(prefix + "/") || path.startsWith(prefix + "?")) {
                return true;
            }
        }

        // /api/auth/ 下的子路径也放行（如 /api/auth/logout 需要 JWT，由下游自行校验）
        // 注意：logout 和 refresh 需要 JWT 的场景，由具体服务自行校验，此处仅放行路由

        // 公开读取：分类、标签、搜索
        if (normalizedPath.startsWith("/api/category/") || normalizedPath.startsWith("/api/tag/")) {
            return true;
        }

        // 搜索：GET /api/search 和 /api/search/**
        if (method == HttpMethod.GET && (path.equals("/api/search") || normalizedPath.startsWith("/api/search/"))) {
            return true;
        }

        // 文章：GET 公开读取
        if (method == HttpMethod.GET && normalizedPath.startsWith("/api/article/")) {
            return true;
        }

        // 评论：GET 公开读取
        if (method == HttpMethod.GET && normalizedPath.startsWith("/api/comment/")) {
            return true;
        }

        // 统计：GET 公开读取（但 liked 需要认证）
        if (method == HttpMethod.GET && normalizedPath.startsWith("/api/stats/")) {
            if (LIKED_PATH.matcher(path).matches()) {
                return false; // 点赞状态查询需要认证
            }
            return true;
        }

        // POST /api/stats/article/{id}/view 公开（阅读量统计）
        if (method == HttpMethod.POST && path.matches("^/api/stats/article/\\d+/view$")) {
            return true;
        }

        // ===== 认证相关端点（公开） =====
        if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) {
            return true;
        }

        return false;
    }

    /**
     * 返回401未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"code\":%d,\"message\":\"%s\",\"data\":null,\"timestamp\":%d}",
                ResultCode.UNAUTHORIZED.getCode(), message, System.currentTimeMillis()
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

}
