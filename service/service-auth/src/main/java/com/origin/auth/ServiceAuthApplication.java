package com.origin.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 认证授权服务启动类
 * 
 * @author origin
 * @since 2024-07-30
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.origin.auth.feign"})
public class ServiceAuthApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ServiceAuthApplication.class, args);
    }
}