package com.origin.user.controller;

import com.origin.common.ResultData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器 - 用于验证Knife4j配置
 * 
 * @author origin
 * @since 2025-07-30
 */
@Tag(name = "测试接口", description = "用于验证API文档配置的测试接口")
@RestController
@RequestMapping("/test")
public class TestController {

    @Operation(summary = "健康检查", description = "用于验证服务是否正常运行的接口")
    @GetMapping("/health")
    public ResultData<String> health() {
        return ResultData.success("User Service is running!");
    }

    @Operation(summary = "Knife4j测试", description = "用于验证Knife4j配置是否正常工作的接口")
    @GetMapping("/knife4j")
    public ResultData<String> knife4jTest() {
        return ResultData.success("Knife4j configuration is working!");
    }
} 