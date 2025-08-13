package com.origin.banyu.aliyunOss.exception;

/**
 * 文件大小超限异常
 */
public class FileSizeExceededException extends RuntimeException {
    public FileSizeExceededException(String message) {
        super(message);
    }
}
