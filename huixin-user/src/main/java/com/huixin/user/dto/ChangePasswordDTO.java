package com.huixin.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "修改密码请求")
public class ChangePasswordDTO {

    @NotBlank(message = "旧密码不能为空")
    @Schema(description = "旧密码", example = "old12345")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, max = 20, message = "新密码长度需在8-20位之间")
    @Pattern(regexp = "^(?=\\S+$)(?=.*[a-zA-Z])(?=.*\\d).+$", message = "新密码必须包含字母和数字，且不能包含空格")
    @Schema(description = "新密码", example = "new67890")
    private String newPassword;

}
