package com.origin.banyu.auth.service;

import com.origin.banyu.auth.feign.UserFeignClient;
import com.origin.banyu.auth.service.impl.SysUserServiceFeignAdapter;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.SysUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysUserServiceFeignAdapterTest {

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SysUserServiceFeignAdapter sysUserService;

    @Test
    @DisplayName("getByUsername - 正常返回")
    void getByUsername_ok() {
        SysUser user = new SysUser();
        user.setUserId("u1");
        user.setUsername("alice");
        com.baomidou.mybatisplus.core.metadata.IPage<SysUser> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        ((com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser>) page).setRecords(java.util.List.of(user));
        when(userFeignClient.getUserList(eq("alice"), anyInt(), anyInt())).thenReturn(ResultData.success(page));

        SysUser result = sysUserService.getByUsername("alice");
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("alice");
    }

    @Test
    @DisplayName("updateLastLoginTime - 调用下游，无异常")
    void updateLastLoginTime_ok() {
        when(userFeignClient.updateLastLoginTime(anyString())).thenReturn(ResultData.success("ok"));
        sysUserService.updateLastLoginTime("u1");
        verify(userFeignClient, times(1)).updateLastLoginTime("u1");
    }

    @Test
    @DisplayName("validatePassword - 使用PasswordEncoder")
    void validatePassword_ok() {
        when(passwordEncoder.matches("raw", "enc")).thenReturn(true);
        boolean ok = sysUserService.validatePassword("raw", "enc");
        assertThat(ok).isTrue();
    }
}


