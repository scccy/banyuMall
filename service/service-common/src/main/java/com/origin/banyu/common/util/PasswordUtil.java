package com.origin.banyu.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 密码工具类
 * 提供统一的密码加密和验证功能
 * 使用BCrypt算法，确保安全性
 * 
 * @author scccy
 * @since 2025-08-14
 */
@Slf4j
public class PasswordUtil {
    
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    
    /**
     * 私有构造函数，防止实例化
     */
    private PasswordUtil() {
        throw new UnsupportedOperationException("工具类不能实例化");
    }
    
    /**
     * 加密密码
     * 
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public static String encrypt(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        
        try {
            String encryptedPassword = passwordEncoder.encode(rawPassword);
            log.debug("密码加密成功");
            return encryptedPassword;
        } catch (Exception e) {
            log.error("密码加密失败: {}", e.getMessage());
            throw new RuntimeException("密码加密失败", e);
        }
    }
    
    /**
     * 验证密码
     * 
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @return 验证结果
     */
    public static boolean verify(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        
        try {
            boolean isValid = passwordEncoder.matches(rawPassword, encodedPassword);
            log.debug("密码验证完成，结果: {}", isValid);
            return isValid;
        } catch (Exception e) {
            log.error("密码验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查密码是否需要重新加密
     * 用于检测旧版本的加密方式
     * 
     * @param encodedPassword 加密后的密码
     * @return 是否需要重新加密
     */
    public static boolean needsRehash(String encodedPassword) {
        if (encodedPassword == null) {
            return false;
        }
        
        try {
            return passwordEncoder.upgradeEncoding(encodedPassword);
        } catch (Exception e) {
            log.warn("检查密码是否需要重新加密时出错: {}", e.getMessage());
            return false;
        }
    }
}
