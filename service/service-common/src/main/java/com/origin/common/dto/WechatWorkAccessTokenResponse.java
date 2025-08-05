package com.origin.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业微信AccessToken响应类
 * 用于处理企业微信获取access_token的响应
 * 
 * @author scccy
 * @since 2025-08-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatWorkAccessTokenResponse extends WechatWorkApiResponse<WechatWorkAccessTokenResponse.AccessTokenData> {

    /**
     * AccessToken数据
     */
    @Data
    public static class AccessTokenData {
        /**
         * 访问令牌
         */
        private String access_token;

        /**
         * 过期时间（秒）
         */
        private Integer expires_in;
    }

    /**
     * 获取访问令牌
     * 
     * @return 访问令牌
     */
    public String getAccessToken() {
        return getData() != null ? getData().getAccess_token() : null;
    }

    /**
     * 获取过期时间
     * 
     * @return 过期时间（秒）
     */
    public Integer getExpiresIn() {
        return getData() != null ? getData().getExpires_in() : null;
    }

    /**
     * 判断AccessToken是否有效
     * 
     * @return true-有效，false-无效
     */
    public boolean isAccessTokenValid() {
        return isSuccess() && getAccessToken() != null && !getAccessToken().isEmpty();
    }

    /**
     * 创建成功响应
     * 
     * @param accessToken 访问令牌
     * @param expiresIn 过期时间
     * @return 成功响应
     */
    public static WechatWorkAccessTokenResponse success(String accessToken, Integer expiresIn) {
        WechatWorkAccessTokenResponse response = new WechatWorkAccessTokenResponse();
        response.setErrcode(0);
        response.setErrmsg("ok");
        
        AccessTokenData data = new AccessTokenData();
        data.setAccess_token(accessToken);
        data.setExpires_in(expiresIn);
        response.setData(data);
        
        return response;
    }

    /**
     * 创建失败响应
     * 
     * @param errcode 错误码
     * @param errmsg 错误信息
     * @return 失败响应
     */
    public static WechatWorkAccessTokenResponse fail(Integer errcode, String errmsg) {
        WechatWorkAccessTokenResponse response = new WechatWorkAccessTokenResponse();
        response.setErrcode(errcode);
        response.setErrmsg(errmsg);
        return response;
    }
} 