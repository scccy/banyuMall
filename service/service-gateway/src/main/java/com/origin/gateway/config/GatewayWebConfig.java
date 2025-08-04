package com.origin.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;


/**
 * Gateway WebFlux配置类
 * 确保Gateway服务使用响应式Web环境而不是传统的Servlet环境
 * 
 * @author origin
 */
@Configuration
@EnableWebFlux
public class GatewayWebConfig implements WebFluxConfigurer {
    
    /**
     * 配置消息编解码器
     */
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(1024 * 1024); // 1MB
    }
    
    /**
     * 提供mvcConversionService bean以解决依赖问题
     * 这个bean通常由WebMvcAutoConfiguration提供，但由于我们排除了WebMvcAutoConfiguration，
     * 需要手动提供这个bean来满足某些组件的依赖需求
     */
    @Bean("mvcConversionService")
    public ConversionService mvcConversionService() {
        return new DefaultConversionService();
    }
    
    /**
     * 配置CORS跨域
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
} 