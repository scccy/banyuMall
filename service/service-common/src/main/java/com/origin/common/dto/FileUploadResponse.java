package com.origin.common.dto;

import lombok.Data;

/**
 * 文件上传响应DTO
 * 
 * @author scccy
 * @since 2025-01-27
 */
@Data
public class FileUploadResponse {

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件访问URL
     */
    private String accessUrl;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * MIME类型
     */
    private String mimeType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 上传状态
     */
    private String uploadStatus;

    /**
     * 消息
     */
    private String message;
} 