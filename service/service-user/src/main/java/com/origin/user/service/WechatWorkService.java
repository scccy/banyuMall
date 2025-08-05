package com.origin.user.service;

import com.origin.common.dto.WechatWorkAccessTokenResponse;
import com.origin.common.entity.ThirdPartyConfig;
import com.origin.user.feign.AuthFeignClient;
import com.origin.user.feign.WechatWorkApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 企业微信服务类
 * 展示如何使用common模块中的返回结果类和Feign客户端
 * 
 * @author scccy
 * @since 2025-08-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatWorkService {

    private final WechatWorkApiClient wechatWorkApiClient;
    private final AuthFeignClient authFeignClient;

    /**
     * 获取企业微信AccessToken
     * 
     * @param platformType 平台类型
     * @return AccessToken响应
     */
    public WechatWorkAccessTokenResponse getAccessToken(Integer platformType) {
        try {
            // 1. 从配置服务获取企业微信配置
            var configResult = authFeignClient.getConfigByPlatformType(platformType);
            if (!configResult.isSuccess()) {
                log.error("获取第三方平台配置失败：{}", configResult.getMessage());
                return WechatWorkAccessTokenResponse.fail(-1, "获取平台配置失败");
            }

            ThirdPartyConfig config = configResult.getData();
            if (config == null) {
                log.error("未找到平台类型为 {} 的配置", platformType);
                return WechatWorkAccessTokenResponse.fail(-1, "未找到平台配置");
            }

            // 2. 调用企业微信API获取AccessToken
            WechatWorkAccessTokenResponse response = wechatWorkApiClient.getAccessToken(
                    config.getCorpId(), 
                    config.getCorpSecret()
            );

            if (response.isSuccess()) {
                log.info("成功获取企业微信AccessToken，平台类型：{}", platformType);
                // 3. 可以在这里更新数据库中的access_token
                // updateAccessTokenInDatabase(config.getConfigId(), response.getAccessToken());
            } else {
                log.error("获取企业微信AccessToken失败，平台类型：{}，错误：{}", 
                        platformType, response.getErrmsg());
            }

            return response;

        } catch (Exception e) {
            log.error("获取企业微信AccessToken异常，平台类型：{}", platformType, e);
            return WechatWorkAccessTokenResponse.fail(-1, "获取AccessToken异常：" + e.getMessage());
        }
    }

    /**
     * 使用应用Secret获取企业微信AccessToken
     * 
     * @param platformType 平台类型
     * @return AccessToken响应
     */
    public WechatWorkAccessTokenResponse getAccessTokenByAgent(Integer platformType) {
        try {
            // 1. 从配置服务获取企业微信配置
            var configResult = authFeignClient.getConfigByPlatformType(platformType);
            if (!configResult.isSuccess()) {
                log.error("获取第三方平台配置失败：{}", configResult.getMessage());
                return WechatWorkAccessTokenResponse.fail(-1, "获取平台配置失败");
            }

            ThirdPartyConfig config = configResult.getData();
            if (config == null) {
                log.error("未找到平台类型为 {} 的配置", platformType);
                return WechatWorkAccessTokenResponse.fail(-1, "未找到平台配置");
            }

            // 2. 调用企业微信API获取AccessToken（使用应用Secret）
            WechatWorkAccessTokenResponse response = wechatWorkApiClient.getAccessTokenByAgent(
                    config.getCorpId(), 
                    config.getAppId(), 
                    config.getAppSecret()
            );

            if (response.isSuccess()) {
                log.info("成功获取企业微信AccessToken，平台类型：{}", platformType);
            } else {
                log.error("获取企业微信AccessToken失败，平台类型：{}，错误：{}", 
                        platformType, response.getErrmsg());
            }

            return response;

        } catch (Exception e) {
            log.error("获取企业微信AccessToken异常，平台类型：{}", platformType, e);
            return WechatWorkAccessTokenResponse.fail(-1, "获取AccessToken异常：" + e.getMessage());
        }
    }

    /**
     * 验证AccessToken是否有效
     * 
     * @param platformType 平台类型
     * @return true-有效，false-无效
     */
    public boolean validateAccessToken(Integer platformType) {
        WechatWorkAccessTokenResponse response = getAccessToken(platformType);
        return response.isAccessTokenValid();
    }
} 