package com.origin.banyu.user.feign;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.common.entity.ThirdPartyConfig;
import com.origin.banyu.common.dto.ThirdPartyConfigQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.origin.banyu.common.dto.ResultData;
/**
 * 认证服务Feign客户端降级处理
 * 
 * @author scccy
 * @since 2025-08-05
 */
@Slf4j
@Component
public class AuthFeignClientFallback implements AuthFeignClient {

    @Override
    public ResultData<ThirdPartyConfig> createConfig(ThirdPartyConfig config) {
        log.error("创建第三方平台配置失败，服务降级");
        return ResultData.fail("服务暂时不可用");
    }

    @Override
    public ResultData<ThirdPartyConfig> updateConfig(Integer configId, ThirdPartyConfig config) {
        log.error("更新第三方平台配置失败，服务降级，配置ID：{}", configId);
        return ResultData.fail("服务暂时不可用");
    }

    @Override
    public ResultData<Boolean> deleteConfig(Integer configId) {
        log.error("删除第三方平台配置失败，服务降级，配置ID：{}", configId);
        return ResultData.fail("服务暂时不可用");
    }

    @Override
    public ResultData<ThirdPartyConfig> getConfigById(Integer configId) {
        log.error("查询第三方平台配置失败，服务降级，配置ID：{}", configId);
        return ResultData.fail("服务暂时不可用");
    }

    @Override
    public ResultData<ThirdPartyConfig> getConfigByPlatformType(Integer platformType) {
        log.error("根据平台类型查询配置失败，服务降级，平台类型：{}", platformType);
        return ResultData.fail("服务暂时不可用");
    }

    @Override
    public ResultData<IPage<ThirdPartyConfig>> getConfigPage(ThirdPartyConfigQueryRequest request) {
        log.error("分页查询第三方平台配置失败，服务降级");
        return ResultData.fail("服务暂时不可用");
    }

    @Override
    public ResultData<Boolean> updateConfigStatus(Integer configId, Integer status) {
        log.error("更新配置状态失败，服务降级，配置ID：{}，状态：{}", configId, status);
        return ResultData.fail("服务暂时不可用");
    }
} 