package com.huixin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求DTO
 *
 * @author 爱吃罗氏虾
 */
@Data
@Schema(description = "注册请求")
public class RegisterDTO {

    /**
     * 用户名（4-20位字母数字下划线）
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度需在4-20位之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 密码（8-20位，至少包含字母和数字）
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度需在8-20位之间")
    @Pattern(regexp = "^(?=\\S+$)(?=.*[a-zA-Z])(?=.*\\d).+$", message = "密码必须包含字母和数字，且不能包含空格")
    @Schema(description = "密码", example = "abc12345")
    private String password;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

}
