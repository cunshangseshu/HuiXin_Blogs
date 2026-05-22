package com.huixin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求DTO
 * <p>
 * 支持使用用户名或邮箱登录。
 * </p>
 *
 * @author 爱吃罗氏虾
 */
@Data
@Schema(description = "登录请求")
public class LoginDTO {

    /**
     * 用户名或邮箱
     */
    @NotBlank(message = "用户名/邮箱不能为空")
    @Schema(description = "用户名或邮箱", example = "zhangsan")
    private String account;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "abc12345")
    private String password;

}
