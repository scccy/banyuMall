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
    private Boolean rememberMe = true;
}