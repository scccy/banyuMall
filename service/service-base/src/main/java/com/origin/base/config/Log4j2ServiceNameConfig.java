package com.origin.base.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import jakarta.annotation.PostConstruct;

/**
 * Log4j2服务名称配置类
 * 在应用启动完成后设置服务名称，确保日志文件使用正确的服务名称
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Slf4j
@Configuration
public class Log4j2ServiceNameConfig {

    private final Environment environment;

    public Log4j2ServiceNameConfig(Environment environment) {
        this.environment = environment;
        log.info("Log4j2ServiceNameConfig 已创建");
    }

    @PostConstruct
    public void init() {
        log.info("Log4j2ServiceNameConfig PostConstruct 已执行");
    }

    /**
     * 在应用启动完成后设置服务名称
     * 确保在Spring Boot完全启动后设置系统属性
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            // 获取应用名称
            String appName = environment.getProperty("spring.application.name", "unknown-service");
            
            // 设置系统属性
            System.setProperty("service.name", appName);
            
            log.info("Log4j2服务名称已设置: {}", appName);
            
            // 重新配置Log4j2
            org.apache.logging.log4j.core.config.Configurator.reconfigure();
            
            log.info("Log4j2配置已重新加载，日志文件将使用服务名称: {}", appName);
            
        } catch (Exception e) {
            log.warn("设置Log4j2服务名称失败: {}", e.getMessage());
        }
    }
} 