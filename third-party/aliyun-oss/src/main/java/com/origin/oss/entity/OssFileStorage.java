package com.origin.oss.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * OSS文件存储记录实体
 * 
 * @author scccy
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("oss_file_storage")
public class OssFileStorage {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 原始文件名
     */
    @TableField("original_name")
    private String originalName;

    /**
     * 文件大小(字节)
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * MIME类型
     */
    @TableField("mime_type")
    private String mimeType;

    /**
     * OSS对象键
     */
    @TableField("object_key")
    private String objectKey;

    /**
     * 文件访问URL
     */
    @TableField("access_url")
    private String accessUrl;

    /**
     * OSS存储桶名称
     */
    @TableField("bucket_name")
    private String bucketName;

    /**
     * 文件路径(如:core-publisher/task-image/2025/08/01/)
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 来源服务(如:core-publisher)
     */
    @TableField("source_service")
    private String sourceService;

    /**
     * 业务类型(如:task-image)
     */
    @TableField("business_type")
    private String businessType;

    /**
     * 上传用户ID
     */
    @TableField("upload_user_id")
    private Long uploadUserId;

    /**
     * 上传用户名
     */
    @TableField("upload_user_name")
    private String uploadUserName;

    /**
     * 上传时间
     */
    @TableField("upload_time")
    private LocalDateTime uploadTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
} 