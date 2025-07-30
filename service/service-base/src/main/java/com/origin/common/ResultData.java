package com.origin.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.origin.common.exception.ErrorCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ResultData<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> ResultData<T> ok() {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(200);
        resultData.setMsg("SUCCESS");
        return resultData;
    }
    
    public static <T> ResultData<T> ok(String mes, T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(200);
        resultData.setMsg(mes);
        resultData.setData(data);
        return resultData;
    }

    public static <T> ResultData<T> ok(String mes) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(200);
        resultData.setMsg(mes);
        return resultData;
    }

    public static <T> ResultData<T> fail() {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(500);
        resultData.setMsg("FAIL");
        return resultData;
    }
    
    public static <T> ResultData<T> fail(String code, String msg, T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(Integer.valueOf(code));
        resultData.setMsg(msg);
        resultData.setData(data);
        return resultData;
    }
    
    /**
     * 失败响应（使用Integer错误码和消息）
     */
    public static <T> ResultData<T> fail(Integer code, String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(code);
        resultData.setMsg(message);
        return resultData;
    }
    
    /**
     * 失败响应（使用ErrorCode）
     */
    public static <T> ResultData<T> fail(ErrorCode errorCode) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(errorCode.getCode());
        resultData.setMsg(errorCode.getMessage());
        return resultData;
    }
    
    /**
     * 失败响应（使用ErrorCode和自定义消息）
     */
    public static <T> ResultData<T> fail(ErrorCode errorCode, String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(errorCode.getCode());
        resultData.setMsg(message);
        return resultData;
    }
    
    /**
     * 失败响应（使用ErrorCode、自定义消息和数据）
     */
    public static <T> ResultData<T> fail(ErrorCode errorCode, String message, T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(errorCode.getCode());
        resultData.setMsg(message);
        resultData.setData(data);
        return resultData;
    }
    
    public static <T> ResultData<T> fail(String message) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setCode(500);
        resultData.setMsg(message);
        return resultData;
    }

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
     * 获取响应数据
     *
     * @return 响应数据
     */
    public T getData() {
        return this.data;
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
        if (this.data != null && clazz.isInstance(this.data)) {
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
        return this.msg != null && !this.msg.trim().isEmpty() ? this.msg : defaultMessage;
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
