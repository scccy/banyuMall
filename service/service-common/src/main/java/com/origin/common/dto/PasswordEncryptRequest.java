package com.origin.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;

/**
 * 密码加密请求DTO
 * 
 * @author scccy
 * @since 2025-08-01
 */
@Data
@Accessors(chain = true)
public class PasswordEncryptRequest {
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 明文密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
} 