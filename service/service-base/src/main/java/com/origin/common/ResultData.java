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
public class ResultData {
    private Integer code;
    private String msg;
    private Object data;

    public static ResultData ok() {
        ResultData resultData = new ResultData();
        resultData.setCode(200);
        resultData.setMsg("SUCCESS");
        return resultData;
    }
    public static ResultData ok(String mes,Object data) {
        ResultData resultData = new ResultData();
        resultData.setCode(200);
        resultData.setMsg(mes);
        resultData.setData(data);
        return resultData;
    }

    public static ResultData ok(String mes) {
        ResultData resultData = new ResultData();
        resultData.setCode(200);
        resultData.setMsg(mes);
        return resultData;
    }

    public static ResultData fail() {
        ResultData resultData = new ResultData();
        resultData.setCode(500);
        resultData.setMsg("FAIL");
        return resultData;
    }
    public static ResultData fail(String code,String msg,Object data) {
        ResultData resultData = new ResultData();
        resultData.setCode(Integer.valueOf(code));
        resultData.setMsg(msg);
        resultData.setData(data);
        return resultData;
    }
    
    /**
     * 失败响应（使用Integer错误码和消息）
     */
    public static ResultData fail(Integer code, String message) {
        ResultData resultData = new ResultData();
        resultData.setCode(code);
        resultData.setMsg(message);
        return resultData;
    }
    
    /**
     * 失败响应（使用ErrorCode）
     */
    public static ResultData fail(ErrorCode errorCode) {
        ResultData resultData = new ResultData();
        resultData.setCode(errorCode.getCode());
        resultData.setMsg(errorCode.getMessage());
        return resultData;
    }
    
    /**
     * 失败响应（使用ErrorCode和自定义消息）
     */
    public static ResultData fail(ErrorCode errorCode, String message) {
        ResultData resultData = new ResultData();
        resultData.setCode(errorCode.getCode());
        resultData.setMsg(message);
        return resultData;
    }
    
    /**
     * 失败响应（使用ErrorCode、自定义消息和数据）
     */
    public static ResultData fail(ErrorCode errorCode, String message, Object data) {
        ResultData resultData = new ResultData();
        resultData.setCode(errorCode.getCode());
        resultData.setMsg(message);
        resultData.setData(data);
        return resultData;
    }
    public static ResultData fail(String message) {
        ResultData resultData = new ResultData();
        resultData.setCode(500);
        resultData.setMsg(message);
        return resultData;
    }


}
