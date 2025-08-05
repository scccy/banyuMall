package com.origin.auth.config;

import com.origin.auth.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 认证模块的WebMvc配置
 * 专门管理认证相关的拦截器
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "jwt.enabled", havingValue = "true", matchIfMissing = true)
public class AuthWebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Value("${security.permit-all:}")
    private List<String> permitAllPaths;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT拦截器
        var registration = registry.addInterceptor(jwtInterceptor);
        
        // 拦截所有路径
        registration.addPathPatterns("/**");
        
        // 排除不需要认证的路径（从配置文件读取）
        if (permitAllPaths != null && !permitAllPaths.isEmpty()) {
            registration.excludePathPatterns(permitAllPaths.toArray(new String[0]));
        }
    }
} 