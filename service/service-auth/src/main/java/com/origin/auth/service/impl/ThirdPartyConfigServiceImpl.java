package com.origin.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.auth.dto.ThirdPartyConfigDTO;
import com.origin.auth.dto.ThirdPartyConfigQueryRequest;
import com.origin.common.entity.ThirdPartyConfig;
import com.origin.auth.mapper.ThirdPartyConfigMapper;
import com.origin.auth.service.ThirdPartyConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 第三方平台配置服务实现类
 * 
 * @author scccy
 * @since 2025-08-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThirdPartyConfigServiceImpl implements ThirdPartyConfigService {

    private final ThirdPartyConfigMapper thirdPartyConfigMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ThirdPartyConfig createConfig(ThirdPartyConfigDTO configDTO) {
        log.info("创建第三方平台配置，平台类型：{}", configDTO.getPlatformType());
        
        // 检查平台类型是否已存在
        ThirdPartyConfig existingConfig = thirdPartyConfigMapper.selectByPlatformType(configDTO.getPlatformType());
        if (existingConfig != null) {
            throw new RuntimeException("平台类型已存在：" + configDTO.getPlatformType());
        }

        // 创建新配置
        ThirdPartyConfig config = new ThirdPartyConfig();
        BeanUtils.copyProperties(configDTO, config);
        
        // 设置默认值
        if (config.getConfigStatus() == null) {
            config.setConfigStatus(1); // 默认启用
        }
        
        thirdPartyConfigMapper.insert(config);
        log.info("第三方平台配置创建成功，配置ID：{}", config.getConfigId());
        
        return config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ThirdPartyConfig updateConfig(Integer configId, ThirdPartyConfigDTO configDTO) {
        log.info("更新第三方平台配置，配置ID：{}", configId);
        
        // 检查配置是否存在
        ThirdPartyConfig existingConfig = thirdPartyConfigMapper.selectById(configId);
        if (existingConfig == null) {
            throw new RuntimeException("配置不存在：" + configId);
        }

        // 更新配置
        BeanUtils.copyProperties(configDTO, existingConfig);
        existingConfig.setConfigId(configId); // 确保ID不被覆盖
        
        thirdPartyConfigMapper.updateById(existingConfig);
        log.info("第三方平台配置更新成功，配置ID：{}", configId);
        
        return existingConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteConfig(Integer configId) {
        log.info("删除第三方平台配置，配置ID：{}", configId);
        
        // 检查配置是否存在
        ThirdPartyConfig existingConfig = thirdPartyConfigMapper.selectById(configId);
        if (existingConfig == null) {
            throw new RuntimeException("配置不存在：" + configId);
        }

        // 逻辑删除
        int result = thirdPartyConfigMapper.deleteById(configId);
        log.info("第三方平台配置删除成功，配置ID：{}", configId);
        
        return result > 0;
    }

    @Override
    public ThirdPartyConfig getConfigById(Integer configId) {
        log.debug("根据配置ID查询配置，配置ID：{}", configId);
        return thirdPartyConfigMapper.selectById(configId);
    }

    @Override
    public ThirdPartyConfig getConfigByPlatformType(Integer platformType) {
        log.debug("根据平台类型查询配置，平台类型：{}", platformType);
        return thirdPartyConfigMapper.selectByPlatformType(platformType);
    }

    @Override
    public IPage<ThirdPartyConfig> getConfigPage(ThirdPartyConfigQueryRequest request) {
        log.debug("分页查询第三方平台配置，请求参数：{}", request);
        
        // 构建查询条件
        LambdaQueryWrapper<ThirdPartyConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ThirdPartyConfig::getDeleted, 0);
        
        if (request.getPlatformType() != null) {
            queryWrapper.eq(ThirdPartyConfig::getPlatformType, request.getPlatformType());
        }
        
        if (StringUtils.hasText(request.getPlatformName())) {
            queryWrapper.like(ThirdPartyConfig::getPlatformName, request.getPlatformName());
        }
        
        if (request.getConfigStatus() != null) {
            queryWrapper.eq(ThirdPartyConfig::getConfigStatus, request.getConfigStatus());
        }
        
        queryWrapper.orderByDesc(ThirdPartyConfig::getCreatedTime);
        
        // 分页查询
        Page<ThirdPartyConfig> page = new Page<>(request.getCurrent(), request.getSize());
        IPage<ThirdPartyConfig> result = thirdPartyConfigMapper.selectPage(page, queryWrapper);
        
        log.debug("分页查询第三方平台配置完成，总记录数：{}", result.getTotal());
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfigStatus(Integer configId, Integer status) {
        log.info("更新第三方平台配置状态，配置ID：{}，状态：{}", configId, status);
        
        // 检查配置是否存在
        ThirdPartyConfig existingConfig = thirdPartyConfigMapper.selectById(configId);
        if (existingConfig == null) {
            throw new RuntimeException("配置不存在：" + configId);
        }

        // 更新状态
        existingConfig.setConfigStatus(status);
        int result = thirdPartyConfigMapper.updateById(existingConfig);
        log.info("第三方平台配置状态更新成功，配置ID：{}，状态：{}", configId, status);
        
        return result > 0;
    }
} 