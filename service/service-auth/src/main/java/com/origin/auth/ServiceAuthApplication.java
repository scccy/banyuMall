package com.origin.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证授权服务启动类
 * 
 * @author origin
 * @since 2024-07-30
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.origin.auth.mapper")
public class ServiceAuthApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ServiceAuthApplication.class, args);
    }
}