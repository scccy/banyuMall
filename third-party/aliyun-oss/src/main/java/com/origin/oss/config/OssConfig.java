package com.origin.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS配置类
 * 
 * @author scccy
 * @since 2025-01-27
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssConfig {

    /**
     * OSS端点
     */
    private String endpoint = "oss-cn-hangzhou.aliyuncs.com";

    /**
     * 访问密钥ID
     */
    private String accessKeyId;

    /**
     * 访问密钥Secret
     */
    private String accessKeySecret;

    /**
     * 存储桶名称
     */
    private String bucketName = "banyumall-files";

    /**
     * 文件上传配置
     */
    private UploadConfig upload = new UploadConfig();

    /**
     * 文件访问配置
     */
    private AccessConfig access = new AccessConfig();

    /**
     * 文件上传配置
     */
    @Data
    public static class UploadConfig {
        /**
         * 最大文件大小
         */
        private long maxFileSize = 10 * 1024 * 1024; // 10MB

        /**
         * 允许的文件类型
         */
        private String[] allowedFileTypes = {
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
        };

        /**
         * 图片处理配置
         */
        private ImageConfig image = new ImageConfig();
    }

    /**
     * 图片处理配置
     */
    @Data
    public static class ImageConfig {
        /**
         * 是否启用图片处理
         */
        private boolean resizeEnabled = true;

        /**
         * 最大宽度
         */
        private int maxWidth = 4096;

        /**
         * 最大高度
         */
        private int maxHeight = 4096;

        /**
         * 图片质量
         */
        private int quality = 85;

        /**
         * 图片格式
         */
        private String format = "JPEG";

        /**
         * 是否启用水印
         */
        private boolean watermarkEnabled = false;

        /**
         * 水印文字
         */
        private String watermarkText = "BanyuMall";

        /**
         * 水印位置
         */
        private String watermarkPosition = "BOTTOM_RIGHT";
    }

    /**
     * 文件访问配置
     */
    @Data
    public static class AccessConfig {
        /**
         * URL过期时间(秒)
         */
        private long urlExpireSeconds = 3600;

        /**
         * 是否启用私有访问
         */
        private boolean privateAccessEnabled = true;

        /**
         * 是否启用Referer检查
         */
        private boolean refererCheckEnabled = true;

        /**
         * 允许的Referer
         */
        private String[] allowedReferers = {
            "*.banyumall.com",
            "*.example.com"
        };
    }
} 