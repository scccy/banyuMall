package com.origin.base.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.common.comm.SignVersion;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 阿里云OSS配置类
 * 
 * @author origin
 * @since 2024-12-19
 */
@Configuration
@ConditionalOnProperty(prefix = "aliyun.oss", name = "enabled", havingValue = "true", matchIfMissing = false)
public class OssConfig {

    @Component
    @ConfigurationProperties(prefix = "aliyun.oss")
    @Data
    public static class OssProperties {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
        private String region;
        private String securityToken;
        private Integer maxConnections = 200;
        private Integer connectionTimeout = 30000;
        private Integer socketTimeout = 60000;
        private Integer maxRetries = 3;
        private Integer retryDelay = 1000;
        private Boolean enabled = false;
    }

    /**
     * 配置OSS客户端
     */
    @Bean
    public OSS ossClient(OssProperties ossProperties) {
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setMaxConnections(ossProperties.getMaxConnections());
        conf.setConnectionTimeout(ossProperties.getConnectionTimeout());
        conf.setSocketTimeout(ossProperties.getSocketTimeout());
        conf.setMaxErrorRetry(ossProperties.getMaxRetries());
        conf.setSignatureVersion(SignVersion.V4);

        return new OSSClientBuilder().build(
            ossProperties.getEndpoint(),
            ossProperties.getAccessKeyId(),
            ossProperties.getAccessKeySecret(),
            conf
        );
    }
}