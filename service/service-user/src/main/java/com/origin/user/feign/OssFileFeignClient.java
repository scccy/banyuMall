package com.origin.user.feign;

import com.origin.common.dto.ResultData;
import com.origin.common.dto.AliyunOssFileUploadRequest;
import com.origin.common.dto.AliyunOssFileUploadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * OSS文件服务Feign客户端
 * 
 * @author scccy
 * @since 2025-08-01
 */
@FeignClient(name = "third-party-aliyunOss", path = "/tp/oss", fallback = OssFileFeignClientFallback.class)
public interface OssFileFeignClient {
    
    /**
     * 上传文件到OSS
     *
     * @param request 文件上传请求
     * @return 上传结果
     */
    @PostMapping("/upload")
    ResultData<AliyunOssFileUploadResponse> uploadFile(@RequestBody AliyunOssFileUploadRequest request);
    
    /**
     * 获取文件访问URL
     *
     * @param fileId 文件ID
     * @return 文件访问URL
     */
    @GetMapping("/url/{fileId}")
    ResultData<String> getFileUrl(@PathVariable("fileId") String fileId);
} 