package com.huixin.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT（JSON Web Token）工具类
 * <p>
 * 提供Token的生成、解析、验证等操作。
 * 使用HMAC-SHA256签名算法。
 * </p>
 * <p>
 * Token类型：
 * - Access Token：有效期2小时，用于API鉴权
 * - Refresh Token：有效期7天，用于刷新Access Token
 * </p>
 *
 * @author Huixin Blog
 */
@Slf4j
public class JwtUtil {

    /**
     * 签名密钥
     * <p>
     * 优先级：系统属性 jwt.secret > 环境变量 JWT_SECRET > 默认开发密钥。
     * 生产环境必须通过环境变量或启动参数注入强随机密钥。
     * </p>
     * <p>生成强密钥示例：{@code openssl rand -base64 32}</p>
     */
    private static final String SECRET_KEY;
    private static final SecretKey KEY;

    static {
        String configuredKey = System.getProperty("jwt.secret");
        if (configuredKey == null || configuredKey.isBlank()) {
            configuredKey = System.getenv("JWT_SECRET");
        }
        if (configuredKey == null || configuredKey.isBlank()) {
            log.warn("[JWT] 未配置自定义密钥，使用默认开发密钥。生产环境请设置环境变量 JWT_SECRET 或系统属性 jwt.secret");
            configuredKey = "huixin-blog-jwt-secret-key-2026-please-change-in-production";
        }
        SECRET_KEY = configuredKey;
        KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Access Token 过期时间：2小时（毫秒）
     */
    public static final long ACCESS_TOKEN_EXPIRE = 2 * 60 * 60 * 1000L;

    /**
     * Refresh Token 过期时间：7天（毫秒）
     */
    public static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60 * 1000L;

    /**
     * JWT签发者
     */
    private static final String ISSUER = "huixin-blog";

    /**
     * Claims中的用户ID键名
     */
    public static final String CLAIM_USER_ID = "userId";

    /**
     * Claims中的用户名键名
     */
    public static final String CLAIM_USERNAME = "username";

    /**
     * Claims中的角色键名
     */
    public static final String CLAIM_ROLE = "role";

    /**
     * 生成Access Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param role     角色类型（0-普通用户，1-博主）
     * @return JWT字符串
     */
    public static String generateAccessToken(Long userId, String username, Integer role) {
        return generateToken(userId, username, role, ACCESS_TOKEN_EXPIRE);
    }

    /**
     * 生成Refresh Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param role     角色类型
     * @return JWT字符串
     */
    public static String generateRefreshToken(Long userId, String username, Integer role) {
        return generateToken(userId, username, role, REFRESH_TOKEN_EXPIRE);
    }

    /**
     * 从Token中解析Claims
     *
     * @param token JWT字符串
     * @return Claims
     * @throws JwtException Token无效或已过期
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token JWT字符串
     * @return 用户ID
     */
    public static Long getUserId(String token) {
        return parseToken(token).get(CLAIM_USER_ID, Long.class);
    }

    /**
     * 从Token中获取用户名
     *
     * @param token JWT字符串
     * @return 用户名
     */
    public static String getUsername(String token) {
        return parseToken(token).get(CLAIM_USERNAME, String.class);
    }

    /**
     * 从Token中获取角色类型
     *
     * @param token JWT字符串
     * @return 角色类型
     */
    public static Integer getRole(String token) {
        return parseToken(token).get(CLAIM_ROLE, Integer.class);
    }

    /**
     * 验证Token是否有效
     *
     * @param token JWT字符串
     * @return true-有效，false-无效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (SignatureException e) {
            log.warn("[JWT] 签名无效: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("[JWT] Token已过期: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("[JWT] Token格式错误: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("[JWT] Token验证异常: {}", e.getMessage());
        }
        return false;
    }

    /* ==================== 私有方法 ==================== */

    /**
     * 生成JWT Token
     */
    private static String generateToken(Long userId, String username, Integer role, long expireMillis) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireMillis);

        return Jwts.builder()
                // 自定义Claims
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_USERNAME, username)
                .claim(CLAIM_ROLE, role)
                // 签发者
                .issuer(ISSUER)
                // 签发时间
                .issuedAt(now)
                // 过期时间
                .expiration(expiration)
                // 签名
                .signWith(KEY)
                .compact();
    }

}
