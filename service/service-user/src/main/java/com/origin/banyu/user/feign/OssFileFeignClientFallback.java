package com.origin.banyu.user.feign;

import com.origin.banyu.common.dto.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
/**
 * OSS文件服务Feign客户端降级处理（极简版）
 */
@Slf4j
@Component
public class OssFileFeignClientFallback implements OssFileFeignClient {
    @Override
    public ResultData<String> uploadFile(MultipartFile file) {
        log.error("OSS文件上传服务调用失败，触发降级处理");
        return ResultData.fail("OSS服务暂时不可用，请稍后重试");
    }
}