package com.origin.aliyunOss;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 阿里云OSS文件存储服务启动类
 * 
 * @author scccy
 * @since 2025-07-31
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.origin.aliyunOss.mapper")
public class AliyunOssApplication {

    public static void main(String[] args) {
        SpringApplication.run(AliyunOssApplication.class, args);
    }
} 