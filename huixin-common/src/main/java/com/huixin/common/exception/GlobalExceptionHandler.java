package com.huixin.common.exception;

import com.huixin.common.enums.ResultCode;
import com.huixin.common.vo.ResultVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一捕获并处理Controller层抛出的各类异常，
 * 返回标准格式的ResultVO，避免异常信息直接暴露给前端。
 * </p>
 *
 * @author Huixin Blog
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ==================== 业务异常 ==================== */

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResultVO<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("[业务异常] path={}, code={}, message={}", request.getRequestURI(), e.getCode(), e.getMessage());
        return ResultVO.error(e.getCode(), e.getMessage());
    }

    /* ==================== 参数校验异常 ==================== */

    /**
     * 处理 @Valid 校验失败异常（JSON请求体）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("[参数校验失败] path={}, errors={}", request.getRequestURI(), message);
        return ResultVO.error(ResultCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理表单绑定异常（表单请求）
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Void> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("[参数绑定失败] path={}, errors={}", request.getRequestURI(), message);
        return ResultVO.error(ResultCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Void> handleMissingParam(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("[缺少参数] path={}, param={}", request.getRequestURI(), e.getParameterName());
        return ResultVO.error(ResultCode.BAD_REQUEST.getCode(),
                "缺少必要参数: " + e.getParameterName());
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("[参数类型错误] path={}, param={}, value={}", request.getRequestURI(), e.getName(), e.getValue());
        return ResultVO.error(ResultCode.BAD_REQUEST.getCode(),
                "参数类型错误: " + e.getName());
    }

    /* ==================== 运行时异常 ==================== */

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVO<Void> handleNullPointer(NullPointerException e, HttpServletRequest request) {
        log.error("[空指针异常] path={}", request.getRequestURI(), e);
        return ResultVO.error(ResultCode.INTERNAL_ERROR);
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVO<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("[系统异常] path={}", request.getRequestURI(), e);
        return ResultVO.error(ResultCode.INTERNAL_ERROR);
    }

}
