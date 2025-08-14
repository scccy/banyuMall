package com.origin.banyu.user.feign;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.dto.ThirdPartyConfigQueryRequest;

import com.origin.banyu.common.entity.ThirdPartyConfig;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
/**
 * 认证服务Feign客户端
 * 
 * @author scccy
 * @since 2025-08-05
 */
@FeignClient(name = "service-auth", path = "/service/auth", fallback = AuthFeignClientFallback.class)
public interface AuthFeignClient {

    /**
     * 创建第三方平台配置
     * 
     * @param config 配置信息
     * @return 创建结果
     */
    @PostMapping("/tp/config")
    ResultData<ThirdPartyConfig> createConfig(@RequestBody ThirdPartyConfig config);

    /**
     * 更新第三方平台配置
     * 
     * @param configId 配置ID
     * @param config 配置信息
     * @return 更新结果
     */
    @PutMapping("/tp/config/{configId}")
    ResultData<ThirdPartyConfig> updateConfig(@PathVariable("configId") Integer configId,
                                             @RequestBody ThirdPartyConfig config);

    /**
     * 删除第三方平台配置
     * 
     * @param configId 配置ID
     * @return 删除结果
     */
    @DeleteMapping("/tp/config/{configId}")
    ResultData<Boolean> deleteConfig(@PathVariable("configId") Integer configId);

    /**
     * 根据配置ID查询配置
     * 
     * @param configId 配置ID
     * @return 配置信息
     */
    @GetMapping("/tp/config/{configId}")
    ResultData<ThirdPartyConfig> getConfigById(@PathVariable("configId") Integer configId);

    /**
     * 根据平台类型查询配置
     * 
     * @param platformType 平台类型
     * @return 配置信息
     */
    @GetMapping("/tp/config/platform/{platformType}")
    ResultData<ThirdPartyConfig> getConfigByPlatformType(@PathVariable("platformType") Integer platformType);

    /**
     * 分页查询第三方平台配置
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    @GetMapping("/tp/config/list")
    ResultData<IPage<ThirdPartyConfig>> getConfigPage(@ModelAttribute ThirdPartyConfigQueryRequest request);

    /**
     * 更新配置状态
     * 
     * @param configId 配置ID
     * @param status 状态
     * @return 更新结果
     */
    @PutMapping("/tp/config/{configId}/status")
    ResultData<Boolean> updateConfigStatus(@PathVariable("configId") Integer configId,
                                          @RequestParam("status") Integer status);


} 