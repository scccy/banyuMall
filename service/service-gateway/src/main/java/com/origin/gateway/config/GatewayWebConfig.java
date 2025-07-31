package com.origin.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Gateway WebFlux配置类
 * 确保Gateway服务使用正确的WebFlux环境
 * 
 * @author origin
 */
@Configuration
@EnableWebFlux
public class GatewayWebConfig implements WebFluxConfigurer {
    
    /**
     * 配置WebFlux环境
     * 确保Gateway服务使用响应式Web环境而不是传统的Servlet环境
     */
    @Bean
    public WebFluxConfigurer webFluxConfigurer() {
        return new WebFluxConfigurer() {
            // 可以在这里添加自定义的WebFlux配置
        };
    }
} 