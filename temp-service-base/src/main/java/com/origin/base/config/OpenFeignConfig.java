package com.origin.base.config;

import feign.Logger;
import feign.Request;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * OpenFeign配置类
 * 
 * @author origin
 * @since 2024-12-19
 */
@Configuration
public class OpenFeignConfig {

    /**
     * 配置日志级别
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 配置请求超时时间
     */
    @Bean
    public Request.Options options() {
        // Spring Cloud 2023.0.0版本中，Request.Options构造函数已更改
        // 新的构造函数不再接受TimeUnit参数，而是直接使用毫秒值
        return new Request.Options(
            10000,  // 连接超时（毫秒）
            60000,  // 读取超时（毫秒）
            false   // 是否跟随重定向
        );
    }

    /**
     * 配置编码器
     */
    @Bean
    public Encoder feignEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    /**
     * 配置解码器
     */
    @Bean
    public Decoder feignDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SpringDecoder(messageConverters);
    }
}