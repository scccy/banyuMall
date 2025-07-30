package com.origin.user.controller;

import com.origin.common.ResultData;
import com.origin.user.entity.UserConfig;
import com.origin.user.service.UserConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户配置控制器
 *
 * @author origin
 * @since 2025-01-27
 */
@Tag(name = "用户配置管理", description = "用户配置相关接口")
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class UserConfigController {

    private final UserConfigService userConfigService;

    @Operation(summary = "获取用户所有配置")
    @GetMapping("/{userId}")
    public ResultData<List<UserConfig>> getUserConfigs(@PathVariable String userId) {
        List<UserConfig> configs = userConfigService.getByUserId(userId);
        return ResultData.success(configs);
    }

    @Operation(summary = "获取用户配置Map")
    @GetMapping("/{userId}/map")
    public ResultData<Map<String, String>> getUserConfigMap(@PathVariable String userId) {
        Map<String, String> configMap = userConfigService.getConfigMap(userId);
        return ResultData.success(configMap);
    }

    @Operation(summary = "获取用户指定配置")
    @GetMapping("/{userId}/{configKey}")
    public ResultData<String> getUserConfigValue(
            @PathVariable String userId,
            @PathVariable String configKey) {
        String value = userConfigService.getConfigValue(userId, configKey);
        return ResultData.success(value);
    }

    @Operation(summary = "设置用户配置")
    @PostMapping("/{userId}/{configKey}")
    public ResultData<Boolean> setUserConfig(
            @PathVariable String userId,
            @PathVariable String configKey,
            @RequestParam String configValue,
            @RequestParam(defaultValue = "string") String configType) {
        
        boolean success = userConfigService.setConfig(userId, configKey, configValue, configType);
        return success ? ResultData.success(true) : ResultData.error("操作失败");
    }

    @Operation(summary = "批量设置用户配置")
    @PostMapping("/{userId}/batch")
    public ResultData<Boolean> setUserConfigs(
            @PathVariable String userId,
            @RequestBody Map<String, String> configMap) {
        
        boolean success = userConfigService.setConfigs(userId, configMap);
        return success ? ResultData.success(true) : ResultData.error("操作失败");
    }

    @Operation(summary = "删除用户配置")
    @DeleteMapping("/{userId}/{configKey}")
    public ResultData<Boolean> deleteUserConfig(
            @PathVariable String userId,
            @PathVariable String configKey) {
        
        UserConfig config = userConfigService.lambdaQuery()
                .eq(UserConfig::getUserId, userId)
                .eq(UserConfig::getConfigKey, configKey)
                .one();
        
        if (config == null) {
            return ResultData.error("配置不存在");
        }
        
        boolean success = userConfigService.removeById(config.getId());
        return success ? ResultData.success(true) : ResultData.error("删除失败");
    }
} 