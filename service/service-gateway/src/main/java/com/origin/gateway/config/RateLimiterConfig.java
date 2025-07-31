package com.origin.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * 限流器配置类
 * 配置基于内存的限流器Key解析器
 * 
 * @author origin
 */
@Configuration
public class RateLimiterConfig {

    /**
     * 配置限流器的Key解析器
     * 默认使用请求的IP地址作为限流key
     * 注意：当前使用内存限流，如需Redis限流请添加Redis依赖
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            return Mono.just(ip);
        };
    }
}
