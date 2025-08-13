package com.origin.banyu.publisher.exception;

import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.exception.BusinessException;

/**
 * 任务不存在异常
 * 
 * @author scccy
 * @since 2025-08-01
 */
public class TaskNotFoundException extends BusinessException {

    public TaskNotFoundException(String message) {
        super(ErrorCode.PUBLISHER_TASK_NOT_FOUND, message);
    }

    public TaskNotFoundException(String taskId, String message) {
        super(ErrorCode.PUBLISHER_TASK_NOT_FOUND, "任务ID: " + taskId + ", " + message);
    }
} 