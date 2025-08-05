package com.origin.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.auth.dto.ThirdPartyConfigDTO;
import com.origin.auth.dto.ThirdPartyConfigQueryRequest;
import com.origin.common.entity.ThirdPartyConfig;

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
     * @param configDTO 配置信息
     * @return 创建结果
     */
    ThirdPartyConfig createConfig(ThirdPartyConfigDTO configDTO);

    /**
     * 更新第三方平台配置
     * 
     * @param configId 配置ID
     * @param configDTO 配置信息
     * @return 更新结果
     */
    ThirdPartyConfig updateConfig(Integer configId, ThirdPartyConfigDTO configDTO);

    /**
     * 删除第三方平台配置
     * 
     * @param configId 配置ID
     * @return 删除结果
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
     * 分页查询配置列表
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    IPage<ThirdPartyConfig> getConfigPage(ThirdPartyConfigQueryRequest request);

    /**
     * 启用/禁用配置
     * 
     * @param configId 配置ID
     * @param status 状态：0-禁用，1-启用
     * @return 操作结果
     */
    boolean updateConfigStatus(Integer configId, Integer status);
} 