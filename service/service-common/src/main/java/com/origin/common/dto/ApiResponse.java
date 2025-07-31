package com.origin.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.origin.common.entity.ErrorCode;

/**
 * API响应包装类 - 组合响应数据和请求追踪信息
 * 遵循组合优于继承原则，通过组合实现功能
 * 
 * @author origin
 * @since 2025-07-31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ApiResponse<T> {
    
    /**
     * 响应数据
     */
    private ResultData<T> result;
    
    /**
     * 请求追踪信息
     */
    private RequestTrace trace;
    
    // ==================== 成功响应方法 ====================
    
    /**
     * 成功响应
     * 
     * @param <T> 数据类型
     * @return API响应结果
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<T>()
            .setResult(ResultData.success())
            .setTrace(RequestTrace.create());
    }
    
    /**
     * 成功响应（带数据）
     * 
     * @param <T> 数据类型
     * @param data 响应数据
     * @return API响应结果
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>()
            .setResult(ResultData.success(data))
            .setTrace(RequestTrace.create());
    }
    
    /**
     * 成功响应（带消息和数据）
     * 
     * @param <T> 数据类型
     * @param message 响应消息
     * @param data 响应数据
     * @return API响应结果
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<T>()
            .setResult(ResultData.success(message, data))
            .setTrace(RequestTrace.create());
    }
    
    /**
     * 成功响应（带追踪信息）
     * 
     * @param <T> 数据类型
     * @param message 响应消息
     * @param data 响应数据
     * @param trace 请求追踪信息
     * @return API响应结果
     */
    public static <T> ApiResponse<T> successWithTrace(String message, T data, RequestTrace trace) {
        return new ApiResponse<T>()
            .setResult(ResultData.success(message, data))
            .setTrace(trace);
    }
    
    /**
     * 成功响应（带追踪信息）
     * 
     * @param <T> 数据类型
     * @param data 响应数据
     * @param trace 请求追踪信息
     * @return API响应结果
     */
    public static <T> ApiResponse<T> successWithTrace(T data, RequestTrace trace) {
        return new ApiResponse<T>()
            .setResult(ResultData.success(data))
            .setTrace(trace);
    }

    // ==================== 失败响应方法 ====================
    
    /**
     * 失败响应
     * 
     * @param <T> 数据类型
     * @return API响应结果
     */
    public static <T> ApiResponse<T> fail() {
        return new ApiResponse<T>()
            .setResult(ResultData.fail())
            .setTrace(RequestTrace.create());
    }
    
    /**
     * 失败响应（带消息）
     * 
     * @param <T> 数据类型
     * @param message 错误消息
     * @return API响应结果
     */
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<T>()
            .setResult(ResultData.fail(message))
            .setTrace(RequestTrace.create());
    }
    
    /**
     * 失败响应（带码和消息）
     * 
     * @param <T> 数据类型
     * @param code 错误码
     * @param message 错误消息
     * @return API响应结果
     */
    public static <T> ApiResponse<T> fail(Integer code, String message) {
        return new ApiResponse<T>()
            .setResult(ResultData.fail(code, message))
            .setTrace(RequestTrace.create());
    }
    
    /**
     * 失败响应（使用ErrorCode）
     * 
     * @param <T> 数据类型
     * @param errorCode 错误码枚举
     * @return API响应结果
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<T>()
            .setResult(ResultData.fail(errorCode))
            .setTrace(RequestTrace.create());
    }
    
    /**
     * 失败响应（使用ErrorCode和自定义消息）
     * 
     * @param <T> 数据类型
     * @param errorCode 错误码枚举
     * @param message 自定义错误消息
     * @return API响应结果
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String message) {
        return new ApiResponse<T>()
            .setResult(ResultData.fail(errorCode, message))
            .setTrace(RequestTrace.create());
    }
    
    /**
     * 失败响应（带追踪信息）
     * 
     * @param <T> 数据类型
     * @param message 错误消息
     * @param trace 请求追踪信息
     * @return API响应结果
     */
    public static <T> ApiResponse<T> fail(String message, RequestTrace trace) {
        return new ApiResponse<T>()
            .setResult(ResultData.fail(message))
            .setTrace(trace);
    }
    
    /**
     * 失败响应（带追踪信息）
     * 
     * @param <T> 数据类型
     * @param errorCode 错误码枚举
     * @param message 自定义错误消息
     * @param trace 请求追踪信息
     * @return API响应结果
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String message, RequestTrace trace) {
        return new ApiResponse<T>()
            .setResult(ResultData.fail(errorCode, message))
            .setTrace(trace);
    }

    // ==================== 兼容性方法 ====================
    
    /**
     * 兼容旧版本的ok方法
     * 
     * @param <T> 数据类型
     * @return API响应结果
     */
    public static <T> ApiResponse<T> ok() {
        return success();
    }
    
    /**
     * 兼容旧版本的ok方法（带消息和数据）
     * 
     * @param <T> 数据类型
     * @param message 响应消息
     * @param data 响应数据
     * @return API响应结果
     */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return success(message, data);
    }

    /**
     * 兼容旧版本的ok方法（带消息）
     * 
     * @param <T> 数据类型
     * @param message 响应消息
     * @return API响应结果
     */
    public static <T> ApiResponse<T> ok(String message) {
        return success(message, null);
    }

    // ==================== 工具方法 ====================
    
    /**
     * 判断响应是否成功
     * 
     * @return true 如果响应成功，false 否则
     */
    public boolean isSuccess() {
        return this.result != null && this.result.isSuccess();
    }

    /**
     * 判断响应是否失败
     * 
     * @return true 如果响应失败，false 否则
     */
    public boolean isFail() {
        return !isSuccess();
    }

    /**
     * 获取响应数据
     * 
     * @return 响应数据
     */
    public T getData() {
        return this.result != null ? this.result.getData() : null;
    }

    /**
     * 获取响应码
     * 
     * @return 响应码
     */
    public Integer getCode() {
        return this.result != null ? this.result.getCode() : null;
    }

    /**
     * 获取响应消息
     * 
     * @return 响应消息
     */
    public String getMessage() {
        return this.result != null ? this.result.getMessage() : null;
    }

    /**
     * 获取请求ID
     * 
     * @return 请求ID
     */
    public String getRequestId() {
        return this.trace != null ? this.trace.getRequestId() : null;
    }

    /**
     * 获取用户ID
     * 
     * @return 用户ID
     */
    public Long getUserId() {
        return this.trace != null ? this.trace.getUserId() : null;
    }

    /**
     * 获取处理耗时
     * 
     * @return 处理耗时（毫秒）
     */
    public Long getDuration() {
        return this.trace != null ? this.trace.getDuration() : null;
    }

    /**
     * 完成请求追踪
     * 
     * @param responseStatus 响应状态码
     * @return 当前实例
     */
    public ApiResponse<T> completeTrace(Integer responseStatus) {
        if (this.trace != null) {
            this.trace.complete(responseStatus);
        }
        return this;
    }

    /**
     * 设置请求追踪信息
     * 
     * @param trace 请求追踪信息
     * @return 当前实例
     */
    public ApiResponse<T> setTrace(RequestTrace trace) {
        this.trace = trace;
        return this;
    }

    /**
     * 设置请求追踪基本信息
     * 
     * @param requestId 请求ID
     * @param userId 用户ID
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     * @return 当前实例
     */
    public ApiResponse<T> setTraceInfo(String requestId, Long userId, String clientIp, String userAgent) {
        if (this.trace == null) {
            this.trace = RequestTrace.create(requestId, userId, clientIp, userAgent);
        } else {
            this.trace.setRequestId(requestId)
                     .setUserId(userId)
                     .setClientIp(clientIp)
                     .setUserAgent(userAgent);
        }
        return this;
    }
} 