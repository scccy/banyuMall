package com.origin.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登录请求DTO
 * 
 * @author origin
 * @since 2024-07-30
 */
@Data
@Accessors(chain = true)
public class LoginRequest {
    
    // ==================== 请求追踪字段 ====================
    
    /**
     * 请求ID，用于链路追踪
     */
    private String requestId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 客户端IP
     */
    private String clientIp;
    
    /**
     * 用户代理
     */
    private String userAgent;
    
    /**
     * 请求时间戳
     */
    private Long timestamp = System.currentTimeMillis();
    
    // ==================== 登录业务字段 ====================
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 验证码
     */
    private String captcha;
    
    /**
     * 验证码KEY
     */
    private String captchaKey;
    
    /**
     * 记住我
     */
    private Boolean rememberMe = false;
}