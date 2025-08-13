package com.origin.banyu.user.exception;

import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.exception.BusinessException;

/**
 * 用户已存在异常
 * 
 * @author scccy
 * @since 2025-08-01
 */
public class UserAlreadyExistsException extends BusinessException {

    public UserAlreadyExistsException(String message) {
        super(ErrorCode.USER_ALREADY_EXISTS, message);
    }

    public UserAlreadyExistsException(String username, String message) {
        super(ErrorCode.USER_ALREADY_EXISTS, "用户名: " + username + ", " + message);
    }
} 