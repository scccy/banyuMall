package com.origin.banyu.aliyunOss.exception;

/**
 * OSS服务异常
 */
public class OssServiceException extends RuntimeException {
    public OssServiceException(String message) {
        super(message);
    }

    public OssServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
