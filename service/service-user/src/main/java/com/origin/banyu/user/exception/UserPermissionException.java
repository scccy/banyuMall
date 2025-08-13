package com.origin.banyu.user.exception;

import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.exception.BusinessException;

/**
 * 用户权限异常
 * 
 * @author scccy
 * @since 2025-08-01
 */
public class UserPermissionException extends BusinessException {

    public UserPermissionException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }

    public UserPermissionException(String userId, String message) {
        super(ErrorCode.FORBIDDEN, "用户ID: " + userId + ", 权限不足: " + message);
    }
} 