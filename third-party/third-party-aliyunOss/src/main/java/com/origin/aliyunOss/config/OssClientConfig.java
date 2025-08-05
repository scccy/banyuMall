package com.origin.aliyunOss.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS客户端配置类
 * 
 * @author scccy
 * @since 2025-07-31
 */
@Slf4j
@Configuration
public class OssClientConfig {

    @Autowired
    private OssConfig ossConfig;

    @Bean
    public OSS ossClient() {
        try {
            log.info("初始化OSS客户端，endpoint: {}", ossConfig.getEndpoint());
            
            OSS ossClient = new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
            );
            
            log.info("OSS客户端初始化成功");
            return ossClient;
        } catch (Exception e) {
            log.error("OSS客户端初始化失败: {}", e.getMessage(), e);
            throw new RuntimeException("OSS客户端初始化失败", e);
        }
    }
} 