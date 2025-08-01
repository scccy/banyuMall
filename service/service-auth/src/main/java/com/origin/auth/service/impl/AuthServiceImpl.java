package com.origin.auth.service.impl;

import com.origin.auth.service.AuthService;
import com.origin.auth.dto.LoginRequest;
import com.origin.auth.dto.LoginResponse;
import com.origin.auth.entity.SysUser;
import com.origin.auth.mapper.SysUserMapper;
import com.origin.auth.util.JwtUtil;
import com.origin.auth.util.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现类
 * 
 * @author origin
 * @since 2024-07-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "auth:jwt")
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtTokenManager jwtTokenManager;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录 - 用户名: {}", request.getUsername());
        
        // 验证用户名和密码
        SysUser user = sysUserMapper.selectByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("登录失败 - 用户名或密码错误: {}", request.getUsername());
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.updateById(user);
        
        log.info("用户登录成功 - 用户名: {}", request.getUsername());
        
        return new LoginResponse()
                .setToken(token)
                .setUsername(user.getUsername())
                .setUserId(user.getId());
    }

    @Override
    @Cacheable(key = "#token")
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            log.warn("令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @CacheEvict(key = "#token")
    public void logout(String token) {
        log.info("用户登出 - 令牌: {}", token);
        
        try {
            // 从token中提取用户信息
            String userId = jwtUtil.getUserIdFromToken(token);
            
            if (userId != null) {
                // 使用基于用户ID的token管理
                jwtTokenManager.removeUserToken(userId);
                
                // 将token加入黑名单
                long expirationTime = jwtUtil.getExpirationTime(token);
                if (expirationTime > 0) {
                    jwtTokenManager.addToBlacklist(token, expirationTime);
                }
                
                log.info("用户登出成功 - 用户ID: {}", userId);
            } else {
                log.warn("登出失败 - Token中缺少用户信息");
            }
        } catch (Exception e) {
            log.error("登出失败 - Token解析错误: {}", e.getMessage());
            throw new RuntimeException("登出失败：无效的token", e);
        }
    }

    @Override
    public void forceLogout(String userId) {
        log.info("强制登出用户 - 用户ID: {}", userId);
        
        try {
            // 强制移除用户token
            jwtTokenManager.removeUserToken(userId);
            log.info("强制登出成功 - 用户ID: {}", userId);
        } catch (Exception e) {
            log.error("强制登出失败 - 用户ID: {}, 错误: {}", userId, e.getMessage());
            throw new RuntimeException("强制登出失败", e);
        }
    }

    @Override
    @Cacheable(key = "#token")
    public String getUsernameFromToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        
        try {
            return jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            log.warn("从令牌获取用户名失败: {}", e.getMessage());
            return null;
        }
    }
}