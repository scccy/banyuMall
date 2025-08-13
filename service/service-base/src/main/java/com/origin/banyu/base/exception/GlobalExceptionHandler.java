package com.origin.banyu.base.exception;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.exception.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 排除Gateway服务，因为Gateway使用WebFlux异常处理
 * 
 * 职责范围：
 * 1. 处理通用基础异常（参数校验、运行时异常等）
 * 2. 为各微服务模块提供兜底异常处理
 * 3. 各微服务模块应该有自己的业务异常处理器
 *
 * @author origin
 * @since 2025-07-31
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnMissingClass("com.origin.gateway.GatewayApplication")
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * 注意：此方法仅作为兜底处理，各微服务模块应该有自己的业务异常处理器
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleBusinessException(BusinessException e) {
        log.warn("业务异常（base模块兜底处理）: {}", e.getMessage());
        return ResultData.fail(e.getErrorCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验异常: {}", message);
        return ResultData.fail(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定异常: {}", message);
        return ResultData.fail(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("约束违反异常: {}", message);
        return ResultData.fail(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultData<Object> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        return ResultData.fail(ErrorCode.INTERNAL_ERROR, "系统内部错误");
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultData<Object> handleException(Exception e) {
        log.error("系统异常: ", e);
        return ResultData.fail(ErrorCode.INTERNAL_ERROR, "系统异常，请联系管理员");
    }
} 