package com.origin.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.origin.common.entity.ErrorCode;

/**
 * 统一响应数据类 - 核心响应数据结构
 * 遵循单一职责原则，只负责响应数据的管理
 * 
 * @author origin
 * @since 2024-12-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ResultData<T> {
    
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
    private T data;
    
    /**
     * 响应时间戳
     */
    private Long timestamp = System.currentTimeMillis();

    // ==================== 成功响应方法 ====================
    
    /**
     * 成功响应
     * 
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> ResultData<T> success() {
        return new ResultData<T>()
            .setCode(200)
            .setMessage("SUCCESS")
            .setTimestamp(System.currentTimeMillis());
    }
    
    /**
     * 成功响应（带数据）
     * 
     * @param <T> 数据类型
     * @param data 响应数据
     * @return 成功响应结果
     */
    public static <T> ResultData<T> success(T data) {
        return new ResultData<T>()
            .setCode(200)
            .setMessage("SUCCESS")
            .setData(data)
            .setTimestamp(System.currentTimeMillis());
    }
    
    /**
     * 成功响应（带消息和数据）
     * 
     * @param <T> 数据类型
     * @param message 响应消息
     * @param data 响应数据
     * @return 成功响应结果
     */
    public static <T> ResultData<T> success(String message, T data) {
        return new ResultData<T>()
            .setCode(200)
            .setMessage(message)
            .setData(data)
            .setTimestamp(System.currentTimeMillis());
    }

    // ==================== 失败响应方法 ====================
    
    /**
     * 失败响应
     * 
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> ResultData<T> fail() {
        return new ResultData<T>()
            .setCode(500)
            .setMessage("FAIL")
            .setTimestamp(System.currentTimeMillis());
    }
    
    /**
     * 失败响应（带消息）
     * 
     * @param <T> 数据类型
     * @param message 错误消息
     * @return 失败响应结果
     */
    public static <T> ResultData<T> fail(String message) {
        return new ResultData<T>()
            .setCode(500)
            .setMessage(message)
            .setTimestamp(System.currentTimeMillis());
    }
    
    /**
     * 失败响应（带码和消息）
     * 
     * @param <T> 数据类型
     * @param code 错误码
     * @param message 错误消息
     * @return 失败响应结果
     */
    public static <T> ResultData<T> fail(Integer code, String message) {
        return new ResultData<T>()
            .setCode(code)
            .setMessage(message)
            .setTimestamp(System.currentTimeMillis());
    }
    
    /**
     * 失败响应（带码、消息和数据）
     * 
     * @param <T> 数据类型
     * @param code 错误码
     * @param message 错误消息
     * @param data 错误数据
     * @return 失败响应结果
     */
    public static <T> ResultData<T> fail(Integer code, String message, T data) {
        return new ResultData<T>()
            .setCode(code)
            .setMessage(message)
            .setData(data)
            .setTimestamp(System.currentTimeMillis());
    }
    
    /**
     * 失败响应（使用ErrorCode）
     * 
     * @param <T> 数据类型
     * @param errorCode 错误码枚举
     * @return 失败响应结果
     */
    public static <T> ResultData<T> fail(ErrorCode errorCode) {
        return new ResultData<T>()
            .setCode(errorCode.getCode())
            .setMessage(errorCode.getMessage())
            .setTimestamp(System.currentTimeMillis());
    }
    
    /**
     * 失败响应（使用ErrorCode和自定义消息）
     * 
     * @param <T> 数据类型
     * @param errorCode 错误码枚举
     * @param message 自定义错误消息
     * @return 失败响应结果
     */
    public static <T> ResultData<T> fail(ErrorCode errorCode, String message) {
        return new ResultData<T>()
            .setCode(errorCode.getCode())
            .setMessage(message)
            .setTimestamp(System.currentTimeMillis());
    }
    
    /**
     * 失败响应（使用ErrorCode、自定义消息和数据）
     * 
     * @param <T> 数据类型
     * @param errorCode 错误码枚举
     * @param message 自定义错误消息
     * @param data 错误数据
     * @return 失败响应结果
     */
    public static <T> ResultData<T> fail(ErrorCode errorCode, String message, T data) {
        return new ResultData<T>()
            .setCode(errorCode.getCode())
            .setMessage(message)
            .setData(data)
            .setTimestamp(System.currentTimeMillis());
    }

    // ==================== 兼容性方法 ====================
    
    /**
     * 兼容旧版本的ok方法
     * 
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> ResultData<T> ok() {
        return success();
    }
    
    /**
     * 兼容旧版本的ok方法（带消息和数据）
     * 
     * @param <T> 数据类型
     * @param message 响应消息
     * @param data 响应数据
     * @return 成功响应结果
     */
    public static <T> ResultData<T> ok(String message, T data) {
        return success(message, data);
    }

    /**
     * 兼容旧版本的ok方法（带消息）
     * 
     * @param <T> 数据类型
     * @param message 响应消息
     * @return 成功响应结果
     */
    public static <T> ResultData<T> ok(String message) {
        return success(message, null);
    }

    // ==================== 工具方法 ====================
    
    /**
     * 判断响应是否成功
     *
     * @return true 如果响应成功，false 否则
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
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
     * 获取响应数据（类型转换方法）
     *
     * @param <R> 目标类型
     * @param clazz 目标类型Class
     * @return 转换后的数据，如果转换失败返回null
     */
    @SuppressWarnings("unchecked")
    public <R> R getData(Class<R> clazz) {
        if (clazz.isInstance(this.data)) {
            return (R) this.data;
        }
        return null;
    }

    /**
     * 获取响应消息，如果为空则返回默认消息
     *
     * @param defaultMessage 默认消息
     * @return 响应消息或默认消息
     */
    public String getMessageOrDefault(String defaultMessage) {
        return this.message != null && !this.message.trim().isEmpty() ? this.message : defaultMessage;
    }

    /**
     * 获取响应代码，如果为空则返回默认代码
     *
     * @param defaultCode 默认代码
     * @return 响应代码或默认代码
     */
    public Integer getCodeOrDefault(Integer defaultCode) {
        return this.code != null ? this.code : defaultCode;
    }
}
