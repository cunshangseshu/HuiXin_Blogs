package com.huixin.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改密码请求DTO
 * <p>
 * 需要提供旧密码进行验证，确保是本人操作。
 * </p>
 *
 * @author Huixin Blog
 */
@Data
@Schema(description = "修改密码请求")
public class ChangePasswordDTO {

    /**
     * 旧密码（用于身份验证）
     */
    @NotBlank(message = "旧密码不能为空")
    @Schema(description = "旧密码", example = "old12345")
    private String oldPassword;

    /**
     * 新密码（8-20位，至少包含字母和数字）
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, max = 20, message = "新密码长度需在8-20位之间")
    @Schema(description = "新密码", example = "new67890")
    private String newPassword;

}
