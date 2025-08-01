package com.origin.auth.service;

import com.origin.auth.dto.LoginRequest;
import com.origin.auth.dto.LoginResponse;

/**
 * 认证服务接口
 * 
 * @author origin
 * @since 2024-07-30
 */
public interface AuthService {
    
    /**
     * 用户登录
     * 
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 验证JWT令牌
     * 
     * @param token JWT令牌
     * @return 是否有效
     */
    boolean validateToken(String token);
    
        /**
     * 用户登出
     *
     * @param token JWT令牌
     */
    void logout(String token);

    /**
     * 强制登出用户（管理员功能）
     *
     * @param userId 用户ID
     */
    void forceLogout(String userId);
    
    /**
     * 从JWT令牌中获取用户名
     * 
     * @param token JWT令牌
     * @return 用户名
     */
    String getUsernameFromToken(String token);
} 