package com.origin.wechatWork;

import com.origin.banyu.wechatWork.dto.AuthQrCodeResponse;
import com.origin.banyu.wechatWork.service.WechatWorkAuthService;
import com.origin.banyu.wechatWork.service.AccessTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 企业微信二维码授权集成测试类
 * 
 * @author scccy
 */
@SpringBootTest(classes = com.origin.banyu.wechatWork.WechatWorkApplication.class)
@ActiveProfiles("test")
class WechatWorkQrCodeAuthTests {

    @Autowired
    private WechatWorkAuthService authService;

    @Autowired
    private AccessTokenService accessTokenService;

    @Test
    void testCompleteQrCodeAuthFlow() {
        // 步骤1: 验证access_token获取
        String accessToken = accessTokenService.getAccessToken();
        assertNotNull(accessToken, "access_token不应为空");
        assertFalse(accessToken.isEmpty(), "access_token不应为空字符串");
        System.out.println("✅ access_token获取成功: " + accessToken.substring(0, 20) + "...");

        // 步骤2: 生成授权二维码
        String redirectUri = "https://banyumall.com/wechatwork/callback";
        AuthQrCodeResponse qrCodeResponse = authService.generateAuthQrCode(redirectUri);
        
        // 验证二维码响应
        assertNotNull(qrCodeResponse, "二维码响应不应为空");
        assertNotNull(qrCodeResponse.getAuthUrl(), "授权URL不应为空");
        assertNotNull(qrCodeResponse.getQrCodeImage(), "二维码图片不应为空");
        assertNotNull(qrCodeResponse.getState(), "状态参数不应为空");
        assertNotNull(qrCodeResponse.getExpireTime(), "过期时间不应为空");
        
        // 验证URL格式
        assertTrue(qrCodeResponse.getAuthUrl().contains("open.work.weixin.qq.com"), "URL应包含企业微信域名");
        assertTrue(qrCodeResponse.getAuthUrl().contains("wwopen/sso/qrConnect"), "URL应包含二维码授权路径");
        assertTrue(qrCodeResponse.getAuthUrl().contains(redirectUri), "URL应包含回调地址");
        
        // 验证二维码图片（Base64格式）
        assertTrue(qrCodeResponse.getQrCodeImage().length() > 1000, "二维码图片应有一定大小");
        
        // 验证过期时间
        assertTrue(qrCodeResponse.getExpireTime() > System.currentTimeMillis(), "过期时间应大于当前时间");
        
        System.out.println("✅ 二维码生成成功");
        System.out.println("   授权URL: " + qrCodeResponse.getAuthUrl());
        System.out.println("   状态参数: " + qrCodeResponse.getState());
        System.out.println("   过期时间: " + qrCodeResponse.getExpireTime());
        System.out.println("   二维码图片长度: " + qrCodeResponse.getQrCodeImage().length());
        
        // 步骤3: 验证二维码可用性（模拟）
        // 这里可以添加二维码解码验证，确保生成的二维码可以被正确识别
        validateQrCodeContent(qrCodeResponse.getAuthUrl());
        
        System.out.println("✅ 二维码内容验证通过");
        System.out.println("🎉 完整的二维码授权流程验证成功！");
    }

    @Test
    void testQrCodeAuthWithDifferentRedirectUris() {
        String[] redirectUris = {
            "https://banyumall.com/wechatwork/callback",
            "https://api.banyumall.com/auth/callback",
            "https://localhost:3000/callback"
        };
        
        for (String redirectUri : redirectUris) {
            AuthQrCodeResponse response = authService.generateAuthQrCode(redirectUri);
            
            assertNotNull(response, "响应不应为空");
            assertTrue(response.getAuthUrl().contains(redirectUri), "URL应包含指定的回调地址");
            
            System.out.println("✅ 回调地址测试通过: " + redirectUri);
        }
    }

    @Test
    void testQrCodeExpiration() {
        String redirectUri = "https://banyumall.com/wechatwork/callback";
        AuthQrCodeResponse response = authService.generateAuthQrCode(redirectUri);
        
        // 验证过期时间设置
        long currentTime = System.currentTimeMillis();
        long expireTime = response.getExpireTime();
        long expectedExpireTime = currentTime + 300000; // 5分钟
        
        assertTrue(expireTime > currentTime, "过期时间应大于当前时间");
        assertTrue(expireTime <= expectedExpireTime + 1000, "过期时间应在5分钟内");
        
        long timeToExpire = expireTime - currentTime;
        System.out.println("✅ 二维码过期时间验证通过");
        System.out.println("   当前时间: " + currentTime);
        System.out.println("   过期时间: " + expireTime);
        System.out.println("   剩余时间: " + (timeToExpire / 1000) + "秒");
    }

    /**
     * 验证二维码内容
     */
    private void validateQrCodeContent(String qrCodeContent) {
        // 验证URL格式
        assertTrue(qrCodeContent.startsWith("https://"), "URL应以https开头");
        assertTrue(qrCodeContent.contains("open.work.weixin.qq.com"), "URL应包含企业微信域名");
        assertTrue(qrCodeContent.contains("wwopen/sso/qrConnect"), "URL应包含二维码授权路径");
        
        // 验证参数
        assertTrue(qrCodeContent.contains("key="), "URL应包含key参数");
        assertTrue(qrCodeContent.contains("agentid="), "URL应包含agentid参数");
        assertTrue(qrCodeContent.contains("redirect_uri="), "URL应包含redirect_uri参数");
        assertTrue(qrCodeContent.contains("state="), "URL应包含state参数");
        
        System.out.println("   二维码内容验证: " + qrCodeContent);
    }
} 