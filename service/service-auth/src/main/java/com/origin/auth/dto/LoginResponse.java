package com.origin.auth.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 登录响应DTO
 * 
 * @author origin
 * @since 2024-07-30
 */
@Data
@Accessors(chain = true)
public class LoginResponse {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 角色列表
     */
    private List<String> roles;
    
    /**
     * 权限列表
     */
    private List<String> permissions;
    
    /**
     * 访问令牌
     */
    private String token;
    
    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";
    
    /**
     * 过期时间（秒）
     */
    private Long expiresIn;
}