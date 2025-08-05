package com.origin.auth.service;

import com.origin.common.dto.LoginRequest;
import com.origin.common.dto.LoginResponse;
import com.origin.auth.dto.UserInfoResponse;

/**
 * 认证服务接口
 * 基于简化的权限控制，使用用户类型字段直接控制权限
 * 
 * @author scccy
 * @since 2025-07-31
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
    
    /**
     * 从JWT令牌中获取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID
     */
    String getUserIdFromToken(String token);
    
    /**
     * 从JWT令牌中获取用户类型
     * 
     * @param token JWT令牌
     * @return 用户类型
     */
    Integer getUserTypeFromToken(String token);
    
    /**
     * 验证用户权限
     * 
     * @param userId 用户ID
     * @param requiredUserType 需要的用户类型
     * @return 是否有权限
     */
    boolean hasPermission(String userId, Integer requiredUserType);
    
    /**
     * 验证用户是否为管理员
     * 
     * @param userId 用户ID
     * @return 是否为管理员
     */
    boolean isAdmin(String userId);
    
    /**
     * 验证用户是否为发布者
     * 
     * @param userId 用户ID
     * @return 是否为发布者
     */
    boolean isPublisher(String userId);
    
    /**
     * 验证用户是否为接受者
     * 
     * @param userId 用户ID
     * @return 是否为接受者
     */
    boolean isReceiver(String userId);
    
    /**
     * 获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoResponse getUserInfo(String userId);
} 