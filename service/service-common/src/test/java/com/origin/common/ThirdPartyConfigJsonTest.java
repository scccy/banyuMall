package com.origin.banyu.common;

import com.alibaba.fastjson2.JSON;
import com.origin.banyu.common.dto.ThirdPartyPlatformConfigDTO;
import com.origin.banyu.common.entity.ThirdPartyConfig;
import com.origin.banyu.common.enums.PlatformType;
import com.origin.banyu.common.util.ThirdPartyConfigParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ThirdPartyConfig JSON解析测试
 * 
 * @author scccy
 * @since 2025-08-07
 */
public class ThirdPartyConfigJsonTest {

    @Test
    public void testWechatWorkConfigParsing() {
        // 准备测试数据
        String jsonConfig = "{\"corpId\":\"test1\",\"corpSecret\":\"test2\",\"webhookUrl\":\"https://your.webhook.url\",\"callbackUrl\":\"https://your.callback.url\"}";
        
        // 测试解析工具类（使用数字）
        Object result = ThirdPartyConfigParser.parseConfigByType(1, jsonConfig);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result instanceof ThirdPartyPlatformConfigDTO.WechatWorkConfig);
        
        ThirdPartyPlatformConfigDTO.WechatWorkConfig config = (ThirdPartyPlatformConfigDTO.WechatWorkConfig) result;
        assertEquals("test1", config.getCorpId());
        assertEquals("test2", config.getCorpSecret());
        assertEquals("https://your.webhook.url", config.getWebhookUrl());
        assertEquals("https://your.callback.url", config.getCallbackUrl());
    }

    @Test
    public void testWechatWorkConfigParsingWithEnum() {
        // 准备测试数据
        String jsonConfig = "{\"corpId\":\"test1\",\"corpSecret\":\"test2\",\"webhookUrl\":\"https://your.webhook.url\",\"callbackUrl\":\"https://your.callback.url\"}";
        
        // 测试解析工具类（使用枚举）
        Object result = ThirdPartyConfigParser.parseConfigByType(PlatformType.WECHAT_WORK, jsonConfig);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result instanceof ThirdPartyPlatformConfigDTO.WechatWorkConfig);
        
        ThirdPartyPlatformConfigDTO.WechatWorkConfig config = (ThirdPartyPlatformConfigDTO.WechatWorkConfig) result;
        assertEquals("test1", config.getCorpId());
        assertEquals("test2", config.getCorpSecret());
        assertEquals("https://your.webhook.url", config.getWebhookUrl());
        assertEquals("https://your.callback.url", config.getCallbackUrl());
    }

    @Test
    public void testWechatWorkConfigParsingWithParser() {
        // 准备测试数据
        String jsonConfig = "{\"corpId\":\"test1\",\"corpSecret\":\"test2\",\"webhookUrl\":\"https://your.webhook.url\",\"callbackUrl\":\"https://your.callback.url\"}";
        
        // 测试解析工具类
        Object result = ThirdPartyConfigParser.parseConfigByType(1, jsonConfig);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result instanceof ThirdPartyPlatformConfigDTO.WechatWorkConfig);
        
        ThirdPartyPlatformConfigDTO.WechatWorkConfig config = (ThirdPartyPlatformConfigDTO.WechatWorkConfig) result;
        assertEquals("test1", config.getCorpId());
        assertEquals("test2", config.getCorpSecret());
        assertEquals("https://your.webhook.url", config.getWebhookUrl());
        assertEquals("https://your.callback.url", config.getCallbackUrl());
    }

    @Test
    public void testYouZanConfigParsing() {
        // 准备测试数据
        String jsonConfig = "{\"clientId\":\"youzan_client\",\"clientSecret\":\"youzan_secret\",\"webhookUrl\":\"https://youzan.webhook.url\"}";
        
        // 测试解析工具类
        Object result = ThirdPartyConfigParser.parseConfigByType(3, jsonConfig);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result instanceof ThirdPartyPlatformConfigDTO.YouZanConfig);
        
        ThirdPartyPlatformConfigDTO.YouZanConfig config = (ThirdPartyPlatformConfigDTO.YouZanConfig) result;
        assertEquals("youzan_client", config.getClientId());
        assertEquals("youzan_secret", config.getClientSecret());
        assertEquals("https://youzan.webhook.url", config.getWebhookUrl());
    }

    @Test
    public void testYouZanConfigParsingWithParser() {
        // 准备测试数据
        String jsonConfig = "{\"clientId\":\"youzan_client\",\"clientSecret\":\"youzan_secret\",\"webhookUrl\":\"https://youzan.webhook.url\"}";
        
        // 测试解析工具类
        Object result = ThirdPartyConfigParser.parseConfigByType(3, jsonConfig);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result instanceof ThirdPartyPlatformConfigDTO.YouZanConfig);
        
        ThirdPartyPlatformConfigDTO.YouZanConfig config = (ThirdPartyPlatformConfigDTO.YouZanConfig) result;
        assertEquals("youzan_client", config.getClientId());
        assertEquals("youzan_secret", config.getClientSecret());
        assertEquals("https://youzan.webhook.url", config.getWebhookUrl());
    }

    @Test
    public void testEntityParsing() {
        // 创建实体对象
        ThirdPartyConfig config = new ThirdPartyConfig();
        config.setConfigId(1);
        config.setPlatformType(1);
        config.setPlatformName("企业微信");
        
        // 设置JSON配置
        String jsonConfig = "{\"corpId\":\"test1\",\"corpSecret\":\"test2\"}";
        config.setPlatformConfig(JSON.parseObject(jsonConfig));
        
        // 设置解析后的配置
        Object parsedConfig = ThirdPartyConfigParser.parseConfigByType(config.getPlatformType(), jsonConfig);
        config.setPlatformConfig(parsedConfig);
        
        // 验证解析结果
        assertNotNull(config.getPlatformConfig());
        assertTrue(config.getPlatformConfig() instanceof ThirdPartyPlatformConfigDTO.WechatWorkConfig);
        
        // 测试JSON序列化
        String jsonString = JSON.toJSONString(config);
        assertTrue(jsonString.contains("\"corpId\":\"test1\""));
        assertTrue(jsonString.contains("\"corpSecret\":\"test2\""));
        assertFalse(jsonString.contains("\"platformConfig\":\"{")); // 不应该包含转义的JSON字符串
    }

    @Test
    public void testDingTalkConfigParsing() {
        // 准备测试数据
        String jsonConfig = "{\"appKey\":\"dingtalk_key\",\"appSecret\":\"dingtalk_secret\",\"agentId\":\"agent123\"}";
        
        // 测试解析工具类
        Object result = ThirdPartyConfigParser.parseConfigByType(4, jsonConfig);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result instanceof ThirdPartyPlatformConfigDTO.DingTalkConfig);
        
        ThirdPartyPlatformConfigDTO.DingTalkConfig config = (ThirdPartyPlatformConfigDTO.DingTalkConfig) result;
        assertEquals("dingtalk_key", config.getAppKey());
        assertEquals("dingtalk_secret", config.getAppSecret());
        assertEquals("agent123", config.getAgentId());
    }

    @Test
    public void testFeiShuConfigParsing() {
        // 准备测试数据
        String jsonConfig = "{\"appId\":\"feishu_app\",\"appSecret\":\"feishu_secret\",\"tenantAccessToken\":\"token123\"}";
        
        // 测试解析工具类
        Object result = ThirdPartyConfigParser.parseConfigByType(5, jsonConfig);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result instanceof ThirdPartyPlatformConfigDTO.FeiShuConfig);
        
        ThirdPartyPlatformConfigDTO.FeiShuConfig config = (ThirdPartyPlatformConfigDTO.FeiShuConfig) result;
        assertEquals("feishu_app", config.getAppId());
        assertEquals("feishu_secret", config.getAppSecret());
        assertEquals("token123", config.getTenantAccessToken());
    }

    @Test
    public void testJsonStringDoubleEscapingFix() {
        // 模拟从数据库查询出来的JSON字符串（已经被转义）
        String escapedJsonString = "{\"appId\": \"\", \"token\": \"\", \"corpId\": \"wwb2cff798f41fb19d\", \"echoStr\": \"\", \"appSecret\": \"\", \"corpSecret\": \"iWwjbLTcE1GGeSw66r2opJTFxJDMPQ6bggIlxLcRvtM\", \"webhookUrl\": \"https://your.webhook.url\", \"accessToken\": \"\", \"callbackUrl\": \"https://your.callback.url\", \"encodingAesKey\": \"\"}";
        
        // 测试解析工具类
        Object result = ThirdPartyConfigParser.parseConfigByType(1, escapedJsonString);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result instanceof ThirdPartyPlatformConfigDTO.WechatWorkConfig);
        
        ThirdPartyPlatformConfigDTO.WechatWorkConfig config = (ThirdPartyPlatformConfigDTO.WechatWorkConfig) result;
        assertEquals("wwb2cff798f41fb19d", config.getCorpId());
        assertEquals("iWwjbLTcE1GGeSw66r2opJTFxJDMPQ6bggIlxLcRvtM", config.getCorpSecret());
        assertEquals("https://your.webhook.url", config.getWebhookUrl());
        assertEquals("https://your.callback.url", config.getCallbackUrl());
    }
} 