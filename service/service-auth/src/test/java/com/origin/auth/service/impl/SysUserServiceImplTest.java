package com.origin.auth.service.impl;

import com.origin.auth.dto.LoginRequest;
import com.origin.auth.dto.LoginResponse;
import com.origin.auth.entity.SysUser;
import com.origin.auth.service.SysPermissionService;
import com.origin.auth.service.SysRoleService;
import com.origin.auth.util.JwtUtil;
import com.origin.auth.util.TokenBlacklistUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * SysUserServiceImpl 测试类
 */
public class SysUserServiceImplTest {

    @Mock
    private SysRoleService sysRoleService;

    @Mock
    private SysPermissionService sysPermissionService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenBlacklistUtil tokenBlacklistUtil;

    @InjectMocks
    private SysUserServiceImpl sysUserService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginSuccess() {
        // 准备测试数据
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        SysUser user = new SysUser();
        user.setId("1");
        user.setUsername("admin");
        user.setNickname("管理员");
        user.setPassword("$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm");
        user.setStatus(1);
        user.setLastLoginTime(LocalDateTime.now());

        List<String> roles = Arrays.asList("ADMIN", "USER");
        List<String> permissions = Arrays.asList("user:read", "user:write");

        // 模拟依赖方法
        when(sysUserService.getByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("123456", user.getPassword())).thenReturn(true);
        when(sysRoleService.getRoleCodesByUserId("1")).thenReturn(roles);
        when(sysPermissionService.getPermissionCodesByUserId("1")).thenReturn(permissions);
        when(jwtUtil.generateToken("1", "admin", roles, permissions)).thenReturn("test-token");
        when(tokenBlacklistUtil.markAsValid(anyString(), any())).thenReturn(true);

        // 执行测试
        LoginResponse response = sysUserService.login(loginRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals("1", response.getUserId());
        assertEquals("admin", response.getUsername());
        assertEquals("管理员", response.getNickname());
        assertEquals(roles, response.getRoles());
        assertEquals(permissions, response.getPermissions());
        assertEquals("test-token", response.getToken());
    }
} 