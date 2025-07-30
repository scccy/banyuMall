package com.origin.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 基础请求DTO
 * 
 * @author origin
 * @since 2024-12-19
 */
@Data
@Accessors(chain = true)
public class BaseRequest {
    
    /**
     * 请求ID，用于链路追踪
     */
    private String requestId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户Token
     */
    private String token;
    
    /**
     * 客户端IP
     */
    private String clientIp;
    
    /**
     * 用户代理
     */
    private String userAgent;
    
    /**
     * 请求时间戳
     */
    private Long timestamp = System.currentTimeMillis();
} 