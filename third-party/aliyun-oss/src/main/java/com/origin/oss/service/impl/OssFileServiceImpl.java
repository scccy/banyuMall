package com.origin.oss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.origin.oss.config.OssConfig;
import com.origin.oss.dto.FileUploadRequest;
import com.origin.oss.dto.FileUploadResponse;
import com.origin.oss.entity.OssFileStorage;
import com.origin.oss.mapper.OssFileStorageMapper;
import com.origin.common.entity.ErrorCode;
import com.origin.common.exception.BusinessException;
import com.origin.oss.service.OssFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * OSS文件服务实现类
 * 
 * @author scccy
 * @since 2025-01-27
 */
@Slf4j
@Service
public class OssFileServiceImpl implements OssFileService {

    @Autowired
    private OssFileStorageMapper ossFileStorageMapper;

    @Autowired
    private com.aliyun.oss.OSS ossClient;
    
    @Autowired
    private OssConfig ossConfig;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final java.util.Set<String> ALLOWED_EXTENSIONS = java.util.Set.of(
        "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx"
    );

    @Override
    public FileUploadResponse uploadFile(FileUploadRequest request) {
        MultipartFile file = request.getFile();
        
        // 1. 文件大小检查
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
        
        // 2. 文件类型检查
        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件类型不允许，仅支持图片、PDF、文档等格式");
        }
        
        // 3. 生成安全的文件名
        String objectKey = generateSafeObjectName(request.getBusinessType(), extension);
        
        // 4. 上传到OSS (TODO: 实现OSS上传逻辑)
        String accessUrl = uploadToOss(objectKey, file);
        
        // 5. 保存文件记录到数据库
        OssFileStorage fileStorage = new OssFileStorage();
        fileStorage.setOriginalName(file.getOriginalFilename());
        fileStorage.setFileSize(file.getSize());
        fileStorage.setMimeType(file.getContentType());
        fileStorage.setObjectKey(objectKey);
        fileStorage.setAccessUrl(accessUrl);
        fileStorage.setBucketName("banyumall-files"); // TODO: 从配置获取
        fileStorage.setFilePath(request.getFilePath());
        fileStorage.setSourceService(request.getSourceService());
        fileStorage.setBusinessType(request.getBusinessType());
        fileStorage.setUploadUserId(request.getUploadUserId());
        fileStorage.setUploadUserName(request.getUploadUserName());
        fileStorage.setUploadTime(LocalDateTime.now());
        
        ossFileStorageMapper.insert(fileStorage);
        
        // 6. 构建响应
        FileUploadResponse response = new FileUploadResponse();
        response.setFileId(fileStorage.getId());
        response.setOriginalName(fileStorage.getOriginalName());
        response.setAccessUrl(fileStorage.getAccessUrl());
        response.setFileSize(fileStorage.getFileSize());
        response.setMimeType(fileStorage.getMimeType());
        response.setFilePath(fileStorage.getFilePath());
        response.setUploadStatus("SUCCESS");
        response.setMessage("文件上传成功");
        
        log.info("文件上传成功: fileId={}, originalName={}", fileStorage.getId(), fileStorage.getOriginalName());
        
        return response;
    }

    @Override
    public String getFileAccessUrl(Long fileId) {
        OssFileStorage fileStorage = ossFileStorageMapper.selectById(fileId);
        if (fileStorage == null) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        
        // TODO: 生成预签名URL
        return fileStorage.getAccessUrl();
    }

    @Override
    public String generateFilePath(String sourceService, String businessType) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("%s/%s/%s/", sourceService, businessType, date);
    }

    @Override
    public boolean deleteFile(Long fileId, Long userId) {
        OssFileStorage fileStorage = ossFileStorageMapper.selectById(fileId);
        if (fileStorage == null) {
            return false;
        }
        
        // 验证文件所有权
        if (!fileStorage.getUploadUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除此文件");
        }
        
        // TODO: 从OSS删除文件
        // ossClient.deleteObject(fileStorage.getBucketName(), fileStorage.getObjectKey());
        
        // 逻辑删除数据库记录
        ossFileStorageMapper.deleteById(fileId);
        
        log.info("文件删除成功: fileId={}, userId={}", fileId, userId);
        
        return true;
    }

    @Override
    public List<FileUploadResponse> batchUploadFiles(List<MultipartFile> files, 
                                                   String sourceService, 
                                                   String businessType, 
                                                   Long userId) {
        return files.stream()
            .map(file -> {
                FileUploadRequest request = new FileUploadRequest();
                request.setFile(file);
                request.setSourceService(sourceService);
                request.setBusinessType(businessType);
                request.setFilePath(generateFilePath(sourceService, businessType));
                request.setUploadUserId(userId);
                
                return uploadFile(request);
            })
            .collect(Collectors.toList());
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
    private String generateSafeObjectName(String businessType, String extension) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String uuid = UUID.randomUUID().toString();
        return String.format("%s/%s/%s.%s", businessType, date, uuid, extension);
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
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, "文件上传失败: " + e.getMessage());
        }
    }
} 