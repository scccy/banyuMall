package com.origin.oss.service;

import com.origin.common.dto.FileUploadRequest;
import com.origin.common.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * OSS文件服务接口
 * 
 * @author scccy
 * @since 2025-01-27
 */
public interface OssFileService {

    /**
     * 上传文件到OSS
     * 
     * @param request 文件上传请求
     * @return 文件上传响应
     */
    FileUploadResponse uploadFile(FileUploadRequest request);

    /**
     * 获取文件访问URL
     * 
     * @param fileId 文件ID
     * @return 文件访问URL
     */
    String getFileAccessUrl(Long fileId);

    /**
     * 生成文件路径
     * 
     * @param sourceService 来源服务
     * @param businessType 业务类型
     * @return 文件路径
     */
    String generateFilePath(String sourceService, String businessType);

    /**
     * 删除文件
     * 
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteFile(Long fileId, Long userId);

    /**
     * 批量上传文件
     * 
     * @param files 文件列表
     * @param sourceService 来源服务
     * @param businessType 业务类型
     * @param userId 用户ID
     * @return 上传结果列表
     */
    java.util.List<FileUploadResponse> batchUploadFiles(java.util.List<MultipartFile> files, 
                                                       String sourceService, 
                                                       String businessType, 
                                                       Long userId);
} 