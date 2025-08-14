package com.origin.banyu.common.util;

import com.alibaba.fastjson2.JSON;
import com.origin.banyu.common.dto.ThirdPartyPlatformConfigDTO;
import com.origin.banyu.common.entity.ThirdPartyConfig;
import com.origin.banyu.common.enums.PlatformType;
import lombok.extern.slf4j.Slf4j;

/**
 * 第三方平台配置解析工具类
 * 支持多种平台配置的解析，包括企业微信、个人微信、有赞等
 * 
 * @author scccy
 * @since 2025-08-07
 */
@Slf4j
public class ThirdPartyConfigParser {

    /**
     * 解析ThirdPartyConfig实体中的配置
     * 
     * @param config ThirdPartyConfig实体
     * @return 解析后的配置对象
     */
    public static Object parseConfig(ThirdPartyConfig config) {
        if (config == null) {
            return null;
        }
        return config.getPlatformConfig();
    }

    /**
     * 根据平台类型解析为对应的配置对象
     * 
     * @param platformType 平台类型
     * @param platformConfig JSON配置字符串
     * @return 对应的配置对象
     */
    public static Object parseConfigByType(int platformType, String platformConfig) {
        try {
            if (platformConfig == null || platformConfig.trim().isEmpty()) {
                log.debug("平台配置为空，跳过解析: platformType={}", platformType);
                return null;
            }

            PlatformType type = PlatformType.fromCode(platformType);
            if (type == null) {
                log.warn("不支持的平台类型: {}", platformType);
                return null;
            }

            Object result = parseConfigByType(type, platformConfig);
            if (result == null) {
                log.debug("解析结果为空: platformType={}, platformConfig={}", platformType, platformConfig);
            }
            return result;
        } catch (Exception e) {
            log.error("解析平台配置失败: platformType={}, platformConfig={}, error={}", platformType, platformConfig, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据平台类型枚举解析为对应的配置对象
     * 
     * @param platformType 平台类型枚举
     * @param platformConfig JSON配置字符串
     * @return 对应的配置对象
     */
    public static Object parseConfigByType(PlatformType platformType, String platformConfig) {
        try {
            if (platformConfig == null || platformConfig.trim().isEmpty()) {
                log.debug("平台配置为空，跳过解析: platformType={}", platformType != null ? platformType.getName() : "null");
                return null;
            }

            if (platformType == null) {
                log.warn("平台类型不能为空");
                return null;
            }

            Object result = null;
            switch (platformType) {
                case WECHAT_WORK:
                    result = JSON.parseObject(platformConfig, ThirdPartyPlatformConfigDTO.WechatWorkConfig.class);
                    log.debug("解析企业微信配置成功: configLength={}", platformConfig.length());
                    break;
                case WECHAT_PERSONAL:
                    // TODO: 添加个人微信配置解析
                    log.warn("暂不支持个人微信配置解析: platformType={}", platformType.getName());
                    return null;
                case YOUZAN:
                    result = JSON.parseObject(platformConfig, ThirdPartyPlatformConfigDTO.YouZanConfig.class);
                    log.debug("解析有赞配置成功: configLength={}", platformConfig.length());
                    break;
                case DINGTALK:
                    result = JSON.parseObject(platformConfig, ThirdPartyPlatformConfigDTO.DingTalkConfig.class);
                    log.debug("解析钉钉配置成功: configLength={}", platformConfig.length());
                    break;
                case FEISHU:
                    result = JSON.parseObject(platformConfig, ThirdPartyPlatformConfigDTO.FeiShuConfig.class);
                    log.debug("解析飞书配置成功: configLength={}", platformConfig.length());
                    break;
                default:
                    log.warn("暂不支持的平台类型: {}", platformType.getName());
                    return null;
            }
            
            if (result == null) {
                log.warn("解析结果为空: platformType={}, platformConfig={}", platformType.getName(), platformConfig);
            }
            return result;
        } catch (Exception e) {
            log.error("解析平台配置失败: platformType={}, platformConfig={}, error={}", 
                     platformType != null ? platformType.getName() : "null", platformConfig, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将平台配置对象序列化为标准 JSON 字符串
     * @param configObj 平台配置对象（如 WechatWorkConfig/Map/DTO）
     * @return 标准 JSON 字符串
     */
    public static String toJsonString(Object configObj) {
        if (configObj == null) {
            return null;
        }
        return JSON.toJSONString(configObj);
    }
} 