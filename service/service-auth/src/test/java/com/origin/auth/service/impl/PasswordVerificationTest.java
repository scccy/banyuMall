package com.origin.auth.service.impl;

import com.origin.auth.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 密码验证测试类
 * 
 * @author origin
 * @since 2025-07-31
 */
public class PasswordVerificationTest {

    @Test
    public void testSpecificPasswordVerification() {
        // 测试特定的BCrypt密码
        String encodedPassword = "$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm";
        String testPassword = "123456";
        
        System.out.println("=== 测试特定密码验证 ===");
        System.out.println("加密密码: " + encodedPassword);
        System.out.println("测试密码: " + testPassword);
        
        // 使用PasswordUtil验证
        boolean passwordUtilMatches = PasswordUtil.matches(testPassword, encodedPassword);
        System.out.println("PasswordUtil验证结果: " + passwordUtilMatches);
        
        // 使用不同强度的BCryptPasswordEncoder验证
        BCryptPasswordEncoder encoder10 = new BCryptPasswordEncoder(10);
        boolean encoder10Matches = encoder10.matches(testPassword, encodedPassword);
        System.out.println("BCryptPasswordEncoder(10)验证结果: " + encoder10Matches);
        
        BCryptPasswordEncoder encoder12 = new BCryptPasswordEncoder(12);
        boolean encoder12Matches = encoder12.matches(testPassword, encodedPassword);
        System.out.println("BCryptPasswordEncoder(12)验证结果: " + encoder12Matches);
        
        // 解析密码信息
        PasswordUtil.BCryptInfo info = PasswordUtil.parseBCryptPassword(encodedPassword);
        if (info != null) {
            System.out.println("密码信息: " + info);
            System.out.println("成本因子: " + info.getCost());
        }
        
        // 生成相同密码的新加密结果
        String newEncoded10 = encoder10.encode(testPassword);
        String newEncoded12 = encoder12.encode(testPassword);
        
        System.out.println("新生成的加密密码(强度10): " + newEncoded10);
        System.out.println("新生成的加密密码(强度12): " + newEncoded12);
        
        // 验证新生成的密码
        boolean newMatches10 = encoder10.matches(testPassword, newEncoded10);
        boolean newMatches12 = encoder12.matches(testPassword, newEncoded12);
        
        System.out.println("新密码验证结果(强度10): " + newMatches10);
        System.out.println("新密码验证结果(强度12): " + newMatches12);
        
        // 断言
        assertTrue(passwordUtilMatches, "PasswordUtil应该能验证成功");
        assertTrue(encoder10Matches, "BCryptPasswordEncoder(10)应该能验证成功");
        assertTrue(encoder12Matches, "BCryptPasswordEncoder(12)应该能验证成功");
        assertTrue(newMatches10, "新生成的密码(强度10)应该能验证成功");
        assertTrue(newMatches12, "新生成的密码(强度12)应该能验证成功");
    }
    
    @Test
    public void testPasswordEncoderCompatibility() {
        String testPassword = "123456";
        
        System.out.println("=== 测试密码编码器兼容性 ===");
        
        // 创建不同强度的编码器
        BCryptPasswordEncoder encoder10 = new BCryptPasswordEncoder(10);
        BCryptPasswordEncoder encoder12 = new BCryptPasswordEncoder(12);
        
        // 使用强度10编码器加密
        String encoded10 = encoder10.encode(testPassword);
        System.out.println("强度10加密结果: " + encoded10);
        
        // 使用强度12编码器加密
        String encoded12 = encoder12.encode(testPassword);
        System.out.println("强度12加密结果: " + encoded12);
        
        // 测试交叉验证
        boolean crossValidation1 = encoder10.matches(testPassword, encoded12);
        boolean crossValidation2 = encoder12.matches(testPassword, encoded10);
        
        System.out.println("强度10验证强度12的密码: " + crossValidation1);
        System.out.println("强度12验证强度10的密码: " + crossValidation2);
        
        // 使用PasswordUtil验证
        boolean passwordUtilValidation1 = PasswordUtil.matches(testPassword, encoded10);
        boolean passwordUtilValidation2 = PasswordUtil.matches(testPassword, encoded12);
        
        System.out.println("PasswordUtil验证强度10的密码: " + passwordUtilValidation1);
        System.out.println("PasswordUtil验证强度12的密码: " + passwordUtilValidation2);
        
        // 断言
        assertTrue(crossValidation1, "不同强度的编码器应该能交叉验证");
        assertTrue(crossValidation2, "不同强度的编码器应该能交叉验证");
        assertTrue(passwordUtilValidation1, "PasswordUtil应该能验证强度10的密码");
        assertTrue(passwordUtilValidation2, "PasswordUtil应该能验证强度12的密码");
    }
} 