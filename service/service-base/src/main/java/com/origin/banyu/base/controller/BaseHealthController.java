package com.origin.banyu.base.controller;


import com.origin.banyu.common.dto.HealthResponse;
import com.origin.banyu.common.dto.ResultData;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

/**
 * 基础健康检查控制器
 * 提供统一的健康检查实现，各微服务可以继承此类
 * 
 * @author origin
 * @since 2025-08-07
 */
@Slf4j
public abstract class BaseHealthController {

    @Value("${spring.application.name:unknown}")
    private String serviceName;

    @Value("${spring.application.version:0.0.1-SNAPSHOT}")
    private String serviceVersion;

    /**
     * 获取服务特定的健康检查路径
     * 子类必须实现此方法，返回对应的健康检查路径
     */
    protected abstract String getHealthPath();

    /**
     * 获取服务描述
     * 子类可以重写此方法，提供特定的服务描述
     */
    protected String getServiceDescription() {
        return getServiceDescription(serviceName);
    }

    @Operation(summary = "健康检查", description = "检查服务运行状态")
    @GetMapping
    public ResultData<HealthResponse> health() {
        log.debug("{} 健康检查请求", serviceName);
        
        HealthResponse healthResponse = HealthResponse.builder()
                .status("UP")
                .service(serviceName)
                .version(serviceVersion)
                .timestamp(LocalDateTime.now())
                .description(getServiceDescription())
                .build();

        return ResultData.success(getServiceDescription(), healthResponse);
    }

    /**
     * 获取服务描述
     */
    private String getServiceDescription(String serviceName) {
        switch (serviceName) {
            case "service-auth":
                return "认证服务运行正常";
            case "service-user":
                return "用户服务运行正常";
            case "service-gateway":
                return "网关服务运行正常";
            case "third-party-wechatWork":
                return "企业微信服务运行正常";
            case "third-party-aliyunOss":
                return "阿里云OSS服务运行正常";
            case "core-publisher":
                return "发布者核心服务运行正常";
            default:
                return "服务运行正常";
        }
    }
} 