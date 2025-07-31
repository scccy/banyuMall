package com.origin.auth.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;

/**
 * 密码破解工具类 - 仅用于开发和测试环境
 * 注意：BCrypt是单向哈希，无法直接反推明文密码
 * 此工具类仅用于测试和验证已知密码
 * 
 * @author origin
 * @since 2025-07-31
 */
@Slf4j
public class PasswordCrackerUtil {
    
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    
    // 常见密码列表
    private static final List<String> COMMON_PASSWORDS = Arrays.asList(
        "123456", "password", "123456789", "12345678", "12345", "qwerty", "abc123",
        "111111", "123123", "admin", "letmein", "welcome", "monkey", "1234567",
        "1234567890", "password123", "12345678910", "123456789012", "1234567890123",
        "admin123", "root", "test", "guest", "user", "demo", "1234", "123456789012345",
        "password1", "password123", "admin123", "root123", "test123", "guest123",
        "user123", "demo123", "1234567890123456", "12345678901234567", "123456789012345678",
        "1234567890123456789", "12345678901234567890", "password123456", "admin123456",
        "root123456", "test123456", "guest123456", "user123456", "demo123456"
    );
    
    /**
     * 验证密码是否匹配
     * 
     * @param rawPassword 明文密码
     * @param encodedPassword 加密密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        try {
            boolean matches = encoder.matches(rawPassword, encodedPassword);
            log.info("密码验证 - 明文: {}, 加密: {}, 匹配: {}", 
                    maskPassword(rawPassword), encodedPassword, matches);
            return matches;
        } catch (Exception e) {
            log.error("密码验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 尝试破解密码（测试常见密码）
     * 
     * @param encodedPassword 加密密码
     * @return 破解结果
     */
    public static CrackResult crackPassword(String encodedPassword) {
        log.info("开始尝试破解密码: {}", encodedPassword);
        
        CrackResult result = new CrackResult();
        result.setEncodedPassword(encodedPassword);
        result.setStartTime(System.currentTimeMillis());
        
        // 先解析密码信息
        PasswordUtil.BCryptInfo info = PasswordUtil.parseBCryptPassword(encodedPassword);
        if (info != null) {
            result.setVersion(info.getVersion());
            result.setCost(info.getCost());
            result.setSalt(info.getSalt());
        }
        
        // 尝试常见密码
        for (String commonPassword : COMMON_PASSWORDS) {
            if (encoder.matches(commonPassword, encodedPassword)) {
                result.setFound(true);
                result.setPlainPassword(commonPassword);
                result.setEndTime(System.currentTimeMillis());
                result.setDuration(result.getEndTime() - result.getStartTime());
                result.setAttempts(COMMON_PASSWORDS.indexOf(commonPassword) + 1);
                
                log.info("密码破解成功! 明文密码: {}, 尝试次数: {}, 耗时: {}ms", 
                        maskPassword(commonPassword), result.getAttempts(), result.getDuration());
                return result;
            }
        }
        
        // 尝试自定义密码列表
        List<String> customPasswords = getCustomPasswords();
        int totalAttempts = COMMON_PASSWORDS.size();
        
        for (String customPassword : customPasswords) {
            totalAttempts++;
            if (encoder.matches(customPassword, encodedPassword)) {
                result.setFound(true);
                result.setPlainPassword(customPassword);
                result.setEndTime(System.currentTimeMillis());
                result.setDuration(result.getEndTime() - result.getStartTime());
                result.setAttempts(totalAttempts);
                
                log.info("密码破解成功! 明文密码: {}, 尝试次数: {}, 耗时: {}ms", 
                        maskPassword(customPassword), result.getAttempts(), result.getDuration());
                return result;
            }
        }
        
        result.setFound(false);
        result.setEndTime(System.currentTimeMillis());
        result.setDuration(result.getEndTime() - result.getStartTime());
        result.setAttempts(totalAttempts);
        
        log.info("密码破解失败，尝试了 {} 个密码，耗时: {}ms", result.getAttempts(), result.getDuration());
        return result;
    }
    
    /**
     * 生成密码哈希
     * 
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public static String generateHash(String rawPassword) {
        try {
            String encoded = encoder.encode(rawPassword);
            log.info("生成密码哈希 - 明文: {}, 哈希: {}", maskPassword(rawPassword), encoded);
            return encoded;
        } catch (Exception e) {
            log.error("生成密码哈希失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 测试特定密码
     * 
     * @param encodedPassword 加密密码
     * @return 测试结果
     */
    public static TestResult testSpecificPassword(String encodedPassword) {
        log.info("测试特定密码: {}", encodedPassword);
        
        TestResult result = new TestResult();
        result.setEncodedPassword(encodedPassword);
        
        // 解析密码信息
        PasswordUtil.BCryptInfo info = PasswordUtil.parseBCryptPassword(encodedPassword);
        if (info != null) {
            result.setVersion(info.getVersion());
            result.setCost(info.getCost());
            result.setSalt(info.getSalt());
            result.setHash(info.getHash());
        }
        
        // 尝试破解
        CrackResult crackResult = crackPassword(encodedPassword);
        result.setCrackResult(crackResult);
        
        // 生成相同密码的新哈希
        if (crackResult.isFound()) {
            String newHash = generateHash(crackResult.getPlainPassword());
            result.setNewHash(newHash);
            result.setNewHashMatches(encoder.matches(crackResult.getPlainPassword(), newHash));
        }
        
        return result;
    }
    
    /**
     * 获取自定义密码列表
     * 可以在这里添加项目特定的常见密码
     */
    private static List<String> getCustomPasswords() {
        return Arrays.asList(
            // 项目特定的常见密码
            "admin", "admin123", "admin123456", "administrator", "root", "root123",
            "test", "test123", "demo", "demo123", "user", "user123", "guest", "guest123",
            "password", "password123", "123456", "123456789", "111111", "000000",
            // 中文拼音常见密码
            "zhang123", "li123", "wang123", "liu123", "chen123", "yang123", "huang123",
            "zhao123", "wu123", "zhou123", "xu123", "sun123", "ma123", "zhu123",
            // 其他常见组合
            "qwerty", "abc123", "letmein", "welcome", "monkey", "dragon", "master",
            "hello", "freedom", "whatever", "qazwsx", "trustno1", "jordan", "harley",
            "ranger", "iwantu", "jennifer", "hunter", "buster", "soccer", "harley",
            "guitar", "hammer", "silver", "222222", "888888", "11111111", "00000000"
        );
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
     * 破解结果类
     */
    @Data
    public static class CrackResult {
        private String encodedPassword;
        private String plainPassword;
        private boolean found;
        private long startTime;
        private long endTime;
        private long duration;
        private int attempts;
        private String version;
        private int cost;
        private String salt;

        
        @Override
        public String toString() {
            return String.format("CrackResult{found=%s, plainPassword='%s', attempts=%d, duration=%dms}", 
                    found, plainPassword != null ? maskPassword(plainPassword) : "null", attempts, duration);
        }
    }
    
    /**
     * 测试结果类
     */
    @Data
    public static class TestResult {
        private String encodedPassword;
        private String version;
        private int cost;
        private String salt;
        private String hash;
        private CrackResult crackResult;
        private String newHash;
        private boolean newHashMatches;
    }

    /**
     * 主方法 - 直接运行密码破解测试
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("=== BCrypt密码破解工具 ===");
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
        CrackResult result = crackPassword(encodedPassword);
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
        boolean matches = verifyPassword(testPassword, encodedPassword);
        System.out.println("测试密码 '" + testPassword + "' 验证结果: " + (matches ? "✅ 匹配" : "❌ 不匹配"));
        
        // 直接调用BCrypt验证，显示完整密码
        System.out.println("\n4. 直接BCrypt验证（显示完整密码）:");
        boolean directMatches = encoder.matches(testPassword, encodedPassword);
        System.out.println("直接验证结果: " + (directMatches ? "✅ 匹配" : "❌ 不匹配"));
        System.out.println("明文密码: " + testPassword);
        System.out.println("加密密码: " + encodedPassword);
        
        System.out.println("\n5. 生成新密码哈希:");
        String newHash = generateHash(testPassword);
        System.out.println("新生成的哈希: " + newHash);
        
        System.out.println("\n6. 验证新生成的哈希:");
        boolean newMatches = verifyPassword(testPassword, newHash);
        System.out.println("新哈希验证结果: " + (newMatches ? "✅ 匹配" : "❌ 不匹配"));
        
        System.out.println("\n=== 测试完成 ===");
    }
} 