package com.origin.user.feign;

import com.origin.common.dto.WechatWorkAccessTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 企业微信API客户端降级处理
 * 
 * @author scccy
 * @since 2025-08-05
 */
@Slf4j
@Component
public class WechatWorkApiClientFallback implements WechatWorkApiClient {

    @Override
    public WechatWorkAccessTokenResponse getAccessToken(String corpid, String corpsecret) {
        log.error("获取企业微信AccessToken失败，企业ID：{}，服务降级", corpid);
        return WechatWorkAccessTokenResponse.fail(-1, "获取企业微信AccessToken失败，服务暂时不可用");
    }

    @Override
    public WechatWorkAccessTokenResponse getAccessTokenByAgent(String corpid, String agentid, String secret) {
        log.error("获取企业微信AccessToken失败，企业ID：{}，应用ID：{}，服务降级", corpid, agentid);
        return WechatWorkAccessTokenResponse.fail(-1, "获取企业微信AccessToken失败，服务暂时不可用");
    }
} 