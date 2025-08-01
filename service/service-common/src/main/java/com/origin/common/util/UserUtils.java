package com.origin.common.util;

import java.util.regex.Pattern;

/**
 * 用户相关工具类
 * 提供用户特定的工具方法，通用验证方法请使用ValidationUtils
 * 
 * @author scccy
 * @since 2025-08-01
 */
public class UserUtils {
    
    /**
     * 用户名格式正则表达式（字母数字下划线，3-20位）
     */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3-20}$");
    
    /**
     * 验证用户名格式
     * 
     * @param username 用户名
     * @return true 如果用户名格式正确，false 否则
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }
    
    /**
     * 验证密码强度
     * 密码必须包含字母和数字，长度8-20位
     * 
     * @param password 密码
     * @return true 如果密码强度符合要求，false 否则
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 20) {
            return false;
        }
        // 必须包含字母和数字
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        return hasLetter && hasDigit;
    }
    
    /**
     * 生成用户昵称
     * 
     * @param username 用户名
     * @return 生成的昵称
     */
    public static String generateNickname(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "用户" + System.currentTimeMillis();
        }
        return username + "_" + System.currentTimeMillis();
    }
    
    /**
     * 生成用户ID
     * 
     * @return 生成的用户ID
     */
    public static String generateUserId() {
        return "U" + System.currentTimeMillis() + String.format("%03d", (int)(Math.random() * 1000));
    }
    
    /**
     * 脱敏处理用户名
     * 
     * @param username 用户名
     * @return 脱敏后的用户名
     */
    public static String maskUsername(String username) {
        if (username == null || username.length() <= 2) {
            return username;
        }
        return username.substring(0, 1) + "*".repeat(username.length() - 2) + username.substring(username.length() - 1);
    }
    
    /**
     * 脱敏处理手机号
     * 
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
    
    /**
     * 脱敏处理邮箱
     * 
     * @param email 邮箱
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 2) {
            return username + "@" + domain;
        }
        
        String maskedUsername = username.substring(0, 1) + "*".repeat(username.length() - 2) + username.substring(username.length() - 1);
        return maskedUsername + "@" + domain;
    }
} 