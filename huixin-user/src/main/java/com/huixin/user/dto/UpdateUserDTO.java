package com.huixin.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户信息请求DTO
 * <p>
 * 仅允许修改昵称和个人简介，用户名/邮箱/角色等敏感字段不可通过此接口修改。
 * </p>
 *
 * @author 爱吃罗氏虾
 */
@Data
@Schema(description = "更新用户信息请求")
public class UpdateUserDTO {

    /**
     * 昵称（1-20字符）
     */
    @Size(min = 1, max = 20, message = "昵称长度需在1-20字符之间")
    @Schema(description = "昵称", example = "程序员张三")
    private String nickname;

    /**
     * 个人简介（最多200字符）
     */
    @Size(max = 200, message = "个人简介不能超过200字符")
    @Schema(description = "个人简介", example = "一个热爱技术的全栈开发者")
    private String bio;

}
