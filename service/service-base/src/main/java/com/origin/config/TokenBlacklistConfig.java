package com.origin.config;

import com.origin.common.util.TokenBlacklistUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import jakarta.annotation.PostConstruct;

/**
 * Token 黑名单配置
 * 
 * @author origin
 * @since 2024-07-30
 */
@Configuration
public class TokenBlacklistConfig {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        TokenBlacklistUtil.setRedisTemplate(redisTemplate);
    }
} 