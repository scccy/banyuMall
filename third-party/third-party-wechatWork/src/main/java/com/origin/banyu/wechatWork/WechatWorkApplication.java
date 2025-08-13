package com.origin.banyu.wechatWork;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 企业微信第三方服务启动类
 * 符合Spring Boot启动性能优化规则
 * 
 * @author scccy
 */
@SpringBootApplication(scanBasePackages = {"com.origin.banyu.*"})
@EnableFeignClients(basePackages = "com.origin.banyu")
@EnableScheduling
@MapperScan("com.origin.banyu.wechatWork.mapper")
public class WechatWorkApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(WechatWorkApplication.class, args);
    }
} 