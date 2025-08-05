package com.origin.user.feign;

import com.origin.common.dto.WechatWorkAccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 企业微信API客户端
 * 用于调用企业微信相关API
 * 
 * @author scccy
 * @since 2025-08-05
 */
@FeignClient(name = "wechatwork-api", url = "https://qyapi.weixin.qq.com", fallback = WechatWorkApiClientFallback.class)
public interface WechatWorkApiClient {

    /**
     * 获取企业微信AccessToken
     * 
     * @param corpid 企业ID
     * @param corpsecret 应用的凭证密钥
     * @return AccessToken响应
     */
    @GetMapping("/cgi-bin/gettoken")
    WechatWorkAccessTokenResponse getAccessToken(
            @RequestParam("corpid") String corpid,
            @RequestParam("corpsecret") String corpsecret
    );

    /**
     * 获取企业微信AccessToken（使用应用Secret）
     * 
     * @param corpid 企业ID
     * @param agentid 应用ID
     * @param secret 应用Secret
     * @return AccessToken响应
     */
    @GetMapping("/cgi-bin/gettoken")
    WechatWorkAccessTokenResponse getAccessTokenByAgent(
            @RequestParam("corpid") String corpid,
            @RequestParam("agentid") String agentid,
            @RequestParam("secret") String secret
    );
} 