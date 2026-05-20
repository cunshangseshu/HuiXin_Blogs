package com.huixin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * Token返回VO
 * <p>
 * 登录成功或刷新Token后返回给前端。
 * </p>
 *
 * @author Huixin Blog
 */
@Data
@Builder
@Schema(description = "Token信息")
public class TokenVO {

    /**
     * 访问令牌（Access Token），有效期2小时
     */
    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    /**
     * 刷新令牌（Refresh Token），有效期7天
     */
    @Schema(description = "刷新令牌", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    /**
     * Access Token过期时间（秒）
     */
    @Schema(description = "过期时间（秒）", example = "7200")
    private Long expiresIn;

}
