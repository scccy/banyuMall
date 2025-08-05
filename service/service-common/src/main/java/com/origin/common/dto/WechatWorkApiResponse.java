package com.origin.common.dto;

import lombok.Data;

/**
 * 企业微信API响应顶级父类
 * 用于处理企业微信API的统一响应格式
 * 
 * @author scccy
 * @since 2025-08-05
 */
@Data
public class WechatWorkApiResponse<T> {

    /**
     * 错误码，0表示成功
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 判断是否成功
     * 
     * @return true-成功，false-失败
     */
    public boolean isSuccess() {
        return errcode != null && errcode == 0;
    }

    /**
     * 判断是否失败
     * 
     * @return true-失败，false-成功
     */
    public boolean isFailed() {
        return !isSuccess();
    }

    /**
     * 获取错误信息，如果成功则返回null
     * 
     * @return 错误信息
     */
    public String getErrorMessage() {
        return isSuccess() ? null : errmsg;
    }

    /**
     * 创建成功响应
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> WechatWorkApiResponse<T> success(T data) {
        WechatWorkApiResponse<T> response = new WechatWorkApiResponse<>();
        response.setErrcode(0);
        response.setErrmsg("ok");
        response.setData(data);
        return response;
    }

    /**
     * 创建失败响应
     * 
     * @param errcode 错误码
     * @param errmsg 错误信息
     * @param <T> 数据类型
     * @return 失败响应
     */
    public static <T> WechatWorkApiResponse<T> fail(Integer errcode, String errmsg) {
        WechatWorkApiResponse<T> response = new WechatWorkApiResponse<>();
        response.setErrcode(errcode);
        response.setErrmsg(errmsg);
        return response;
    }

    /**
     * 创建失败响应
     * 
     * @param errcode 错误码
     * @param <T> 数据类型
     * @return 失败响应
     */
    public static <T> WechatWorkApiResponse<T> fail(Integer errcode) {
        return fail(errcode, "未知错误");
    }
} 