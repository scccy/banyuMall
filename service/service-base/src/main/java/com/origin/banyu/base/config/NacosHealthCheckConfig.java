package com.origin.banyu.base.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * Nacos健康检查配置
 * 配置Nacos主动健康检查参数
 * 
 * @author origin
 * @since 2025-08-07
 */
@Configuration
@ConditionalOnClass(name = "com.alibaba.cloud.nacos.NacosDiscoveryAutoConfiguration")
@Slf4j
public class NacosHealthCheckConfig {

    /**
     * Nacos健康检查配置说明：
     * 
     * 1. 心跳机制 (Client → Nacos):
     *    - 微服务每5秒向Nacos发送心跳
     *    - Nacos接收心跳，更新服务状态
     * 
     * 2. 主动健康检查 (Nacos → 微服务):
     *    - Nacos主动调用微服务的健康检查接口
     *    - 需要配置 health-check-enabled: true
     *    - 需要配置 health-check-url: /health
     *    - 需要配置 health-check-timeout: 3000
     *    - 需要配置 health-check-interval: 5000
     * 
     * 3. 健康检查流程:
     *    - Nacos定期调用微服务的 /health 接口
     *    - 检查响应状态码和响应内容
     *    - 根据检查结果更新服务状态
     */
} 