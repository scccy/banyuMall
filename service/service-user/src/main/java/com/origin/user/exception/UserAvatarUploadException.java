package com.origin.user.exception;

import com.origin.common.entity.ErrorCode;
import com.origin.common.exception.BusinessException;

/**
 * 用户头像上传异常
 * 
 * @author scccy
 * @since 2025-08-01
 */
public class UserAvatarUploadException extends BusinessException {

    public UserAvatarUploadException(String message) {
        super(ErrorCode.USER_AVATAR_UPLOAD_FAILED, message);
    }

    public UserAvatarUploadException(String userId, String message) {
        super(ErrorCode.USER_AVATAR_UPLOAD_FAILED, "用户ID: " + userId + ", 头像上传失败: " + message);
    }
} 