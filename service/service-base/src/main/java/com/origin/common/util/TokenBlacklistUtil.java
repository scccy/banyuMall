package com.origin.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Token 黑名单管理工具类
 * 
 * @author origin
 * @since 2024-07-30
 */
@Slf4j
public class TokenBlacklistUtil {

    private static StringRedisTemplate redisTemplate;
    
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    private static final String TOKEN_PREFIX = "token:valid:";

    /**
     * 设置 Redis 模板
     */
    public static void setRedisTemplate(StringRedisTemplate template) {
        TokenBlacklistUtil.redisTemplate = template;
    }

    /**
     * 将 token 加入黑名单
     *
     * @param token JWT token
     * @param expirationTime 过期时间（秒）
     */
    public static void addToBlacklist(String token, long expirationTime) {
        if (redisTemplate == null) {
            log.warn("RedisTemplate 未初始化");
            return;
        }
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "1", expirationTime, TimeUnit.SECONDS);
        log.info("Token 已加入黑名单: {}", token);
    }

    /**
     * 检查 token 是否在黑名单中
     *
     * @param token JWT token
     * @return true 如果在黑名单中，false 否则
     */
    public static boolean isBlacklisted(String token) {
        if (redisTemplate == null) {
            log.warn("RedisTemplate 未初始化");
            return false;
        }
        String key = BLACKLIST_PREFIX + token;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 将 token 标记为有效（用于登录时）
     *
     * @param token JWT token
     * @param expirationTime 过期时间（秒）
     */
    public static void markAsValid(String token, long expirationTime) {
        if (redisTemplate == null) {
            log.warn("RedisTemplate 未初始化");
            return;
        }
        String key = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, "1", expirationTime, TimeUnit.SECONDS);
        log.debug("Token 已标记为有效: {}", token);
    }

    /**
     * 检查 token 是否有效
     *
     * @param token JWT token
     * @return true 如果有效，false 否则
     */
    public static boolean isValid(String token) {
        if (redisTemplate == null) {
            log.warn("RedisTemplate 未初始化");
            return false;
        }
        String key = TOKEN_PREFIX + token;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 从有效 token 列表中移除（用于登出时）
     *
     * @param token JWT token
     */
    public static void removeFromValid(String token) {
        if (redisTemplate == null) {
            log.warn("RedisTemplate 未初始化");
            return;
        }
        String key = TOKEN_PREFIX + token;
        redisTemplate.delete(key);
        log.info("Token 已从有效列表中移除: {}", token);
    }
} 