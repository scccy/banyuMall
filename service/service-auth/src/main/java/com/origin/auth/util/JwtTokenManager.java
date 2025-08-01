package com.origin.auth.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * JWT令牌管理器
 * 负责JWT令牌的存储、验证、黑名单管理等完整生命周期
 * 
 * @author origin
 * @since 2025-01-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenManager {

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.enable-blacklist}")
    private Boolean enableBlacklist;

    @Value("${jwt.blacklist-prefix}")
    private String blacklistPrefix;

    @Value("${jwt.valid-token-prefix}")
    private String validTokenPrefix;

    /**
     * 将 token 加入黑名单
     *
     * @param token JWT token
     * @param expirationTime 过期时间（秒）
     */
    public void addToBlacklist(String token, long expirationTime) {
        if (!enableBlacklist) {
            log.debug("Token黑名单功能已禁用");
            return;
        }
        
        String key = blacklistPrefix + token;
        redisTemplate.opsForValue().set(key, "1", expirationTime, TimeUnit.SECONDS);
        log.info("Token 已加入黑名单: {}", token);
    }

    /**
     * 检查 token 是否在黑名单中
     *
     * @param token JWT token
     * @return true 如果在黑名单中，false 否则
     */
    public boolean isBlacklisted(String token) {
        if (!enableBlacklist) {
            return false;
        }
        
        String key = blacklistPrefix + token;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 将 token 标记为有效（用于登录时）
     *
     * @param token JWT token
     * @param expirationTime 过期时间（秒）
     */
    public void markAsValid(String token, long expirationTime) {
        if (!enableBlacklist) {
            log.debug("Token黑名单功能已禁用");
            return;
        }
        
        String key = validTokenPrefix + token;
        redisTemplate.opsForValue().set(key, "1", expirationTime, TimeUnit.SECONDS);
        log.debug("Token 已标记为有效: {}", token);
    }

    /**
     * 更新用户token（推荐方案 - 基于用户ID管理）
     *
     * @param userId 用户ID
     * @param token JWT token
     * @param expirationTime 过期时间（秒）
     */
    public void updateUserToken(String userId, String token, long expirationTime) {
        if (!enableBlacklist) {
            log.debug("Token黑名单功能已禁用");
            return;
        }
        
        String key = validTokenPrefix + "user:" + userId;
        // 直接覆盖旧token，避免重复存储
        redisTemplate.opsForValue().set(key, token, expirationTime, TimeUnit.SECONDS);
        log.debug("用户 {} 的token已更新", userId);
    }

    /**
     * 检查用户token是否有效
     *
     * @param userId 用户ID
     * @param token JWT token
     * @return true 如果有效，false 否则
     */
    public boolean isUserTokenValid(String userId, String token) {
        if (!enableBlacklist) {
            return true;
        }
        
        String key = validTokenPrefix + "user:" + userId;
        String storedToken = redisTemplate.opsForValue().get(key);
        return token.equals(storedToken);
    }

    /**
     * 移除用户token（用于登出时）
     *
     * @param userId 用户ID
     */
    public void removeUserToken(String userId) {
        if (!enableBlacklist) {
            log.debug("Token黑名单功能已禁用");
            return;
        }
        
        String key = validTokenPrefix + "user:" + userId;
        redisTemplate.delete(key);
        log.debug("用户 {} 的token已移除", userId);
    }

    /**
     * 检查 token 是否有效
     *
     * @param token JWT token
     * @return true 如果有效，false 否则
     */
    public boolean isValid(String token) {
        if (!enableBlacklist) {
            return true; // 如果禁用黑名单，则认为所有token都有效
        }
        
        String key = validTokenPrefix + token;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 从有效 token 列表中移除（用于登出时）
     *
     * @param token JWT token
     */
    public void removeFromValid(String token) {
        if (!enableBlacklist) {
            log.debug("Token黑名单功能已禁用");
            return;
        }
        
        String key = validTokenPrefix + token;
        redisTemplate.delete(key);
        log.debug("Token 已从有效列表中移除: {}", token);
    }

    /**
     * 清理过期的黑名单记录
     */
    public void cleanExpiredBlacklist() {
        if (!enableBlacklist) {
            log.debug("Token黑名单功能已禁用");
            return;
        }
        
        // Redis会自动清理过期的key，这里可以添加额外的清理逻辑
        log.debug("黑名单清理完成");
    }

    /**
     * 获取黑名单中的token数量
     *
     * @return 黑名单中的token数量
     */
    public long getBlacklistCount() {
        if (!enableBlacklist) {
            return 0;
        }
        
        // 这里可以实现获取黑名单数量的逻辑
        // 由于Redis的key是动态的，需要扫描所有匹配的key
        return 0; // 暂时返回0，可以根据需要实现具体逻辑
    }
} 