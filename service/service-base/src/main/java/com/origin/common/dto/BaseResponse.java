package com.origin.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 基础响应DTO
 * 
 * @author origin
 * @since 2024-12-19
 */
@Data
@Accessors(chain = true)
public class BaseResponse {
    
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private Object data;
    
    /**
     * 请求ID，用于链路追踪
     */
    private String requestId;
    
    /**
     * 响应时间戳
     */
    private Long timestamp = System.currentTimeMillis();
    
    /**
     * 成功响应
     */
    public static BaseResponse success() {
        return new BaseResponse()
            .setCode(200)
            .setMessage("SUCCESS");
    }
    
    /**
     * 成功响应（带数据）
     */
    public static BaseResponse success(Object data) {
        return new BaseResponse()
            .setCode(200)
            .setMessage("SUCCESS")
            .setData(data);
    }
    
    /**
     * 成功响应（带消息和数据）
     */
    public static BaseResponse success(String message, Object data) {
        return new BaseResponse()
            .setCode(200)
            .setMessage(message)
            .setData(data);
    }
    
    /**
     * 失败响应
     */
    public static BaseResponse fail() {
        return new BaseResponse()
            .setCode(500)
            .setMessage("FAIL");
    }
    
    /**
     * 失败响应（带消息）
     */
    public static BaseResponse fail(String message) {
        return new BaseResponse()
            .setCode(500)
            .setMessage(message);
    }
    
    /**
     * 失败响应（带码和消息）
     */
    public static BaseResponse fail(Integer code, String message) {
        return new BaseResponse()
            .setCode(code)
            .setMessage(message);
    }
    
    /**
     * 失败响应（带码、消息和数据）
     */
    public static BaseResponse fail(Integer code, String message, Object data) {
        return new BaseResponse()
            .setCode(code)
            .setMessage(message)
            .setData(data);
    }
} 