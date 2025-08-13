package com.origin.banyu.user.exception;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.exception.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;
/**
 * 用户服务异常处理器
 * 处理用户相关的业务异常
 * 
 * @author scccy
 * @since 2025-08-01
 */
@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {

    /**
     * 处理用户不存在异常
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResultData<Object> handleUserNotFoundException(UserNotFoundException e) {
        log.warn("用户不存在: {}", e.getMessage());
        return ResultData.fail(ErrorCode.USER_NOT_FOUND, e.getMessage());
    }

    /**
     * 处理用户已存在异常
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResultData<Object> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        log.warn("用户已存在: {}", e.getMessage());
        return ResultData.fail(ErrorCode.USER_ALREADY_EXISTS, e.getMessage());
    }

    /**
     * 处理用户头像上传异常
     */
    @ExceptionHandler(UserAvatarUploadException.class)
    public ResultData<Object> handleUserAvatarUploadException(UserAvatarUploadException e) {
        log.warn("用户头像上传异常: {}", e.getMessage());
        return ResultData.fail(ErrorCode.USER_AVATAR_UPLOAD_FAILED, e.getMessage());
    }

    /**
     * 处理用户权限异常
     */
    @ExceptionHandler(UserPermissionException.class)
    public ResultData<Object> handleUserPermissionException(UserPermissionException e) {
        log.warn("用户权限异常: {}", e.getMessage());
        return ResultData.fail(ErrorCode.FORBIDDEN, e.getMessage());
    }

    /**
     * 处理用户业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResultData<Object> handleBusinessException(BusinessException e) {
        log.warn("用户业务异常: {}", e.getMessage());
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
        log.warn("用户参数校验异常: {}", message);
        return ResultData.fail(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("用户绑定异常: {}", message);
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
        log.warn("用户约束违反异常: {}", message);
        return ResultData.fail(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("用户文件上传大小超限: {}", e.getMessage());
        return ResultData.fail(ErrorCode.PARAM_ERROR, "上传文件大小超出限制");
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultData<Object> handleRuntimeException(RuntimeException e) {
        log.error("用户服务运行时异常: ", e);
        return ResultData.fail(ErrorCode.INTERNAL_ERROR, "服务异常，请稍后重试");
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultData<Object> handleException(Exception e) {
        log.error("用户服务系统异常: ", e);
        return ResultData.fail(ErrorCode.INTERNAL_ERROR, "系统异常，请联系管理员");
    }
}