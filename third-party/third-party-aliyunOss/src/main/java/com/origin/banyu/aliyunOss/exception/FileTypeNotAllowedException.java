package com.origin.banyu.aliyunOss.exception;

/**
 * 文件类型不允许异常
 */
public class FileTypeNotAllowedException extends RuntimeException {
    public FileTypeNotAllowedException(String message) {
        super(message);
    }
}
