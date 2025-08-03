package com.origin.publisher;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 核心发布者服务启动类
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {"com.origin.publisher", "com.origin.base"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.origin.publisher.feign"})
@MapperScan("com.origin.publisher.mapper")
public class CorePublisherApplication {
    
    public static void main(String[] args) {
            SpringApplication.run(CorePublisherApplication.class, args);

    }
} 