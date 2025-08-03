package com.origin.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登录响应DTO
 * 统一管理登录相关的响应数据
 * 基于简化的权限控制，使用用户类型字段
 * 
 * @author scccy
 * @since 2025-01-27
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
     * 用户类型：1-系统管理员，2-发布者，3-接受者
     */
    private Integer userType;
    
    /**
     * 用户类型描述
     */
    private String userTypeDesc;
    
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