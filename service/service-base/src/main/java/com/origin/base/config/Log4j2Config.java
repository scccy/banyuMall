package com.origin.base.config;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;



/**
 * Log4j2配置类
 * 用于解决包扫描警告问题
 */
@Configuration
@ConditionalOnClass(LogManager.class)
public class Log4j2Config {

    /**
     * 初始化Log4j2配置
     * 禁用包扫描警告
     */
    @PostConstruct
    public void initLog4j2() {
        // 设置系统属性，禁用包扫描警告
        System.setProperty("log4j2.disable.jmx", "true");
        System.setProperty("log4j2.skipJansi", "true");
        
        // 禁用插件扫描警告
        System.setProperty("log4j2.statusLogger.level", "ERROR");
        
        // 设置配置工厂，避免自动扫描
        if (ConfigurationFactory.getInstance() == null) {
            System.setProperty("log4j2.configurationFactory", "org.apache.logging.log4j.core.config.ConfigurationFactory");
        }
    }
} 