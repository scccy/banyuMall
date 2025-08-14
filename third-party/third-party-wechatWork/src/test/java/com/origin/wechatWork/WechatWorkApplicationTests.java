package com.origin.banyu.wechatWork;

import com.origin.banyu.wechatWork.service.AccessTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * 企业微信应用测试类
 * 
 * @author scccy
 */
@SpringBootTest(classes = com.origin.banyu.wechatWork.WechatWorkApplication.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application.yml")
class WechatWorkApplicationTests {

    @Autowired
    private AccessTokenService accessTokenService;

    @Test
    void contextLoads() {
        // 测试Spring Boot应用上下文加载
    }

    @Test
    void testGetAccessToken() {
        String accessToken = accessTokenService.getAccessToken();
        System.out.println("access_token: " + accessToken);
        org.junit.jupiter.api.Assertions.assertNotNull(accessToken, "access_token should not be null");
        org.junit.jupiter.api.Assertions.assertFalse(accessToken.isEmpty(), "access_token should not be empty");
    }
} 