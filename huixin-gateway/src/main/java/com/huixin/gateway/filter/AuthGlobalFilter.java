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

/**
 * 全局JWT鉴权过滤器
 * <p>
 * 在Gateway层统一校验JWT Token，校验通过后将用户信息注入请求头（X-User-Id/X-Username/X-Role），
 * 下游微服务直接从请求头获取当前用户信息，无需重复解析Token。
 * </p>
 *
 * <p><b>白名单路径（无需Token）：</b></p>
 * <ul>
 *   <li>/api/auth/** — 注册/登录</li>
 *   <li>GET /api/article/** — 文章列表/详情（公开阅读）</li>
 *   <li>GET /api/category/** — 分类查询</li>
 *   <li>GET /api/tag/** — 标签查询</li>
 *   <li>GET /api/search/** — 搜索</li>
 *   <li>GET /api/stats/** — 统计数据</li>
 *   <li>POST /api/stats/article/*\/view — 公开阅读统计</li>
 * </ul>
 *
 * @author Huixin Blog
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    /**
     * 白名单路径前缀
     */
    private static final List<String> WHITELIST_PREFIXES = List.of(
            "/api/auth/",
            "/api/search/",
            "/api/category/",
            "/api/tag/"
    );

    /**
     * 认证请求头名称
     */
    private static final String AUTH_HEADER = HttpHeaders.AUTHORIZATION;

    /**
     * Bearer Token前缀
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 传递给下游的用户信息请求头
     */
    private static final String HEADER_USER_ID   = "X-User-Id";
    private static final String HEADER_USERNAME  = "X-Username";
    private static final String HEADER_ROLE      = "X-Role";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        // 1. 白名单放行
        // OPTIONS预检请求放行（CORS）
        if (method == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }
        if (isWhitelisted(path, method)) {
            log.debug("[白名单放行] {} {}", method, path);
            return chain.filter(exchange);
        }

        // 2. 获取Token
        String authHeader = request.getHeaders().getFirst(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("[缺少Token] {} {}", method, path);
            return unauthorized(exchange, "请先登录");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // 3. 验证Token
        if (!JwtUtil.validateToken(token)) {
            log.warn("[Token无效] {} {}", method, path);
            return unauthorized(exchange, "登录已过期，请重新登录");
        }

        // 4. 提取用户信息
        Claims claims = JwtUtil.parseToken(token);
        Long userId = claims.get(JwtUtil.CLAIM_USER_ID, Long.class);
        String username = claims.get(JwtUtil.CLAIM_USERNAME, String.class);
        Integer role = claims.get(JwtUtil.CLAIM_ROLE, Integer.class);

        if (userId == null) {
            log.warn("[Token信息缺失] {} {}", method, path);
            return unauthorized(exchange, "Token信息无效");
        }

        // 5. 注入请求头传递给下游
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(HEADER_USER_ID, userId.toString())
                .header(HEADER_USERNAME, username)
                .header(HEADER_ROLE, role != null ? role.toString() : "0")
                .build();

        log.debug("[鉴权通过] userId={}, username={}, role={}, path={}", userId, username, role, path);

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        // 优先级最高，在其他过滤器之前执行
        return -100;
    }

    /* ==================== 内部方法 ==================== */

    /**
     * 判断路径是否在白名单中
     */
    private boolean isWhitelisted(String path, HttpMethod method) {
        // 前缀白名单
        for (String prefix : WHITELIST_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }

        // GET请求的文章/统计接口公开
        if (method == HttpMethod.GET) {
            if (path.startsWith("/api/article/") || path.startsWith("/api/stats/")) {
                return true;
            }
        }

        // POST /api/stats/article/{id}/view 公开（阅读量统计）
        if (method == HttpMethod.POST && path.matches("^/api/stats/article/\\d+/view$")) {
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
