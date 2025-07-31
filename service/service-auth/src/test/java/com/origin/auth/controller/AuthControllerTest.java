package com.origin.auth.controller;

import com.origin.auth.dto.LoginRequest;
import com.origin.auth.dto.LoginResponse;
import com.origin.auth.service.SysUserService;
import com.origin.common.ResultData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 认证控制器测试类
 */
public class AuthControllerTest {

    @Mock
    private SysUserService sysUserService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin() {
        // 准备测试数据
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId("1");
        loginResponse.setUsername("admin");
        loginResponse.setNickname("系统管理员");
        loginResponse.setRoles(Arrays.asList("ROLE_ADMIN"));
        loginResponse.setPermissions(Arrays.asList("system:manage", "system:user"));
        loginResponse.setToken("Bearer eyJhbGciOiJIUzI1NiJ9...");
        loginResponse.setTokenType("Bearer");
        loginResponse.setExpiresIn(3600L);

        // 模拟服务层方法
        when(sysUserService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // 调用控制器方法
        ResultData result = authController.login(loginRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("登录成功", result.getMessage());
        assertNotNull(result.getData());
        LoginResponse responseData = (LoginResponse) result.getData();
        assertEquals(1L, responseData.getUserId());
        assertEquals("admin", responseData.getUsername());
        assertEquals("系统管理员", responseData.getNickname());
        assertEquals("Bearer eyJhbGciOiJIUzI1NiJ9...", responseData.getToken());
        assertEquals("Bearer", responseData.getTokenType());
        assertEquals(3600L, responseData.getExpiresIn());
    }

    @Test
    public void testLogout() {
        // 模拟请求头
        when(request.getHeader("Authorization")).thenReturn("Bearer test-token");
        
        // 调用控制器方法
        ResultData result = authController.logout(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("登出成功", result.getMessage());
    }
}