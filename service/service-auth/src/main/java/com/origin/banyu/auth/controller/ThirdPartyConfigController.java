package com.origin.banyu.auth.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.auth.service.ThirdPartyConfigService;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.dto.ThirdPartyConfigQueryRequest;
import com.origin.banyu.common.entity.ThirdPartyConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 第三方平台配置控制器（精简版）
 * 
 * @author scccy
 * @since 2025-08-06
 */
@Slf4j
@RestController
@RequestMapping("/service/auth/tp/config")
@RequiredArgsConstructor
@Validated
@Tag(name = "第三方平台配置管理", description = "提供第三方平台配置的增删改查功能")
public class ThirdPartyConfigController {

    private final ThirdPartyConfigService thirdPartyConfigService;

    @PostMapping
    @Operation(summary = "创建第三方平台配置", description = "创建新的第三方平台配置")
    public ResultData<ThirdPartyConfig> createConfig(@Valid @RequestBody ThirdPartyConfig config) {
        ThirdPartyConfig createdConfig = thirdPartyConfigService.createConfig(config);
        return ResultData.success("第三方平台配置创建成功", createdConfig);
    }

    @PutMapping("/{configId}")
    @Operation(summary = "更新第三方平台配置", description = "根据配置ID更新第三方平台配置")
    public ResultData<ThirdPartyConfig> updateConfig(
            @Parameter(description = "配置ID") @PathVariable @NotNull Integer configId,
            @Valid @RequestBody ThirdPartyConfig config) {
        ThirdPartyConfig updatedConfig = thirdPartyConfigService.updateConfig(configId, config);
        return ResultData.success("第三方平台配置更新成功", updatedConfig);
    }

    @DeleteMapping("/{configId}")
    @Operation(summary = "删除第三方平台配置", description = "根据配置ID删除第三方平台配置")
    public ResultData<Boolean> deleteConfig(
            @Parameter(description = "配置ID") @PathVariable @NotNull Integer configId) {
        thirdPartyConfigService.deleteConfig(configId);
        return ResultData.success("第三方平台配置删除成功", true);
    }

    @GetMapping("/{configId}")
    @Operation(summary = "查询第三方平台配置", description = "根据配置ID查询第三方平台配置详情")
    public ResultData<ThirdPartyConfig> getConfigById(
            @Parameter(description = "配置ID") @PathVariable @NotNull Integer configId) {
        ThirdPartyConfig config = thirdPartyConfigService.getConfigById(configId);
        return ResultData.success("查询第三方平台配置成功", config);
    }

    @GetMapping("/platform/{platformType}")
    @Operation(summary = "根据平台类型查询配置", description = "根据平台类型查询启用的第三方平台配置")
    public ResultData<ThirdPartyConfig> getConfigByPlatformType(
            @Parameter(description = "平台类型") @PathVariable @NotNull Integer platformType) {
        ThirdPartyConfig config = thirdPartyConfigService.getConfigByPlatformType(platformType);
        return ResultData.success("查询第三方平台配置成功", config);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询第三方平台配置", description = "分页查询第三方平台配置列表")
    public ResultData<IPage<ThirdPartyConfig>> getConfigPage(@Valid ThirdPartyConfigQueryRequest request) {
        IPage<ThirdPartyConfig> page = thirdPartyConfigService.getConfigPage(request);
        return ResultData.success("查询成功", page);
    }

    @PutMapping("/{configId}/status")
    @Operation(summary = "更新配置状态", description = "启用或禁用第三方平台配置")
    public ResultData<Boolean> updateConfigStatus(
            @Parameter(description = "配置ID") @PathVariable @NotNull Integer configId,
            @Parameter(description = "配置状态：0-禁用，1-启用") @RequestParam @NotNull Integer status) {
        thirdPartyConfigService.updateConfigStatus(configId, status);
        String message = status == 1 ? "第三方平台配置启用成功" : "第三方平台配置禁用成功";
        return ResultData.success(message, true);
    }

    @GetMapping("/test")
    @Operation(summary = "健康检查", description = "第三方平台配置服务健康检查")
    public ResultData<String> test() {
        return ResultData.success("Third Party Config Service is running!");
    }
} 