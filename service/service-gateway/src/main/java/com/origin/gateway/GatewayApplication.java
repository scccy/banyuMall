package com.origin.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关服务启动类
 * 
 * @author origin
 */
@SpringBootApplication(
    scanBasePackages = {"com.origin.gateway", "com.origin.common.util", "com.origin.config.Log4j2Config"},
    exclude = {
        WebMvcAutoConfiguration.class
    }
)
@EnableDiscoveryClient
public class GatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
} 