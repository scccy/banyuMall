package com.origin.banyu.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.banyu.auth.mapper.ThirdPartyConfigMapper;
import com.origin.banyu.auth.service.ThirdPartyConfigService;
import com.origin.banyu.common.dto.ThirdPartyConfigQueryRequest;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.entity.ThirdPartyConfig;
import com.origin.banyu.common.exception.BusinessException;
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
    public ThirdPartyConfig createConfig(ThirdPartyConfig config) {
        log.info("创建第三方平台配置，平台类型：{}", config.getPlatformType());
        // 检查平台类型是否已存在
        ThirdPartyConfig existingConfig = thirdPartyConfigMapper.selectByPlatformType(config.getPlatformType());
        if (existingConfig != null) {
            throw new BusinessException(ErrorCode.CONFLICT, "平台类型已存在：" + config.getPlatformType());
        }
        // 创建新配置
        ThirdPartyConfig newConfig = new ThirdPartyConfig();
        BeanUtils.copyProperties(config, newConfig);
        // 设置默认值
        if (newConfig.getConfigStatus() == null) {
            newConfig.setConfigStatus(1); // 默认启用
        }
        thirdPartyConfigMapper.insert(newConfig);
        log.info("第三方平台配置创建成功，配置ID：{}", newConfig.getConfigId());
        return newConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ThirdPartyConfig updateConfig(Integer configId, ThirdPartyConfig config) {
        log.info("更新第三方平台配置，配置ID：{}", configId);
        // 检查配置是否存在
        ThirdPartyConfig existingConfig = thirdPartyConfigMapper.selectById(configId);
        if (existingConfig == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "配置不存在：" + configId);
        }
        // 更新配置
        BeanUtils.copyProperties(config, existingConfig);
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
            throw new BusinessException(ErrorCode.NOT_FOUND, "配置不存在：" + configId);
        }
        // 逻辑删除
        int result = thirdPartyConfigMapper.deleteById(configId);
        log.info("第三方平台配置删除成功，配置ID：{}", configId);
        return result > 0;
    }

    @Override
    public ThirdPartyConfig getConfigById(Integer configId) {
        log.debug("根据配置ID查询配置，配置ID：{}", configId);
        ThirdPartyConfig config = thirdPartyConfigMapper.selectById(configId);
        if (config == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "配置不存在：" + configId);
        }
        if (config.getPlatformConfig() != null) {
            // 根据platformType转换platformConfig为具体的DTO类型
            Object parsedConfig = com.origin.banyu.common.util.ThirdPartyConfigParser.parseConfigByType(
                config.getPlatformType(), 
                config.getPlatformConfig().toString()
            );
            config.setPlatformConfig(parsedConfig);
        }
        return config;
    }

    @Override
    public ThirdPartyConfig getConfigByPlatformType(Integer platformType) {
        log.debug("根据平台类型查询配置，平台类型：{}", platformType);
        ThirdPartyConfig config = thirdPartyConfigMapper.selectByPlatformType(platformType);
        if (config == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "平台类型配置不存在或已禁用：" + platformType);
        }
        if (config.getPlatformConfig() != null) {
            // 根据platformType转换platformConfig为具体的DTO类型
            Object parsedConfig = com.origin.banyu.common.util.ThirdPartyConfigParser.parseConfigByType(
                platformType, 
                config.getPlatformConfig().toString()
            );
            config.setPlatformConfig(parsedConfig);
        }
        return config;
    }



    @Override
    public IPage<ThirdPartyConfig> getConfigPage(ThirdPartyConfigQueryRequest request) {
        log.debug("分页查询第三方平台配置，参数：{}", request);
        Page<ThirdPartyConfig> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<ThirdPartyConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(request.getPlatformType() != null, ThirdPartyConfig::getPlatformType, request.getPlatformType());
        queryWrapper.like(StringUtils.hasText(request.getPlatformName()), ThirdPartyConfig::getPlatformName, request.getPlatformName());
        queryWrapper.eq(request.getConfigStatus() != null, ThirdPartyConfig::getConfigStatus, request.getConfigStatus());
        queryWrapper.eq(ThirdPartyConfig::getDeleted, 0);
        queryWrapper.orderByDesc(ThirdPartyConfig::getCreatedTime);
        return thirdPartyConfigMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfigStatus(Integer configId, Integer status) {
        log.info("更新第三方平台配置状态，配置ID：{}，状态：{}", configId, status);
        ThirdPartyConfig config = thirdPartyConfigMapper.selectById(configId);
        if (config == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "配置不存在：" + configId);
        }
        config.setConfigStatus(status);
        int result = thirdPartyConfigMapper.updateById(config);
        log.info("第三方平台配置状态更新成功，配置ID：{}，状态：{}", configId, status);
        return result > 0;
    }
    

} 