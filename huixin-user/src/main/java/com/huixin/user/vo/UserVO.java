package com.huixin.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息返回VO
 * <p>
 * 对外暴露的用户信息视图，屏蔽敏感字段（密码、逻辑删除标识等）。
 * </p>
 *
 * @author Huixin Blog
 */
@Data
@Builder
@Schema(description = "用户信息")
public class UserVO {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    /**
     * 昵称
     */
    @Schema(description = "昵称", example = "程序员张三")
    private String nickname;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL")
    private String avatarUrl;

    /**
     * 个人简介
     */
    @Schema(description = "个人简介", example = "一个热爱技术的全栈开发者")
    private String bio;

    /**
     * 角色类型：0-普通用户，1-博主
     */
    @Schema(description = "角色类型", example = "1")
    private Integer roleType;

    /**
     * 注册时间
     */
    @Schema(description = "注册时间")
    private LocalDateTime createTime;

}
