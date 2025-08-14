package com.origin.banyu.publisher.exception;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 发布者服务异常处理器
 * 已注释：Controller层异常现在由base模块统一处理
 * 
 * @author scccy
 * @since 2025-08-01
 */
/*
@Slf4j
@RestControllerAdvice
public class PublisherExceptionHandler {

    // 所有异常处理方法已注释，由base模块统一处理
    
    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultData<Object> handleTaskNotFoundException(TaskNotFoundException e) {
        log.warn("任务不存在: {}", e.getMessage());
        return ResultData.fail(ErrorCode.PUBLISHER_TASK_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(TaskStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleTaskStatusException(TaskStatusException e) {
        log.warn("任务状态异常: {}", e.getMessage());
        return ResultData.fail(ErrorCode.PUBLISHER_TASK_STATUS_INVALID, e.getMessage());
    }

    @ExceptionHandler(TaskReviewException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleTaskReviewException(TaskReviewException e) {
        log.warn("任务审核异常: {}", e.getMessage());
        return ResultData.fail(ErrorCode.PUBLISHER_TASK_REVIEW_STATUS_INVALID, e.getMessage());
    }

    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleFileUploadException(FileUploadException e) {
        log.warn("文件上传异常: {}", e.getMessage());
        return ResultData.fail(ErrorCode.OSS_FILE_UPLOAD_FAILED, e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleBusinessException(BusinessException e) {
        log.warn("发布者业务异常: {}", e.getMessage());
        return ResultData.fail(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(com.origin.banyu.publisher.exception.TaskAlreadyCompletedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleTaskAlreadyCompletedException(com.origin.banyu.publisher.exception.TaskAlreadyCompletedException e) {
        log.warn("任务已完成: {}", e.getMessage());
        return ResultData.fail(ErrorCode.PUBLISHER_TASK_ALREADY_COMPLETED, e.getMessage());
    }

    @ExceptionHandler(com.origin.banyu.publisher.exception.TaskExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleTaskExpiredException(com.origin.banyu.publisher.exception.TaskExpiredException e) {
        log.warn("任务已过期: {}", e.getMessage());
        return ResultData.fail(ErrorCode.PUBLISHER_TASK_EXPIRED, e.getMessage());
    }

    @ExceptionHandler(com.origin.banyu.publisher.exception.TaskConfigException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleTaskConfigException(com.origin.banyu.publisher.exception.TaskConfigException e) {
        log.warn("任务配置错误: {}", e.getMessage());
        return ResultData.fail(ErrorCode.PUBLISHER_TASK_CONFIG_ERROR, e.getMessage());
    }

    @ExceptionHandler(com.origin.banyu.publisher.exception.TaskValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleTaskValidationException(com.origin.banyu.publisher.exception.TaskValidationException e) {
        log.warn("任务验证失败: {}", e.getMessage());
        return ResultData.fail(ErrorCode.PUBLISHER_TASK_VALIDATION_FAILED, e.getMessage());
    }

    @ExceptionHandler(com.origin.banyu.publisher.exception.TaskPublishException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultData<Object> handleTaskPublishException(com.origin.banyu.publisher.exception.TaskPublishException e) {
        log.error("任务发布失败: {}", e.getMessage());
        return ResultData.fail(ErrorCode.PUBLISHER_TASK_PUBLISH_FAILED, e.getMessage());
    }

    @ExceptionHandler(com.origin.banyu.publisher.exception.TaskUnpublishException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultData<Object> handleTaskUnpublishException(com.origin.banyu.publisher.exception.TaskUnpublishException e) {
        log.error("任务下架失败: {}", e.getMessage());
        return ResultData.fail(ErrorCode.PUBLISHER_TASK_UNPUBLISH_FAILED, e.getMessage());
    }

    @ExceptionHandler(com.origin.banyu.publisher.exception.TaskDeleteException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultData<Object> handleTaskDeleteException(com.origin.banyu.publisher.exception.TaskDeleteException e) {
        log.error("任务删除失败: {}", e.getMessage());
        return ResultData.fail(ErrorCode.PUBLISHER_TASK_DELETE_FAILED, e.getMessage());
    }

    @ExceptionHandler(com.origin.banyu.publisher.exception.TaskDuplicateSubmissionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Object> handleTaskDuplicateSubmissionException(com.origin.banyu.publisher.exception.TaskDuplicateSubmissionException e) {
        log.warn("任务重复提交: {}", e.getMessage());
        return ResultData.fail(ErrorCode.PUBLISHER_TASK_DUPLICATE_SUBMISSION, e.getMessage());
    }

    @ExceptionHandler(com.origin.banyu.publisher.exception.TaskCompletionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultData<Object> handleTaskCompletionNotFoundException(com.origin.banyu.publisher.exception.TaskCompletionNotFoundException e) {
        log.warn("任务完成记录不存在: {}", e.getMessage());
        return ResultData.fail(ErrorCode.PUBLISHER_TASK_COMPLETION_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(com.origin.banyu.publisher.exception.ExcelExportException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultData<Object> handleExcelExportException(com.origin.banyu.publisher.exception.ExcelExportException e) {
        log.error("Excel导出异常: {}", e.getMessage());
        return ResultData.fail(ErrorCode.CORE_SERVICE_ERROR, e.getMessage());
    }
}
*/ 