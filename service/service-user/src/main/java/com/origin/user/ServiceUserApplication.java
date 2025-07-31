package com.origin.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 用户服务启动类
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceUserApplication {

    public static void main(String[] args) {
        log.info("ServiceUserApplication 开始启动");
        SpringApplication.run(ServiceUserApplication.class, args);
        log.info("ServiceUserApplication 启动完成");
    }
} 