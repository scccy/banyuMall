package com.origin.common.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文件上传响应DTO
 * 
 * @author scccy
 * @since 2025-07-31
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
     * 对象键（OSS中的唯一标识）
     */
    private String objectKey;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 来源服务
     */
    private String sourceService;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 上传用户ID
     */
    private Long uploadUserId;

    /**
     * 上传用户名
     */
    private String uploadUserName;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 上传状态
     */
    private String uploadStatus;

    /**
     * 消息
     */
    private String message;
} 