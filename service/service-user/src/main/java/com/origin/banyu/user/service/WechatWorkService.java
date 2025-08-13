package com.origin.banyu.user.service;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.dto.WechatWorkAuthStatusResponse;
import com.origin.banyu.common.entity.ThirdPartyConfig;
import com.origin.banyu.user.feign.AuthFeignClient;
import com.origin.banyu.user.feign.WechatWorkApiClient;
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
    public ResultData<WechatWorkAuthStatusResponse> getAccessToken(Integer platformType) {
        try {
            // 1. 从配置服务获取企业微信配置
            var configResult = authFeignClient.getConfigByPlatformType(platformType);
            if (!(configResult.getCode() ==200)) {
                log.error("获取第三方平台配置失败：{}", configResult.getMessage());
                return ResultData.fail(-1, "获取平台配置失败");
            }

            ThirdPartyConfig config = configResult.getData();
            if (config == null) {
                log.error("未找到平台类型为 {} 的配置", platformType);
                return ResultData.fail(-1, "未找到平台配置");
            }

            // 2. 调用企业微信API获取AccessToken
            // 注意：现在config是ThirdPartyConfig，可以直接使用字段
            // 这里需要根据JSON配置来获取corpId和corpSecret
            // 暂时返回错误，需要实现JSON解析逻辑
            log.error("需要实现JSON配置解析逻辑");
            return ResultData.fail(-1, "配置解析功能待实现");

        } catch (Exception e) {
            log.error("获取企业微信AccessToken异常，平台类型：{}", platformType, e);
            return ResultData.fail(-1, "获取AccessToken异常：" + e.getMessage());
        }
    }

    /**
     * 使用应用Secret获取企业微信AccessToken
     * 
     * @param platformType 平台类型
     * @return AccessToken响应
     */
    public ResultData<WechatWorkAuthStatusResponse> getAccessTokenByAgent(Integer platformType) {
        try {
            // 1. 从配置服务获取企业微信配置
            var configResult = authFeignClient.getConfigByPlatformType(platformType);
            if (!(configResult.getCode() ==200)) {
                log.error("获取第三方平台配置失败：{}", configResult.getMessage());
                return ResultData.fail(-1, "获取平台配置失败");
            }

            ThirdPartyConfig config = configResult.getData();
            if (config == null) {
                log.error("未找到平台类型为 {} 的配置", platformType);
                return ResultData.fail(-1, "未找到平台配置");
            }

            // 2. 调用企业微信API获取AccessToken（使用应用Secret）
            // 注意：现在config是ThirdPartyConfig，可以直接使用字段
            // 这里需要根据JSON配置来获取corpId、appId和appSecret
            // 暂时返回错误，需要实现JSON解析逻辑
            log.error("需要实现JSON配置解析逻辑");
            return ResultData.fail(-1, "配置解析功能待实现");

        } catch (Exception e) {
            log.error("获取企业微信AccessToken异常，平台类型：{}", platformType, e);
            return ResultData.fail(-1, "获取AccessToken异常：" + e.getMessage());
        }
    }

    /**
     * 验证AccessToken是否有效
     * 
     * @param platformType 平台类型
     * @return true-有效，false-无效
     */
    public boolean validateAccessToken(Integer platformType) {
        ResultData<WechatWorkAuthStatusResponse> response = getAccessToken(platformType);
        return (response.getCode() ==200);
    }
} 