package com.origin.user.feign;

import com.origin.common.dto.ResultData;
import com.origin.common.dto.PasswordEncryptRequest;
import com.origin.common.dto.PasswordEncryptResponse;
import com.origin.user.entity.SysUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 认证服务Feign客户端测试
 * 
 * @author scccy
 * @since 2025-08-04
 */
@SpringBootTest
@ActiveProfiles("test")
class AuthFeignClientTest {

    @MockBean
    private AuthFeignClient authFeignClient;

    @Test
    @DisplayName("认证服务调用 - 正常调用测试")
    void authFeignTest() {
        // 模拟密码加密响应
        PasswordEncryptResponse mockResponse = new PasswordEncryptResponse()
                .setUsername("testuser")
                .setEncryptedPassword("$2a$12$test.encrypted.password.hash");
        
        when(authFeignClient.encryptPassword(any(PasswordEncryptRequest.class)))
                .thenReturn(ResultData.ok("密码加密成功", mockResponse));
        
        // 测试密码加密功能
        PasswordEncryptRequest request = new PasswordEncryptRequest()
                .setUsername("testuser")
                .setPassword("123456");
        
        ResultData<PasswordEncryptResponse> result = authFeignClient.encryptPassword(request);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        PasswordEncryptResponse response = result.getData();
        assertNotNull(response);
        assertNotNull(response.getEncryptedPassword());
        assertNotEquals("123456", response.getEncryptedPassword());
        assertTrue(response.getEncryptedPassword().startsWith("$2a$"));
        assertEquals("testuser", response.getUsername());
    }

    @Test
    @DisplayName("认证服务调用 - 密码验证测试")
    void passwordVerifyTest() {
        // 模拟密码验证响应
        when(authFeignClient.verifyPassword(any(PasswordEncryptRequest.class)))
                .thenReturn(ResultData.ok("密码验证成功", true));
        
        // 测试密码验证功能
        String username = "testuser";
        String rawPassword = "123456";
        
        // 验证正确密码
        PasswordEncryptRequest verifyRequest = new PasswordEncryptRequest()
                .setUsername(username)
                .setPassword(rawPassword);
        
        ResultData<Boolean> verifyResult = authFeignClient.verifyPassword(verifyRequest);
        assertNotNull(verifyResult);
        assertTrue(verifyResult.isSuccess());
        assertTrue(verifyResult.getData());
    }

    @Test
    @DisplayName("认证服务调用 - 用户信息获取测试")
    void getUserInfoTest() {
        // 模拟用户信息响应
        SysUser mockUser = new SysUser();
        mockUser.setUserId("test_user_001");
        mockUser.setUsername("testuser");
        mockUser.setNickname("测试用户");
        
        when(authFeignClient.getUserInfo("test_user_001"))
                .thenReturn(ResultData.ok("获取用户信息成功", mockUser));
        
        // 测试获取用户信息功能
        String userId = "test_user_001";
        ResultData<SysUser> result = authFeignClient.getUserInfo(userId);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        SysUser user = result.getData();
        assertNotNull(user);
        assertEquals(userId, user.getUserId());
        assertEquals("testuser", user.getUsername());
    }

    @Test
    @DisplayName("认证服务调用 - JWT令牌验证测试")
    void validateTokenTest() {
        // 模拟JWT令牌验证响应
        when(authFeignClient.validateToken("valid-token"))
                .thenReturn(ResultData.ok("令牌验证成功", true));
        when(authFeignClient.validateToken("invalid-token"))
                .thenReturn(ResultData.ok("令牌验证失败", false));
        
        // 测试JWT令牌验证功能
        String validToken = "valid-token";
        ResultData<Boolean> validResult = authFeignClient.validateToken(validToken);
        assertNotNull(validResult);
        assertTrue(validResult.isSuccess());
        assertTrue(validResult.getData());
        
        String invalidToken = "invalid-token";
        ResultData<Boolean> invalidResult = authFeignClient.validateToken(invalidToken);
        assertNotNull(invalidResult);
        assertTrue(invalidResult.isSuccess());
        assertFalse(invalidResult.getData());
    }
} 