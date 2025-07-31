package com.origin.auth.exception;

import com.origin.common.dto.ResultData;
import com.origin.common.entity.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 认证异常处理器
 * 
 * @author origin
 * @since 2024-12-19
 */
@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    /**
     * 处理认证异常
     * 
     * @param e 认证异常
     * @return 错误响应
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResultData<Object> handleAuthenticationException(Exception e) {
        log.error("认证异常: {}", e.getMessage());
        return ResultData.fail(ErrorCode.UNAUTHORIZED, "认证失败: " + e.getMessage());
    }

    /**
     * 处理授权异常
     * 
     * @param e 授权异常
     * @return 错误响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResultData<Object> handleAccessDeniedException(AccessDeniedException e) {
        log.error("授权异常: {}", e.getMessage());
        return ResultData.fail(ErrorCode.FORBIDDEN, "没有权限访问该资源");
    }
} 