package com.origin.auth.config;

import com.origin.common.util.TokenBlacklistUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;

/**
 * JWT配置类
 *
 * @author origin
 * @since 2025-01-27
 */
@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 初始化TokenBlacklistUtil
     */
    @PostConstruct
    public void initTokenBlacklistUtil() {
        TokenBlacklistUtil.setRedisTemplate(stringRedisTemplate);
    }
} 