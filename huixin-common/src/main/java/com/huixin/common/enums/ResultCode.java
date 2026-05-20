package com.huixin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应状态码枚举
 * <p>
 * 200：成功
 * 4xx：客户端错误（参数、认证、权限、资源等）
 * 5xx：服务端错误
 * </p>
 *
 * @author Huixin Blog
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /* ---------- 成功 ---------- */
    SUCCESS(200, "操作成功"),

    /* ---------- 客户端错误 4xx ---------- */
    BAD_REQUEST(400, "请求参数有误"),
    UNAUTHORIZED(401, "请先登录"),
    FORBIDDEN(403, "没有操作权限"),
    NOT_FOUND(404, "请求的资源不存在"),
    METHOD_NOT_ALLOWED(405, "不支持的请求方法"),
    CONFLICT(409, "数据冲突"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),

    /* ---------- 业务错误 ---------- */
    USERNAME_EXIST(4001, "用户名已存在"),
    EMAIL_EXIST(4002, "邮箱已被注册"),
    USER_NOT_FOUND(4003, "用户不存在"),
    PASSWORD_ERROR(4004, "密码错误"),
    ACCOUNT_DISABLED(4005, "账号已被禁用"),
    TOKEN_EXPIRED(4006, "登录已过期，请重新登录"),
    TOKEN_INVALID(4007, "无效的Token"),
    ARTICLE_NOT_FOUND(4008, "文章不存在"),
    CATEGORY_NOT_FOUND(4009, "分类不存在"),
    TAG_NOT_FOUND(4010, "标签不存在"),
    COMMENT_NOT_FOUND(4011, "评论不存在"),
    ALREADY_LIKED(4012, "已经点过赞了"),
    NOT_LIKED(4013, "还没有点赞"),
    BLOGGER_APPLY_DUPLICATE(4014, "已经申请过博主，请勿重复申请"),

    /* ---------- 服务端错误 5xx ---------- */
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),
    FILE_UPLOAD_FAILED(5001, "文件上传失败"),
    DATABASE_ERROR(5002, "数据库操作异常");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态码对应的消息
     */
    private final String message;

}
