package com.origin.banyu.aliyunOss.exception;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * OSS异常处理器
 * 
 * @author scccy
 * @since 2025-07-31
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
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResultData<String>> handleBusinessException(BusinessException e) {
        log.error("业务异常: code={}, message={}", e.getErrorCode().getCode(), e.getMessage(), e);
        
        // 根据错误码判断HTTP状态码
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        if (e.getErrorCode().getCode() >= 5000) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        return ResponseEntity.status(httpStatus)
            .body(ResultData.fail(e.getErrorCode().getCode(), e.getMessage()));
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

    // 400 - 参数缺失
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResultData<String>> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("请求参数缺失: {}", e.getParameterName());
        return ResponseEntity.badRequest().body(ResultData.fail("请求参数缺失: " + e.getParameterName()));
    }

    // 400 - 文件表单项缺失
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ResultData<String>> handleMissingPart(MissingServletRequestPartException e) {
        log.warn("请求文件缺失: {}", e.getRequestPartName());
        return ResponseEntity.badRequest().body(ResultData.fail("请求文件缺失: " + e.getRequestPartName()));
    }

    // 400 - Bean 校验或方法参数校验失败
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class})
    public ResponseEntity<ResultData<String>> handleValidationException(Exception e) {
        log.warn("参数校验失败", e);
        return ResponseEntity.badRequest().body(ResultData.fail("请求参数不合法"));
    }

    // 400 - 参数类型不匹配
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResultData<String>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型不匹配: {}", e.getName());
        return ResponseEntity.badRequest().body(ResultData.fail("参数类型不匹配: " + e.getName()));
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

