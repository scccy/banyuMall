package com.origin.user.exception;

import com.origin.common.entity.ErrorCode;
import com.origin.common.exception.BusinessException;

/**
 * 用户不存在异常
 * 
 * @author scccy
 * @since 2025-08-01
 */
public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String message) {
        super(ErrorCode.USER_NOT_FOUND, message);
    }

    public UserNotFoundException(String userId, String message) {
        super(ErrorCode.USER_NOT_FOUND, "用户ID: " + userId + ", " + message);
    }
} 