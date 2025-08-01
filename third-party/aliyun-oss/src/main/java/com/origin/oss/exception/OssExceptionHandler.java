package com.origin.oss.exception;

import com.origin.common.dto.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * OSS异常处理器
 * 
 * @author scccy
 * @since 2025-01-27
 */
@Slf4j
@RestControllerAdvice
public class OssExceptionHandler {

    /**
     * 处理文件大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResultData<String>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.error("文件大小超过限制", e);
        return ResponseEntity.badRequest()
            .body(ResultData.fail("文件大小超过限制，最大支持10MB"));
    }

    /**
     * 处理文件类型不允许异常
     */
    @ExceptionHandler(FileTypeNotAllowedException.class)
    public ResponseEntity<ResultData<String>> handleFileTypeNotAllowed(FileTypeNotAllowedException e) {
        log.error("文件类型不允许", e);
        return ResponseEntity.badRequest()
            .body(ResultData.fail("文件类型不允许，仅支持图片、PDF、文档等格式"));
    }

    /**
     * 处理文件大小超限异常
     */
    @ExceptionHandler(FileSizeExceededException.class)
    public ResponseEntity<ResultData<String>> handleFileSizeExceeded(FileSizeExceededException e) {
        log.error("文件大小超过限制", e);
        return ResponseEntity.badRequest()
            .body(ResultData.fail("文件大小超过限制，最大支持10MB"));
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResultData<String>> handleUnauthorized(UnauthorizedException e) {
        log.error("权限不足", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ResultData.fail("权限不足，无法执行此操作"));
    }

    /**
     * 处理文件不存在异常
     */
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ResultData<String>> handleFileNotFound(FileNotFoundException e) {
        log.error("文件不存在", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ResultData.fail("文件不存在"));
    }

    /**
     * 处理OSS服务异常
     */
    @ExceptionHandler(OssServiceException.class)
    public ResponseEntity<ResultData<String>> handleOssServiceException(OssServiceException e) {
        log.error("OSS服务异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResultData.fail("文件服务异常，请稍后重试"));
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultData<String>> handleGenericException(Exception e) {
        log.error("OSS服务通用异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResultData.fail("服务异常，请稍后重试"));
    }
}

/**
 * 文件类型不允许异常
 */
class FileTypeNotAllowedException extends RuntimeException {
    public FileTypeNotAllowedException(String message) {
        super(message);
    }
}

/**
 * 文件大小超限异常
 */
class FileSizeExceededException extends RuntimeException {
    public FileSizeExceededException(String message) {
        super(message);
    }
}

/**
 * 权限不足异常
 */
class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

/**
 * 文件不存在异常
 */
class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(String message) {
        super(message);
    }
}

/**
 * OSS服务异常
 */
class OssServiceException extends RuntimeException {
    public OssServiceException(String message) {
        super(message);
    }

    public OssServiceException(String message, Throwable cause) {
        super(message, cause);
    }
} 