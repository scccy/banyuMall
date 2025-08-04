package com.origin.auth.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 测试配置类
 * 在测试环境中禁用JWT拦截器，避免认证问题
 */
@TestConfiguration
@Profile("test")
public class TestConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 在测试环境中排除所有需要认证的路径
        // 这样可以专注于测试业务逻辑，而不需要处理认证
    }
} 