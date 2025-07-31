package com.origin.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置类
 * 
 * @author origin
 * @since 2025-01-27
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    /**
     * JWT密钥
     */
    private String secret = "your-secret-key-here-should-be-at-least-32-bytes-long";
    
    /**
     * 过期时间（毫秒）
     */
    private Long expiration = 3600000L; // 1小时
    
    /**
     * 令牌前缀
     */
    private String tokenPrefix = "Bearer";
    
    /**
     * 刷新令牌过期时间（毫秒）
     */
    private Long refreshExpiration = 86400000L; // 24小时
    
    /**
     * 是否启用黑名单
     */
    private Boolean enableBlacklist = true;
    
    /**
     * Redis黑名单前缀
     */
    private String blacklistPrefix = "token:blacklist:";
    
    /**
     * Redis有效令牌前缀
     */
    private String validTokenPrefix = "token:valid:";
} 