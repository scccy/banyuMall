package com.origin.wechatWork.service;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.dto.ThirdPartyPlatformConfigDTO;
import com.origin.banyu.common.entity.ThirdPartyConfig;
import com.origin.banyu.wechatWork.dto.AuthQrCodeResponse;
import com.origin.banyu.wechatWork.feign.WechatWorkAuthFeignClient;
import com.origin.banyu.wechatWork.service.WechatWorkAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
/**
 * 企业微信授权服务测试类
 * 
 * @author scccy
 */
@SpringBootTest(classes = com.origin.banyu.wechatWork.WechatWorkApplication.class)
@ActiveProfiles("test")
class WechatWorkAuthServiceTest {

    @Autowired
    private WechatWorkAuthService authService;

    @MockBean
    private WechatWorkAuthFeignClient authFeignClient;

    @Test
    void testGenerateAuthQrCode() {
        // 准备测试数据
        String redirectUri = "https://example.com/callback";
        
        // Mock配置数据
        ThirdPartyPlatformConfigDTO.WechatWorkConfig wechatWorkConfig = new ThirdPartyPlatformConfigDTO.WechatWorkConfig();
        wechatWorkConfig.setCorpId("wwb2cff798f41fb19d");
        wechatWorkConfig.setAppId("1000001");
        
        ThirdPartyConfig config = new ThirdPartyConfig();
        config.setPlatformConfig(wechatWorkConfig);
        
        ResultData<ThirdPartyConfig> configResult = ResultData.success("成功", config);
        
        // Mock Feign客户端调用
        when(authFeignClient.getWechatWorkConfig()).thenReturn(configResult);
        
        // 执行测试
        AuthQrCodeResponse response = authService.generateAuthQrCode(redirectUri);
        
        // 验证结果
        assertNotNull(response, "响应不应为空");
        assertNotNull(response.getAuthUrl(), "授权URL不应为空");
        assertNotNull(response.getQrCodeUrl(), "二维码URL不应为空");
        assertNotNull(response.getQrCodeImage(), "二维码图片不应为空");
        assertNotNull(response.getState(), "状态参数不应为空");
        assertNotNull(response.getExpireTime(), "过期时间不应为空");
        
        // 验证URL格式
        assertTrue(response.getAuthUrl().contains("open.work.weixin.qq.com"), "URL应包含企业微信域名");
        assertTrue(response.getAuthUrl().contains("wwopen/sso/qrConnect"), "URL应包含二维码授权路径");
        assertTrue(response.getAuthUrl().contains("wwb2cff798f41fb19d"), "URL应包含企业ID");
        assertTrue(response.getAuthUrl().contains("1000001"), "URL应包含应用ID");
        assertTrue(response.getAuthUrl().contains(redirectUri), "URL应包含回调地址");
        
        // 验证二维码图片格式（Base64）
        assertTrue(response.getQrCodeImage().length() > 0, "二维码图片不应为空");
        
        // 验证过期时间（5分钟后）
        long expectedExpireTime = System.currentTimeMillis() + 300000; // 5分钟
        assertTrue(response.getExpireTime() > System.currentTimeMillis(), "过期时间应大于当前时间");
        assertTrue(response.getExpireTime() <= expectedExpireTime + 1000, "过期时间应在合理范围内");
        
        System.out.println("授权URL: " + response.getAuthUrl());
        System.out.println("状态参数: " + response.getState());
        System.out.println("过期时间: " + response.getExpireTime());
        System.out.println("二维码图片长度: " + response.getQrCodeImage().length());
    }

    @Test
    void testGenerateAuthQrCodeWithInvalidConfig() {
        // 准备测试数据
        String redirectUri = "https://example.com/callback";
        
        // Mock无效配置数据
        ThirdPartyPlatformConfigDTO.WechatWorkConfig wechatWorkConfig = new ThirdPartyPlatformConfigDTO.WechatWorkConfig();
        wechatWorkConfig.setCorpId(null); // 无效的企业ID
        wechatWorkConfig.setAppId("1000001");
        
        ThirdPartyConfig config = new ThirdPartyConfig();
        config.setPlatformConfig(wechatWorkConfig);
        
        ResultData<ThirdPartyConfig> configResult = ResultData.success("成功", config);
        
        // Mock Feign客户端调用
        when(authFeignClient.getWechatWorkConfig()).thenReturn(configResult);
        
        // 执行测试并验证异常
        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.generateAuthQrCode(redirectUri);
        });
        
        assertTrue(exception.getMessage().contains("企业微信配置信息不完整"), "异常信息应包含配置不完整的提示");
    }

    @Test
    void testGenerateAuthQrCodeWithFeignError() {
        // 准备测试数据
        String redirectUri = "https://example.com/callback";
        
        // Mock Feign客户端错误
        ResultData<ThirdPartyConfig> configResult = ResultData.fail("获取配置失败");
        
        // Mock Feign客户端调用
        when(authFeignClient.getWechatWorkConfig()).thenReturn(configResult);
        
        // 执行测试并验证异常
        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.generateAuthQrCode(redirectUri);
        });
        
        assertTrue(exception.getMessage().contains("获取企业微信配置失败"), "异常信息应包含配置获取失败的提示");
    }
} 