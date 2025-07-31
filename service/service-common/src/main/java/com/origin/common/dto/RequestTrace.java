package com.origin.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 请求追踪信息类 - 用于链路追踪和请求上下文管理
 * 遵循单一职责原则，只负责请求追踪相关功能
 * 
 * @author origin
 * @since 2025-07-31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RequestTrace {
    
    /**
     * 请求ID，用于链路追踪
     */
    private String requestId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
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
    private Long requestTimestamp = System.currentTimeMillis();
    
    /**
     * 响应时间戳
     */
    private Long responseTimestamp;
    
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 请求路径
     */
    private String requestPath;
    
    /**
     * 请求方法
     */
    private String requestMethod;
    
    /**
     * 请求参数
     */
    private String requestParams;
    
    /**
     * 响应状态码
     */
    private Integer responseStatus;
    
    /**
     * 处理耗时（毫秒）
     */
    private Long duration;
    
    // ==================== 构造方法 ====================
    
    /**
     * 创建请求追踪实例
     * 
     * @return 请求追踪实例
     */
    public static RequestTrace create() {
        return new RequestTrace()
            .setRequestTimestamp(System.currentTimeMillis());
    }
    
    /**
     * 创建请求追踪实例（带请求ID）
     * 
     * @param requestId 请求ID
     * @return 请求追踪实例
     */
    public static RequestTrace create(String requestId) {
        return new RequestTrace()
            .setRequestId(requestId)
            .setRequestTimestamp(System.currentTimeMillis());
    }
    
    /**
     * 创建请求追踪实例（带基本信息）
     * 
     * @param requestId 请求ID
     * @param userId 用户ID
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     * @return 请求追踪实例
     */
    public static RequestTrace create(String requestId, Long userId, String clientIp, String userAgent) {
        return new RequestTrace()
            .setRequestId(requestId)
            .setUserId(userId)
            .setClientIp(clientIp)
            .setUserAgent(userAgent)
            .setRequestTimestamp(System.currentTimeMillis());
    }
    
    // ==================== 工具方法 ====================
    
    /**
     * 完成请求追踪
     * 
     * @param responseStatus 响应状态码
     * @return 当前实例
     */
    public RequestTrace complete(Integer responseStatus) {
        this.responseTimestamp = System.currentTimeMillis();
        this.responseStatus = responseStatus;
        this.duration = this.responseTimestamp - this.requestTimestamp;
        return this;
    }
    
    /**
     * 设置请求信息
     * 
     * @param serviceName 服务名称
     * @param requestPath 请求路径
     * @param requestMethod 请求方法
     * @param requestParams 请求参数
     * @return 当前实例
     */
    public RequestTrace setRequestInfo(String serviceName, String requestPath, String requestMethod, String requestParams) {
        this.serviceName = serviceName;
        this.requestPath = requestPath;
        this.requestMethod = requestMethod;
        this.requestParams = requestParams;
        return this;
    }
    
    /**
     * 获取处理耗时（毫秒）
     * 
     * @return 处理耗时，如果请求未完成返回null
     */
    public Long getDuration() {
        if (this.responseTimestamp != null && this.requestTimestamp != null) {
            return this.responseTimestamp - this.requestTimestamp;
        }
        return null;
    }
    
    /**
     * 判断请求是否完成
     * 
     * @return true 如果请求已完成，false 否则
     */
    public boolean isCompleted() {
        return this.responseTimestamp != null;
    }
    
    /**
     * 获取请求追踪摘要
     * 
     * @return 追踪摘要信息
     */
    public String getSummary() {
        return String.format("Request[%s] %s %s - %dms", 
            requestId, requestMethod, requestPath, getDuration());
    }
} 