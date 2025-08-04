package com.origin.auth.config;

import com.origin.auth.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 认证模块的WebMvc配置
 * 专门管理认证相关的拦截器
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "auth.jwt.enabled", havingValue = "true", matchIfMissing = true)
public class AuthWebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT拦截器，拦截所有请求，但排除登录、登出、验证码等不需要认证的接口
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/auth/logout", 
                        "/auth/captcha",
                        "/auth/register",
                        "/service/auth/login",
                        "/service/auth/logout", 
                        "/service/auth/captcha",
                        "/service/auth/register",
                        "/service/auth/test",
                        "/service/auth/password/encrypt",
                        "/service/auth/password/verify",
                        "/service/auth/user/info",
                        "/service/auth/validate",
                        "/service/auth/logout/force/**",
                        "/test/**",
                        "/jwt-test/**",
                        "/password-test/**",
                        "/doc.html",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-resources/**"
                );
    }
} 