package com.origin.auth.controller;

import com.origin.auth.config.TestSecurityConfig;
import com.origin.auth.entity.SysUser;
import com.origin.auth.service.AuthService;
import com.origin.auth.service.SysUserService;
import com.origin.auth.config.TestConfig;
import com.origin.common.dto.LoginRequest;
import com.origin.common.dto.LoginResponse;
import com.origin.common.dto.PasswordEncryptRequest;
import com.origin.common.dto.PasswordEncryptResponse;
import com.origin.common.dto.ResultData;
import com.origin.auth.util.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;



/**
 * AuthController API测试类
 */
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {TestConfig.class, TestSecurityConfig.class})
class AuthControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @MockBean
    private SysUserService sysUserService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("用户登录 - 正常请求测试")
    void loginTest() throws Exception {
        // 准备测试数据
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");
        loginRequest.setCaptcha("1234");
        loginRequest.setCaptchaKey("captcha_key");
        loginRequest.setRememberMe(true);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId("admin_001");
        loginResponse.setUsername("admin");
        loginResponse.setNickname("管理员");
        loginResponse.setExpiresIn(3600L);
        loginResponse.setUserType(1);

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // 执行测试
        mockMvc.perform(post("/service/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123")
                .header("X-Client-IP", "127.0.0.1")
                .header("X-User-Agent", "Mozilla/5.0")
                .content("{\"username\":\"admin\",\"password\":\"123456\",\"captcha\":\"1234\",\"captchaKey\":\"captcha_key\",\"rememberMe\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.userId").value("admin_001"))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    @DisplayName("用户登录 - 参数验证测试")
    void loginValidationTest() throws Exception {
        // 测试缺少用户名的情况
        mockMvc.perform(post("/service/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"123456\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("用户登录 - 异常情况测试")
    void loginExceptionTest() throws Exception {
        // 模拟登录失败
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("用户名或密码错误"));

        mockMvc.perform(post("/service/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"nonexistent\",\"password\":\"123456\"}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("用户登出 - 正常请求测试")
    void logoutTest() throws Exception {
        // 模拟JWT令牌验证
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn("test_user_001");
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn("admin");

        mockMvc.perform(post("/service/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer valid_token")
                .header("X-Request-ID", "test-request-123")
                .header("X-Client-IP", "127.0.0.1")
                .header("X-User-Agent", "Mozilla/5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登出成功"));
    }

    @Test
    @DisplayName("用户登出 - 缺少令牌测试")
    void logoutWithoutTokenTest() throws Exception {
        mockMvc.perform(post("/service/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登出失败：缺少有效的token"));
    }

    @Test
    @DisplayName("强制登出用户 - 正常请求测试")
    void forceLogoutTest() throws Exception {
        mockMvc.perform(post("/service/auth/logout/force/test_user_001")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123")
                .header("X-Client-IP", "127.0.0.1")
                .header("X-User-Agent", "Mozilla/5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("强制登出成功"));
    }

    @Test
    @DisplayName("密码加密 - 正常请求测试")
    void encryptPasswordTest() throws Exception {
        // 模拟密码加密
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$encrypted_password");

        mockMvc.perform(post("/service/auth/password/encrypt")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("密码加密成功"))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.encryptedPassword").exists());
    }

    @Test
    @DisplayName("密码验证 - 正常请求测试")
    void verifyPasswordTest() throws Exception {
        // 准备测试用户
        SysUser user = new SysUser();
        user.setUserId("test_user_001");
        user.setUsername("admin");
        user.setPassword("$2a$12$encrypted_password");

        when(sysUserService.getByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/service/auth/password/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("密码验证完成"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("密码验证 - 用户不存在测试")
    void verifyPasswordUserNotFoundTest() throws Exception {
        when(sysUserService.getByUsername("nonexistent")).thenReturn(null);

        mockMvc.perform(post("/service/auth/password/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"nonexistent\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户不存在"))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @DisplayName("获取用户信息 - 正常请求测试")
    void getUserInfoTest() throws Exception {
        // 准备测试用户
        SysUser user = new SysUser();
        user.setUserId("test_user_001");
        user.setUsername("admin");
        user.setNickname("管理员");
        user.setEmail("admin@example.com");
        user.setPhone("13800138000");
        user.setUserType(1);
        user.setStatus(1);

        when(sysUserService.getById("test_user_001")).thenReturn(user);

        mockMvc.perform(get("/service/auth/user/info")
                .param("userId", "test_user_001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取用户信息成功"))
                .andExpect(jsonPath("$.data.userId").value("test_user_001"))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    @DisplayName("获取用户信息 - 用户不存在测试")
    void getUserInfoNotFoundTest() throws Exception {
        when(sysUserService.getById("nonexistent")).thenReturn(null);

        mockMvc.perform(get("/service/auth/user/info")
                .param("userId", "nonexistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    @DisplayName("验证JWT令牌 - 正常请求测试")
    void validateTokenTest() throws Exception {
        when(jwtUtil.validateToken("valid_token")).thenReturn(true);

        mockMvc.perform(post("/service/auth/validate")
                .param("token", "valid_token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("令牌验证完成"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("验证JWT令牌 - 无效令牌测试")
    void validateInvalidTokenTest() throws Exception {
        when(jwtUtil.validateToken("invalid_token")).thenReturn(false);

        mockMvc.perform(post("/service/auth/validate")
                .param("token", "invalid_token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("令牌验证完成"))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @DisplayName("健康检查 - 正常请求测试")
    void testTest() throws Exception {
        mockMvc.perform(get("/service/auth/test")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Request-ID", "test-request-123")
                .header("X-Client-IP", "127.0.0.1")
                .header("X-User-Agent", "Mozilla/5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("认证服务运行正常"));
    }

    @Test
    @DisplayName("用户登录 - 权限验证测试")
    void loginPermissionTest() throws Exception {
        // 测试不同用户类型的登录权限
        LoginRequest adminLoginRequest = new LoginRequest();
        adminLoginRequest.setUsername("admin");
        adminLoginRequest.setPassword("123456");

        LoginResponse adminResponse = new LoginResponse();
        adminResponse.setUserId("admin_001");
        adminResponse.setUsername("admin");
        adminResponse.setUserType(1); // 管理员

        when(authService.login(adminLoginRequest)).thenReturn(adminResponse);

        mockMvc.perform(post("/service/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userType").value(1));
    }

    @Test
    @DisplayName("密码加密 - 异常情况测试")
    void encryptPasswordExceptionTest() throws Exception {
        // 模拟密码加密异常
        when(passwordEncoder.encode(anyString()))
                .thenThrow(new RuntimeException("密码加密失败"));

        mockMvc.perform(post("/service/auth/password/encrypt")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("密码加密失败：密码加密失败"));
    }

    @Test
    @DisplayName("获取用户信息 - 异常情况测试")
    void getUserInfoExceptionTest() throws Exception {
        // 模拟获取用户信息异常
        when(sysUserService.getById(anyString()))
                .thenThrow(new RuntimeException("数据库连接失败"));

        mockMvc.perform(get("/service/auth/user/info")
                .param("userId", "test_user_001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("获取用户信息失败：数据库连接失败"));
    }
} 