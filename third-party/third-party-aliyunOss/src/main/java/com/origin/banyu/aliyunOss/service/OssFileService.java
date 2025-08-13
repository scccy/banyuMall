package com.origin.banyu.aliyunOss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * OSS文件服务接口（极简版，仅上传并返回URL）
 */

public interface OssFileService {

    /**
     * 上传文件到OSS并返回访问URL
     *
     * @param file 上传的文件
     * @return 访问URL
     */
    String uploadFile(MultipartFile file);
}