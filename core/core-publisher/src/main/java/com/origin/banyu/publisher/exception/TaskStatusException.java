package com.origin.banyu.publisher.exception;

import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.exception.BusinessException;

/**
 * 任务状态异常
 * 
 * @author scccy
 * @since 2025-08-01
 */
public class TaskStatusException extends BusinessException {

    public TaskStatusException(String message) {
        super(ErrorCode.PUBLISHER_TASK_STATUS_INVALID, message);
    }

    public TaskStatusException(String taskId, String currentStatus, String expectedStatus) {
        super(ErrorCode.PUBLISHER_TASK_STATUS_INVALID, "任务ID: " + taskId + ", 当前状态: " + currentStatus + ", 期望状态: " + expectedStatus);
    }
} 