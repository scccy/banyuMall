package com.origin.banyu.user.feign;

import com.origin.banyu.common.dto.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
/**
 * OSS文件服务Feign客户端（极简版）
 */
@FeignClient(name = "third-party-aliyunOss", path = "/tp/oss", fallback = OssFileFeignClientFallback.class)
public interface OssFileFeignClient {
    /**
     * 上传文件到OSS并返回访问URL
     */
    @PostMapping("/upload")
    ResultData<String> uploadFile(@RequestPart("file") MultipartFile file);
}