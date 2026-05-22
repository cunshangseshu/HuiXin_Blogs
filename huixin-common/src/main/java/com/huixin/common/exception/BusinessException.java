package com.huixin.common.exception;

import com.huixin.common.enums.ResultCode;
import lombok.Getter;

/**
 * 业务异常类
 * <p>
 * 用于在Service层抛出业务逻辑异常，由GlobalExceptionHandler统一捕获处理。
 * 所有非预期的业务情况都应抛出此异常或其子类。
 * </p>
 *
 * @author 爱吃罗氏虾
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 使用ResultCode枚举构造异常
     *
     * @param resultCode 状态码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * 使用ResultCode枚举 + 自定义消息构造异常
     *
     * @param resultCode 状态码枚举
     * @param message    自定义错误消息（覆盖枚举默认消息）
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    /**
     * 使用自定义code和消息构造异常
     *
     * @param code    自定义状态码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

}
