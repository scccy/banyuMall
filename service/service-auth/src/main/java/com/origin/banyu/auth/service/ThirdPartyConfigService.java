package com.origin.banyu.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.common.dto.ThirdPartyConfigQueryRequest;
import com.origin.banyu.common.entity.ThirdPartyConfig;

/**
 * 第三方平台配置服务接口
 * 
 * @author scccy
 * @since 2025-08-05
 */
public interface ThirdPartyConfigService {

    /**
     * 创建第三方平台配置
     * 
     * @param config 配置信息
     * @return 创建的配置
     */
    ThirdPartyConfig createConfig(ThirdPartyConfig config);

    /**
     * 更新第三方平台配置
     * 
     * @param configId 配置ID
     * @param config 配置信息
     * @return 更新后的配置
     */
    ThirdPartyConfig updateConfig(Integer configId, ThirdPartyConfig config);

    /**
     * 删除第三方平台配置
     * 
     * @param configId 配置ID
     * @return 是否删除成功
     */
    boolean deleteConfig(Integer configId);

    /**
     * 根据配置ID查询配置
     * 
     * @param configId 配置ID
     * @return 配置信息
     */
    ThirdPartyConfig getConfigById(Integer configId);

    /**
     * 根据平台类型查询配置
     * 
     * @param platformType 平台类型
     * @return 配置信息
     */
    ThirdPartyConfig getConfigByPlatformType(Integer platformType);

    /**
     * 分页查询第三方平台配置
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    IPage<ThirdPartyConfig> getConfigPage(ThirdPartyConfigQueryRequest request);

    /**
     * 更新配置状态
     * 
     * @param configId 配置ID
     * @param status 状态
     * @return 是否更新成功
     */
    boolean updateConfigStatus(Integer configId, Integer status);
} 