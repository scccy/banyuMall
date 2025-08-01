package com.origin.publisher.feign;

import com.origin.common.dto.ResultData;
import com.origin.common.dto.FileUploadRequest;
import com.origin.common.dto.FileUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * OSS文件服务Feign客户端降级处理
 * 
 * @author scccy
 * @since 2025-08-01
 */
@Slf4j
@Component
public class OssFileFeignClientFallback implements OssFileFeignClient {
    
    @Override
    public ResultData<FileUploadResponse> uploadFile(FileUploadRequest request) {
        log.error("OSS文件上传服务调用失败，触发降级处理");
        return ResultData.fail("OSS服务暂时不可用，请稍后重试");
    }
    
    @Override
    public ResultData<String> getFileUrl(String fileId) {
        log.error("OSS文件URL获取服务调用失败，触发降级处理，fileId: {}", fileId);
        return ResultData.fail("OSS服务暂时不可用，请稍后重试");
    }
} 