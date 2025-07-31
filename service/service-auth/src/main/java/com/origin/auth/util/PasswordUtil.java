package com.origin.auth.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码工具类 - 用于密码加密、验证和解析
 * 
 * @author origin
 * @since 2025-07-31
 */
@Slf4j
@Component
public class PasswordUtil {
    
    // 使用强度10，与SecurityConfig中的配置保持一致，也与数据库中的现有密码兼容
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    
    /**
     * 验证密码是否匹配
     * 
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        try {
            boolean matches = encoder.matches(rawPassword, encodedPassword);
            log.info("密码验证 - 原始密码: {}, 加密密码: {}, 匹配结果: {}", 
                    maskPassword(rawPassword), encodedPassword, matches);
            return matches;
        } catch (Exception e) {
            log.error("密码验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 加密密码
     * 
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encode(String rawPassword) {
        try {
            String encoded = encoder.encode(rawPassword);
            log.info("密码加密 - 原始密码: {}, 加密结果: {}", maskPassword(rawPassword), encoded);
            return encoded;
        } catch (Exception e) {
            log.error("密码加密失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 解析BCrypt密码信息
     * 
     * @param encodedPassword 加密后的密码
     * @return 密码信息
     */
    public static BCryptInfo parseBCryptPassword(String encodedPassword) {
        try {
            if (encodedPassword == null || !encodedPassword.startsWith("$2a$")) {
                log.warn("不是有效的BCrypt密码格式: {}", encodedPassword);
                return null;
            }
            
            // 解析BCrypt格式: $2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm
            String[] parts = encodedPassword.split("\\$");
            if (parts.length != 4) {
                log.warn("BCrypt密码格式不正确: {}", encodedPassword);
                return null;
            }
            
            String version = parts[1]; // 2a
            String cost = parts[2]; // 10
            String saltAndHash = parts[3]; // ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm
            
            // 盐值是前22个字符
            String salt = saltAndHash.substring(0, 22);
            // 哈希值是剩余部分
            String hash = saltAndHash.substring(22);
            
            BCryptInfo info = new BCryptInfo();
            info.setVersion(version);
            info.setCost(Integer.parseInt(cost));
            info.setSalt(salt);
            info.setHash(hash);
            info.setFullHash(encodedPassword);
            
            log.info("BCrypt密码解析成功 - 版本: {}, 成本因子: {}, 盐值: {}, 哈希: {}", 
                    version, cost, salt, hash);
            
            return info;
        } catch (Exception e) {
            log.error("BCrypt密码解析失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 测试特定密码
     */
    public static void testSpecificPassword() {
        String encodedPassword = "$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm";
        String testPassword = "123456";
        
        log.info("=== BCrypt密码测试 ===");
        log.info("加密密码: {}", encodedPassword);
        log.info("测试密码: {}", testPassword);
        
        // 解析密码信息
        BCryptInfo info = parseBCryptPassword(encodedPassword);
        if (info != null) {
            log.info("密码信息: {}", info);
        }
        
        // 验证密码
        boolean matches = matches(testPassword, encodedPassword);
        log.info("密码验证结果: {}", matches);
        
        // 生成相同密码的加密结果
        String newEncoded = encode(testPassword);
        log.info("新生成的加密密码: {}", newEncoded);
        
        // 验证新生成的密码
        boolean newMatches = matches(testPassword, newEncoded);
        log.info("新密码验证结果: {}", newMatches);
    }
    
    /**
     * 掩码密码（用于日志安全）
     */
    private static String maskPassword(String password) {
        if (password == null || password.length() <= 2) {
            return "***";
        }
        return password.substring(0, 1) + "***" + password.substring(password.length() - 1);
    }
    
    /**
     * BCrypt密码信息类
     */
    public static class BCryptInfo {
        private String version;
        private int cost;
        private String salt;
        private String hash;
        private String fullHash;
        
        // Getters and Setters
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public int getCost() { return cost; }
        public void setCost(int cost) { this.cost = cost; }
        
        public String getSalt() { return salt; }
        public void setSalt(String salt) { this.salt = salt; }
        
        public String getHash() { return hash; }
        public void setHash(String hash) { this.hash = hash; }
        
        public String getFullHash() { return fullHash; }
        public void setFullHash(String fullHash) { this.fullHash = fullHash; }
        
        @Override
        public String toString() {
            return String.format("BCryptInfo{version='%s', cost=%d, salt='%s', hash='%s'}", 
                    version, cost, salt, hash);
        }
    }
} 