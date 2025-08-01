package com.origin.user.exception;

import com.origin.common.dto.ResultData;
import com.origin.common.entity.ErrorCode;
import com.origin.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultData<Object> handleUserNotFoundException(UserNotFoundException e) {
        log.warn("用户不存在: {}", e.getMessage());
        return ResultData.fail(ErrorCode.USER_NOT_FOUND, e.getMessage());
    }

    /**
     * 处理用户已存在异常
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResultData<Object> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        log.warn("用户已存在: {}", e.getMessage());
        return ResultData.fail(ErrorCode.USER_ALREADY_EXISTS, e.getMessage());
    }

    /**
     * 处理用户头像上传异常
     */
    @ExceptionHandler(UserAvatarUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleUserAvatarUploadException(UserAvatarUploadException e) {
        log.warn("用户头像上传异常: {}", e.getMessage());
        return ResultData.fail(ErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
    }

    /**
     * 处理用户权限异常
     */
    @ExceptionHandler(UserPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResultData<Object> handleUserPermissionException(UserPermissionException e) {
        log.warn("用户权限异常: {}", e.getMessage());
        return ResultData.fail(ErrorCode.FORBIDDEN, e.getMessage());
    }

    /**
     * 处理用户业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleBusinessException(BusinessException e) {
        log.warn("用户业务异常: {}", e.getMessage());
        return ResultData.fail(e.getErrorCode(), e.getMessage());
    }
} 