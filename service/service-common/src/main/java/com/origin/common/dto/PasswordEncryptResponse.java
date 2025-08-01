package com.origin.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 密码加密响应DTO
 * 
 * @author scccy
 * @since 2025-08-01
 */
@Data
@Accessors(chain = true)
public class PasswordEncryptResponse {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 加密后的密码
     */
    private String encryptedPassword;
} 