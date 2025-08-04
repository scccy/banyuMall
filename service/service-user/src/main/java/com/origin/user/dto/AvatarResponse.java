package com.origin.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

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
     * 头像缩略图URL
     */
    private String avatarThumbnail;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * MIME类型
     */
    private String mimeType;
    
    /**
     * 原始文件名
     */
    private String originalFileName;
    
    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;
} 