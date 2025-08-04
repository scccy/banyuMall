package com.origin.publisher.exception;

import com.origin.common.entity.ErrorCode;
import com.origin.common.exception.BusinessException;

/**
 * 文件上传异常
 * 
 * @author scccy
 * @since 2025-08-01
 */
public class FileUploadException extends BusinessException {

    public FileUploadException(String message) {
        super(ErrorCode.OSS_FILE_UPLOAD_FAILED, message);
    }

    public FileUploadException(String taskId, String fileType, String message) {
        super(ErrorCode.OSS_FILE_UPLOAD_FAILED, "任务ID: " + taskId + ", 文件类型: " + fileType + ", 上传失败: " + message);
    }
} 