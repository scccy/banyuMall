package com.origin.auth.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.auth.dto.ThirdPartyConfigDTO;
import com.origin.auth.dto.ThirdPartyConfigQueryRequest;
import com.origin.common.entity.ThirdPartyConfig;
import com.origin.auth.service.ThirdPartyConfigService;
import com.origin.common.dto.ResultData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 第三方平台配置控制器
 * 
 * @author scccy
 * @since 2025-08-05
 */
@Slf4j
@RestController
@RequestMapping("/service/auth/third-party/config")
@RequiredArgsConstructor
@Validated
@Tag(name = "第三方平台配置管理", description = "提供第三方平台配置的增删改查功能")
public class ThirdPartyConfigController {

    private final ThirdPartyConfigService thirdPartyConfigService;

    @PostMapping
    @Operation(summary = "创建第三方平台配置", description = "创建新的第三方平台配置")
    public ResultData<ThirdPartyConfig> createConfig(@Valid @RequestBody ThirdPartyConfigDTO configDTO) {
        try {
            ThirdPartyConfig config = thirdPartyConfigService.createConfig(configDTO);
            return ResultData.ok("第三方平台配置创建成功", config);
        } catch (Exception e) {
            log.error("创建第三方平台配置失败", e);
            return ResultData.fail("第三方平台配置创建失败: " + e.getMessage());
        }
    }

    @PutMapping("/{configId}")
    @Operation(summary = "更新第三方平台配置", description = "根据配置ID更新第三方平台配置")
    public ResultData<ThirdPartyConfig> updateConfig(
            @Parameter(description = "配置ID") @PathVariable @NotBlank String configId,
            @Valid @RequestBody ThirdPartyConfigDTO configDTO) {
        try {
            ThirdPartyConfig config = thirdPartyConfigService.updateConfig(configId, configDTO);
            return ResultData.ok("第三方平台配置更新成功", config);
        } catch (Exception e) {
            log.error("更新第三方平台配置失败，配置ID：{}", configId, e);
            return ResultData.fail("第三方平台配置更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{configId}")
    @Operation(summary = "删除第三方平台配置", description = "根据配置ID删除第三方平台配置")
    public ResultData<Boolean> deleteConfig(
            @Parameter(description = "配置ID") @PathVariable @NotBlank String configId) {
        try {
            boolean result = thirdPartyConfigService.deleteConfig(configId);
            if (result) {
                return ResultData.ok("第三方平台配置删除成功", true);
            } else {
                return ResultData.fail("第三方平台配置删除失败");
            }
        } catch (Exception e) {
            log.error("删除第三方平台配置失败，配置ID：{}", configId, e);
            return ResultData.fail("第三方平台配置删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/{configId}")
    @Operation(summary = "查询第三方平台配置", description = "根据配置ID查询第三方平台配置详情")
    public ResultData<ThirdPartyConfig> getConfigById(
            @Parameter(description = "配置ID") @PathVariable @NotBlank String configId) {
        try {
            ThirdPartyConfig config = thirdPartyConfigService.getConfigById(configId);
            if (config != null) {
                return ResultData.ok("查询第三方平台配置成功", config);
            } else {
                return ResultData.fail("第三方平台配置不存在");
            }
        } catch (Exception e) {
            log.error("查询第三方平台配置失败，配置ID：{}", configId, e);
            return ResultData.fail("查询第三方平台配置失败: " + e.getMessage());
        }
    }

    @GetMapping("/platform/{platformType}")
    @Operation(summary = "根据平台类型查询配置", description = "根据平台类型查询启用的第三方平台配置")
    public ResultData<ThirdPartyConfig> getConfigByPlatformType(
            @Parameter(description = "平台类型") @PathVariable @NotBlank String platformType) {
        try {
            ThirdPartyConfig config = thirdPartyConfigService.getConfigByPlatformType(platformType);
            if (config != null) {
                return ResultData.ok("查询第三方平台配置成功", config);
            } else {
                return ResultData.fail("第三方平台配置不存在或已禁用");
            }
        } catch (Exception e) {
            log.error("根据平台类型查询配置失败，平台类型：{}", platformType, e);
            return ResultData.fail("查询第三方平台配置失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询第三方平台配置", description = "分页查询第三方平台配置列表")
    public ResultData<IPage<ThirdPartyConfig>> getConfigPage(@Valid ThirdPartyConfigQueryRequest request) {
        try {
            IPage<ThirdPartyConfig> page = thirdPartyConfigService.getConfigPage(request);
            return ResultData.ok("查询成功", page);
        } catch (Exception e) {
            log.error("分页查询第三方平台配置失败", e);
            return ResultData.fail("查询失败: " + e.getMessage());
        }
    }

    @PutMapping("/{configId}/status")
    @Operation(summary = "更新配置状态", description = "启用或禁用第三方平台配置")
    public ResultData<Boolean> updateConfigStatus(
            @Parameter(description = "配置ID") @PathVariable @NotBlank String configId,
            @Parameter(description = "配置状态：0-禁用，1-启用") @RequestParam @NotNull Integer status) {
        try {
            boolean result = thirdPartyConfigService.updateConfigStatus(configId, status);
            if (result) {
                String message = status == 1 ? "第三方平台配置启用成功" : "第三方平台配置禁用成功";
                return ResultData.ok(message, true);
            } else {
                return ResultData.fail("第三方平台配置状态更新失败");
            }
        } catch (Exception e) {
            log.error("更新第三方平台配置状态失败，配置ID：{}，状态：{}", configId, status, e);
            return ResultData.fail("第三方平台配置状态更新失败: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    @Operation(summary = "健康检查", description = "第三方平台配置服务健康检查")
    public ResultData<String> test() {
        return ResultData.ok("Third Party Config Service is running!");
    }
} 