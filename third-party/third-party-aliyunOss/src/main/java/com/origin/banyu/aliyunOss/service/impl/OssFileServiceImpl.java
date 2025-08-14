package com.origin.banyu.aliyunOss.service.impl;

import com.origin.banyu.aliyunOss.config.OssConfig;
import com.origin.banyu.aliyunOss.entity.OssUploadLog;
import com.origin.banyu.aliyunOss.exception.FileSizeExceededException;
import com.origin.banyu.aliyunOss.exception.FileTypeNotAllowedException;
import com.origin.banyu.aliyunOss.exception.OssServiceException;
import com.origin.banyu.aliyunOss.mapper.OssUploadLogMapper;
import com.origin.banyu.aliyunOss.service.OssFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * OSS文件服务实现类
 * 
 * @author scccy
 * @since 2025-07-31
 */
@Slf4j
@Service
public class OssFileServiceImpl implements OssFileService {

    @Autowired
    private OssUploadLogMapper ossUploadLogMapper;
    @Autowired
    private com.aliyun.oss.OSS ossClient;
    
    @Autowired
    private OssConfig ossConfig;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final java.util.Set<String> ALLOWED_EXTENSIONS = java.util.Set.of(
        "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx"
    );

    @Override
    public String uploadFile(MultipartFile file) {
        
        // 1. 文件大小检查
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException("文件大小超过限制，最大支持10MB");
        }
        
        // 2. 文件类型检查
        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new FileTypeNotAllowedException("文件类型不允许，仅支持图片、PDF、文档等格式");
        }
        
        // 3. 生成对象路径与文件名，构造objectKey（目录/文件名）
        String objectPath = generateDatePath();
        String fileName = generateSafeFileName(extension);
        String objectKey = objectPath + "/" + fileName;
        
        // 4. 上传到OSS并生成访问URL
        String accessUrl = uploadToOss(objectKey, file);

        // 5. 记录上传流水（含文件名与路径）
        OssUploadLog logRecord = new OssUploadLog();
        logRecord.setLogId(UUID.randomUUID().toString().replace("-", ""));
        logRecord.setOriginalName(file.getOriginalFilename());
        logRecord.setObjectPath(objectPath);
        logRecord.setFileName(fileName);
        logRecord.setObjectKey(objectKey);
        logRecord.setAccessUrl(accessUrl);
        logRecord.setDeleted(0);
        logRecord.setCreatedTime(java.time.LocalDateTime.now());
        logRecord.setUpdatedTime(java.time.LocalDateTime.now());
        ossUploadLogMapper.insert(logRecord);

        log.info("文件上传成功: objectKey={}, accessUrl={}", objectKey, accessUrl);
        return accessUrl;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 生成安全的对象名
     */
    private String generateSafeFileName(String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return String.format("%s.%s", uuid, extension);
    }

    private String generateDatePath() {
        java.time.LocalDate now = java.time.LocalDate.now();
        return String.format("uploads/%04d/%02d/%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
    }

    /**
     * 上传文件到OSS
     */
    private String uploadToOss(String objectKey, MultipartFile file) {
        try {
            log.info("开始上传文件到OSS: objectKey={}", objectKey);
            
            // 上传文件到OSS
            ossClient.putObject(ossConfig.getBucketName(), objectKey, file.getInputStream());
            
            // 生成访问URL
            String accessUrl = String.format("https://%s.%s/%s", 
                ossConfig.getBucketName(), 
                ossConfig.getEndpoint(), 
                objectKey);
            
            log.info("文件上传到OSS成功: objectKey={}, accessUrl={}", objectKey, accessUrl);
            
            return accessUrl;
        } catch (Exception e) {
            log.error("文件上传到OSS失败: objectKey={}, error={}", objectKey, e.getMessage(), e);
            throw new OssServiceException("OSS服务异常 上传失败", e);
        }
    }
} 