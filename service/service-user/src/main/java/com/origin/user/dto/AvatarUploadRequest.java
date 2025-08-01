package com.origin.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

/**
 * 头像上传请求DTO
 * 
 * @author scccy
 * @since 2025-08-01
 */
@Data
@Accessors(chain = true)
public class AvatarUploadRequest {
    
    /**
     * 头像文件
     */
    @NotNull(message = "头像文件不能为空")
    private MultipartFile file;
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private String userId;
} 