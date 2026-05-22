package com.huixin.common.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.huixin.common.enums.ResultCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一API返回结果封装
 * <p>
 * 所有Controller返回值统一使用此类包装，前端根据code判断请求结果。
 * </p>
 *
 * @param <T> 业务数据的类型
 * @author 爱吃罗氏虾
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码：200成功，4xx客户端错误，5xx服务端错误
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 业务数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /* ==================== 成功结果 ==================== */

    /**
     * 请求成功（无数据）
     */
    public static <T> ResultVO<T> success() {
        return build(ResultCode.SUCCESS, null);
    }

    /**
     * 请求成功（带数据）
     *
     * @param data 业务数据
     */
    public static <T> ResultVO<T> success(T data) {
        return build(ResultCode.SUCCESS, data);
    }

    /**
     * 请求成功（自定义消息）
     *
     * @param message 提示消息
     */
    public static <T> ResultVO<T> success(String message) {
        ResultVO<T> vo = build(ResultCode.SUCCESS, null);
        vo.setMessage(message);
        return vo;
    }

    /* ==================== 失败结果 ==================== */

    /**
     * 请求失败
     *
     * @param resultCode 状态码枚举
     */
    public static <T> ResultVO<T> error(ResultCode resultCode) {
        return build(resultCode, null);
    }

    /**
     * 请求失败（自定义消息）
     *
     * @param resultCode 状态码枚举
     * @param message    自定义错误消息
     */
    public static <T> ResultVO<T> error(ResultCode resultCode, String message) {
        ResultVO<T> vo = build(resultCode, null);
        vo.setMessage(message);
        return vo;
    }

    /**
     * 请求失败（自定义code和message）
     *
     * @param code    自定义状态码
     * @param message 错误消息
     */
    public static <T> ResultVO<T> error(Integer code, String message) {
        ResultVO<T> vo = new ResultVO<>();
        vo.setCode(code);
        vo.setMessage(message);
        vo.setTimestamp(System.currentTimeMillis());
        return vo;
    }

    /* ==================== 内部构建方法 ==================== */

    private static <T> ResultVO<T> build(ResultCode resultCode, T data) {
        ResultVO<T> vo = new ResultVO<>();
        vo.setCode(resultCode.getCode());
        vo.setMessage(resultCode.getMessage());
        vo.setData(data);
        vo.setTimestamp(System.currentTimeMillis());
        return vo;
    }

}
