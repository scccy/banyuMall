package com.origin.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.origin.user.entity.UserConfig;

import java.util.List;
import java.util.Map;

/**
 * 用户配置服务接口
 *
 * @author origin
 * @since 2025-01-27
 */
public interface UserConfigService extends IService<UserConfig> {

    /**
     * 根据用户ID获取所有配置
     *
     * @param userId 用户ID
     * @return 配置列表
     */
    List<UserConfig> getByUserId(String userId);

    /**
     * 根据用户ID获取配置Map
     *
     * @param userId 用户ID
     * @return 配置Map
     */
    Map<String, String> getConfigMap(String userId);

    /**
     * 根据用户ID和配置键获取配置值
     *
     * @param userId 用户ID
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String userId, String configKey);

    /**
     * 设置用户配置
     *
     * @param userId 用户ID
     * @param configKey 配置键
     * @param configValue 配置值
     * @param configType 配置类型
     * @return 是否成功
     */
    boolean setConfig(String userId, String configKey, String configValue, String configType);

    /**
     * 批量设置用户配置
     *
     * @param userId 用户ID
     * @param configMap 配置Map
     * @return 是否成功
     */
    boolean setConfigs(String userId, Map<String, String> configMap);
} 