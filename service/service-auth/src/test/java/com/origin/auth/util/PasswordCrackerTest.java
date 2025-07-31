package com.origin.auth.util;

import org.junit.jupiter.api.Test;

/**
 * PasswordCrackerUtil测试类
 * 可以直接运行main方法来测试密码破解功能
 * 
 * @author origin
 * @since 2025-07-31
 */
public class PasswordCrackerTest {

    /**
     * 主方法 - 直接运行密码破解测试
     * 您可以在IDE中直接运行这个方法来测试
     */
    public static void main(String[] args) {
        System.out.println("=== BCrypt密码破解工具测试 ===");
        System.out.println("开始测试...");
        
        // 测试特定的BCrypt密码
        String encodedPassword = "$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm";
        
        System.out.println("\n1. 解析密码信息:");
        PasswordUtil.BCryptInfo info = PasswordUtil.parseBCryptPassword(encodedPassword);
        if (info != null) {
            System.out.println("版本: " + info.getVersion());
            System.out.println("成本因子: " + info.getCost());
            System.out.println("盐值: " + info.getSalt());
            System.out.println("哈希: " + info.getHash());
        }
        
        System.out.println("\n2. 尝试破解密码:");
        PasswordCrackerUtil.CrackResult result = PasswordCrackerUtil.crackPassword(encodedPassword);
        if (result.isFound()) {
            System.out.println("✅ 密码破解成功!");
            System.out.println("明文密码: " + result.getPlainPassword());
            System.out.println("尝试次数: " + result.getAttempts());
            System.out.println("耗时: " + result.getDuration() + "ms");
        } else {
            System.out.println("❌ 密码破解失败");
            System.out.println("尝试次数: " + result.getAttempts());
            System.out.println("耗时: " + result.getDuration() + "ms");
        }
        
        System.out.println("\n3. 验证密码:");
        String testPassword = "123456";
        boolean matches = PasswordCrackerUtil.verifyPassword(testPassword, encodedPassword);
        System.out.println("测试密码 '" + testPassword + "' 验证结果: " + (matches ? "✅ 匹配" : "❌ 不匹配"));
        
        // 直接调用BCrypt验证，显示完整密码
        System.out.println("\n4. 直接BCrypt验证（显示完整密码）:");
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = 
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(10);
        boolean directMatches = encoder.matches(testPassword, encodedPassword);
        System.out.println("直接验证结果: " + (directMatches ? "✅ 匹配" : "❌ 不匹配"));
        System.out.println("明文密码: " + testPassword);
        System.out.println("加密密码: " + encodedPassword);
        
        System.out.println("\n5. 生成新密码哈希:");
        String newHash = PasswordCrackerUtil.generateHash(testPassword);
        System.out.println("新生成的哈希: " + newHash);
        
        System.out.println("\n6. 验证新生成的哈希:");
        boolean newMatches = PasswordCrackerUtil.verifyPassword(testPassword, newHash);
        System.out.println("新哈希验证结果: " + (newMatches ? "✅ 匹配" : "❌ 不匹配"));
        
        System.out.println("\n=== 测试完成 ===");
    }

    /**
     * JUnit测试方法
     */
    @Test
    public void testPasswordCracking() {
        System.out.println("=== JUnit测试 - BCrypt密码破解 ===");
        
        String encodedPassword = "$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm";
        
        // 测试密码破解
        PasswordCrackerUtil.CrackResult result = PasswordCrackerUtil.crackPassword(encodedPassword);
        
        if (result.isFound()) {
            System.out.println("✅ 密码破解成功: " + result.getPlainPassword());
        } else {
            System.out.println("❌ 密码破解失败，尝试了 " + result.getAttempts() + " 个密码");
        }
        
        // 测试密码验证
        boolean matches = PasswordCrackerUtil.verifyPassword("123456", encodedPassword);
        System.out.println("密码验证结果: " + (matches ? "✅ 匹配" : "❌ 不匹配"));
    }
} 