package com.origin.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 头像响应DTO
 * 
 * @author scccy
 * @since 2025-08-01
 */
@Data
@Accessors(chain = true)
public class AvatarResponse {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String mimeType;
    
    /**
     * 原始文件名
     */
    private String originalFileName;
} 