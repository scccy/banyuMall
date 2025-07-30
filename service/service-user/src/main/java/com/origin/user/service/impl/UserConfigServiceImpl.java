package com.origin.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.user.entity.UserConfig;
import com.origin.user.mapper.UserConfigMapper;
import com.origin.user.service.UserConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户配置服务实现类
 *
 * @author origin
 * @since 2025-01-27
 */
@Service
public class UserConfigServiceImpl extends ServiceImpl<UserConfigMapper, UserConfig> implements UserConfigService {

    @Override
    public List<UserConfig> getByUserId(String userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public Map<String, String> getConfigMap(String userId) {
        List<UserConfig> configs = getByUserId(userId);
        Map<String, String> configMap = new HashMap<>();
        
        for (UserConfig config : configs) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        
        return configMap;
    }

    @Override
    public String getConfigValue(String userId, String configKey) {
        UserConfig config = baseMapper.selectByUserIdAndKey(userId, configKey);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setConfig(String userId, String configKey, String configValue, String configType) {
        // 查询是否已存在
        UserConfig existingConfig = baseMapper.selectByUserIdAndKey(userId, configKey);
        
        if (existingConfig != null) {
            // 更新
            existingConfig.setConfigValue(configValue);
            existingConfig.setConfigType(configType);
            return updateById(existingConfig);
        } else {
            // 创建
            UserConfig newConfig = new UserConfig();
            newConfig.setUserId(userId);
            newConfig.setConfigKey(configKey);
            newConfig.setConfigValue(configValue);
            newConfig.setConfigType(configType != null ? configType : "string");
            return save(newConfig);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setConfigs(String userId, Map<String, String> configMap) {
        boolean success = true;
        
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            if (!setConfig(userId, entry.getKey(), entry.getValue(), "string")) {
                success = false;
            }
        }
        
        return success;
    }
} 