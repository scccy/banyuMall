package com.origin.banyu.common.dto;

import lombok.Data;
import java.util.Map;

/**
 * 平台配置DTO - 用于JSON字段的结构化处理
 * 
 * @author scccy
 * @since 2025-08-06
 */
public class ThirdPartyPlatformConfigDTO {
    
    /**
     * 企业微信配置
     */
    @Data
    public static class WechatWorkConfig {
        private String corpId;
        private String corpSecret;
        private String token;
        private String encodingAesKey;
        private String echoStr;
        private String appId;
        private String appSecret;
        private String accessToken;
        private String webhookUrl;
        private String callbackUrl;
    }
    
    /**
     * 钉钉配置
     */
    @Data
    public static class DingTalkConfig {
        private String appKey;
        private String appSecret;
        private String agentId;
        private String accessToken;
        private String webhookUrl;
        private String callbackUrl;
    }
    
    /**
     * 飞书配置
     */
    @Data
    public static class FeiShuConfig {
        private String appId;
        private String appSecret;
        private String tenantAccessToken;
        private String webhookUrl;
        private String callbackUrl;
    }
    
    /**
     * 有赞配置
     */
    @Data
    public static class YouZanConfig {
        private String clientId;
        private String clientSecret;
        private String accessToken;
        private String webhookUrl;
        private String callbackUrl;
    }
    
    /**
     * 通用配置 - 用于未知平台或扩展
     */
    @Data
    public static class GenericConfig {
        private Map<String, Object> config;
        
        public GenericConfig() {
            this.config = new java.util.HashMap<>();
        }
        
        public void setConfig(String key, Object value) {
            this.config.put(key, value);
        }
        
        public Object getConfig(String key) {
            return this.config.get(key);
        }
        
        public Map<String, Object> getAllConfig() {
            return new java.util.HashMap<>(this.config);
        }
    }
} 