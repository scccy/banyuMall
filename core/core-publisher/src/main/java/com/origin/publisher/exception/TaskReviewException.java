package com.origin.publisher.exception;

import com.origin.common.entity.ErrorCode;
import com.origin.common.exception.BusinessException;

/**
 * 任务审核异常
 * 
 * @author scccy
 * @since 2025-08-01
 */
public class TaskReviewException extends BusinessException {

    public TaskReviewException(String message) {
        super(ErrorCode.PUBLISHER_TASK_REVIEW_STATUS_INVALID, message);
    }

    public TaskReviewException(String taskId, String reviewStatus, String message) {
        super(ErrorCode.PUBLISHER_TASK_REVIEW_STATUS_INVALID, "任务ID: " + taskId + ", 审核状态: " + reviewStatus + ", " + message);
    }
} 