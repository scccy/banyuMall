package com.origin.user.feign;

import com.origin.common.dto.ResultData;
import com.origin.common.entity.ThirdPartyConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 第三方平台配置Feign客户端降级处理
 * 
 * @author scccy
 * @since 2025-08-05
 */
@Slf4j
@Component
public class ThirdPartyConfigFeignClientFallback implements ThirdPartyConfigFeignClient {

    @Override
    public ResultData<ThirdPartyConfig> getConfigByPlatformType(String platformType) {
        log.error("获取第三方平台配置失败，平台类型：{}，服务降级", platformType);
        return ResultData.fail("获取第三方平台配置失败，服务暂时不可用");
    }

    @Override
    public ResultData<ThirdPartyConfig> getConfigById(String configId) {
        log.error("获取第三方平台配置失败，配置ID：{}，服务降级", configId);
        return ResultData.fail("获取第三方平台配置失败，服务暂时不可用");
    }

    @Override
    public ResultData<String> test() {
        log.error("第三方平台配置服务健康检查失败，服务降级");
        return ResultData.fail("第三方平台配置服务暂时不可用");
    }
} 