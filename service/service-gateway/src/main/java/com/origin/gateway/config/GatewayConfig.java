package com.origin.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关配置类
 * 
 * @author origin
 */
@Configuration
public class GatewayConfig {
    
    /**
     * 自定义路由配置
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 认证服务路由 - 更精确的路径匹配
                .route("auth-service", r -> r
                        .path("/auth/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Source", "service-gateway")
                                .addRequestHeader("X-Service-Name", "service-auth"))
                        .uri("lb://service-auth"))
                
                // 用户服务路由
                .route("user-service", r -> r
                        .path("/service/user/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway-Source", "service-gateway")
                                .addRequestHeader("X-Service-Name", "service-user"))
                        .uri("lb://service-user"))
                
                .build();
    }
} 