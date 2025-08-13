package com.origin.banyu.wechatWork.exception;

/**
 * 企业微信服务异常
 * 符合异常处理规范：定义业务异常类，包含错误码
 * 
 * @author scccy
 */
public class WechatWorkServiceException extends RuntimeException {
    
    private final String errorCode;
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public WechatWorkServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误码
     * @param message 错误信息
     * @param cause 原因异常
     */
    public WechatWorkServiceException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 获取完整错误信息
     * 
     * @return 包含错误码的完整错误信息
     */
    public String getFullMessage() {
        return String.format("[%s] %s", errorCode, getMessage());
    }
} 