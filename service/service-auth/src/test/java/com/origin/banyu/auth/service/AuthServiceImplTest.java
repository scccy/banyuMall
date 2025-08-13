package com.origin.banyu.auth.service;

import com.origin.banyu.auth.service.impl.AuthServiceImpl;
import com.origin.banyu.auth.util.JwtTokenManager;
import com.origin.banyu.auth.util.JwtUtil;
import com.origin.banyu.common.dto.LoginRequest;
import com.origin.banyu.common.entity.SysUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private SysUserService sysUserService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private JwtTokenManager jwtTokenManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("login - 正常流程")
    void login_ok() {
        LoginRequest req = new LoginRequest();
        req.setUsername("alice");
        req.setPassword("pwd");

        SysUser user = new SysUser();
        user.setUserId("u1");
        user.setUsername("alice");
        user.setUserType(1);
        user.setStatus(1);

        when(sysUserService.getById("alice")).thenReturn(user);
        when(sysUserService.validatePassword("pwd", null)).thenReturn(true);
        when(jwtUtil.generateToken(eq("u1"), eq("alice"), any(HashMap.class))).thenReturn("token");
        when(jwtUtil.getExpirationTime("token")).thenReturn(3600L);

        var resp = authService.login(req);
        assertThat(resp.getUsername()).isEqualTo("alice");
        assertThat(resp.getToken()).isEqualTo("token");
    }
}


