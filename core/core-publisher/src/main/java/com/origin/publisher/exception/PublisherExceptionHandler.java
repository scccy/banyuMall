package com.origin.publisher.exception;

import com.origin.common.dto.ResultData;
import com.origin.common.entity.ErrorCode;
import com.origin.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 发布者服务异常处理器
 * 处理发布者相关的业务异常
 * 
 * @author scccy
 * @since 2025-08-01
 */
@Slf4j
@RestControllerAdvice
public class PublisherExceptionHandler {

    /**
     * 处理任务不存在异常
     */
    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultData<Object> handleTaskNotFoundException(TaskNotFoundException e) {
        log.warn("任务不存在: {}", e.getMessage());
        return ResultData.fail(ErrorCode.TASK_NOT_FOUND, e.getMessage());
    }

    /**
     * 处理任务状态异常
     */
    @ExceptionHandler(TaskStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleTaskStatusException(TaskStatusException e) {
        log.warn("任务状态异常: {}", e.getMessage());
        return ResultData.fail(ErrorCode.TASK_STATUS_INVALID, e.getMessage());
    }

    /**
     * 处理任务审核异常
     */
    @ExceptionHandler(TaskReviewException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleTaskReviewException(TaskReviewException e) {
        log.warn("任务审核异常: {}", e.getMessage());
        return ResultData.fail(ErrorCode.TASK_REVIEW_STATUS_INVALID, e.getMessage());
    }

    /**
     * 处理文件上传异常
     */
    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleFileUploadException(FileUploadException e) {
        log.warn("文件上传异常: {}", e.getMessage());
        return ResultData.fail(ErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleBusinessException(BusinessException e) {
        log.warn("发布者业务异常: {}", e.getMessage());
        return ResultData.fail(e.getErrorCode(), e.getMessage());
    }
} 