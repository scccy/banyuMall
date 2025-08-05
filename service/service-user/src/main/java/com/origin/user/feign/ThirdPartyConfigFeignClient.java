package com.origin.user.feign;

import com.origin.common.dto.ResultData;
import com.origin.common.entity.ThirdPartyConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 第三方平台配置Feign客户端
 * 示例：展示如何使用common模块中的ThirdPartyConfig实体类
 * 
 * @author scccy
 * @since 2025-08-05
 */
@FeignClient(name = "service-auth", fallback = ThirdPartyConfigFeignClientFallback.class)
public interface ThirdPartyConfigFeignClient {

    /**
     * 根据平台类型查询配置
     * 
     * @param platformType 平台类型
     * @return 配置信息
     */
    @GetMapping("/service/auth/third-party/config/platform/{platformType}")
    ResultData<ThirdPartyConfig> getConfigByPlatformType(@PathVariable("platformType") String platformType);

    /**
     * 根据配置ID查询配置
     * 
     * @param configId 配置ID
     * @return 配置信息
     */
    @GetMapping("/service/auth/third-party/config/{configId}")
    ResultData<ThirdPartyConfig> getConfigById(@PathVariable("configId") String configId);

    /**
     * 健康检查
     * 
     * @return 服务状态
     */
    @GetMapping("/service/auth/third-party/config/test")
    ResultData<String> test();
} 