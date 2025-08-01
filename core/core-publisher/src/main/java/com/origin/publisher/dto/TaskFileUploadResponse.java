package com.origin.publisher.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 任务文件上传响应DTO
 * 
 * @author scccy
 * @since 2025-08-01
 */
@Data
@Accessors(chain = true)
public class TaskFileUploadResponse {
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 文件URL
     */
    private String fileUrl;
    
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