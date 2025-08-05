package com.origin.aliyunOss.feign;

import com.origin.common.dto.ResultData;
import com.origin.common.dto.AliyunOssFileUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * OSS文件服务Feign客户端降级处理类
 * 
 * @author scccy
 * @since 2025-07-31
 */
@Slf4j
@Component
public class OssFileFeignClientFallback implements OssFileFeignClient {

    @Override
    public ResultData<AliyunOssFileUploadResponse> uploadFile(MultipartFile file, String sourceService, 
                                                   String businessType, String filePath, 
                                                   Long uploadUserId, String uploadUserName) {
        log.error("OSS文件上传服务调用失败，触发降级处理");
        return ResultData.fail("文件上传服务暂时不可用，请稍后重试");
    }

    @Override
    public ResultData<String> getFileAccessUrl(Long fileId) {
        log.error("OSS文件访问URL服务调用失败，触发降级处理，文件ID: {}", fileId);
        return ResultData.fail("文件访问服务暂时不可用，请稍后重试");
    }

    @Override
    public ResultData<Boolean> deleteFile(Long fileId, Long userId) {
        log.error("OSS文件删除服务调用失败，触发降级处理，文件ID: {}, 用户ID: {}", fileId, userId);
        return ResultData.fail("文件删除服务暂时不可用，请稍后重试");
    }

    @Override
    public ResultData<List<AliyunOssFileUploadResponse>> batchUploadFiles(List<MultipartFile> files, 
                                                               String sourceService, 
                                                               String businessType, Long userId) {
        log.error("OSS批量文件上传服务调用失败，触发降级处理");
        return ResultData.fail("批量文件上传服务暂时不可用，请稍后重试");
    }

    @Override
    public ResultData<String> generateFilePath(String sourceService, String businessType) {
        log.error("OSS文件路径生成服务调用失败，触发降级处理");
        return ResultData.fail("文件路径生成服务暂时不可用，请稍后重试");
    }
} 