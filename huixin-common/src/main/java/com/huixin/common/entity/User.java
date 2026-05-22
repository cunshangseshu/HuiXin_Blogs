package com.huixin.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 * <p>
 * 对应用户表（user），存储所有用户的基本信息。
 * 角色区分：roleType = 0 普通用户，1 博主。
 * </p>
 *
 * @author 爱吃罗氏虾
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
@Schema(description = "用户信息")
public class User extends BaseEntity {

    /**
     * 用户名（登录用，唯一）
     */
    @TableField("username")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 密码（BCrypt加密存储）
     */
    @TableField("password")
    @Schema(description = "密码（加密）")
    private String password;

    /**
     * 邮箱
     */
    @TableField("email")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    /**
     * 昵称（页面显示用）
     */
    @TableField("nickname")
    @Schema(description = "昵称", example = "张三")
    private String nickname;

    /**
     * 头像URL
     */
    @TableField("avatar_url")
    @Schema(description = "头像图片URL")
    private String avatarUrl;

    /**
     * 个人简介/个性签名
     */
    @TableField("bio")
    @Schema(description = "个人简介", example = "一个热爱技术的程序员")
    private String bio;

    /**
     * 角色类型：0-普通用户，1-博主
     */
    @TableField("role_type")
    @Schema(description = "角色类型：0-普通用户，1-博主", example = "0")
    private Integer roleType;

    /**
     * 账号状态：0-禁用，1-正常
     */
    @TableField("status")
    @Schema(description = "账号状态：0-禁用，1-正常", example = "1")
    private Integer status;

}
