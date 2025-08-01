package com.origin.common.util;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * 验证工具类
 * 提供通用的验证方法
 * 
 * @author scccy
 * @since 2025-08-01
 */
public class ValidationUtils {
    
    /**
     * 手机号格式正则表达式（中国大陆）
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    /**
     * 邮箱格式正则表达式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /**
     * 身份证号格式正则表达式（中国大陆）
     */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
    
    /**
     * 验证对象是否为空
     * 
     * @param obj 待验证对象
     * @return true 如果对象为空，false 否则
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        
        if (obj instanceof String) {
            return ((String) obj).trim().isEmpty();
        }
        
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        
        if (obj instanceof Object[]) {
            return ((Object[]) obj).length == 0;
        }
        
        return false;
    }
    
    /**
     * 验证对象是否不为空
     * 
     * @param obj 待验证对象
     * @return true 如果对象不为空，false 否则
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
    
    /**
     * 验证字符串长度是否在指定范围内
     * 
     * @param str 待验证字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return true 如果长度在范围内，false 否则
     */
    public static boolean isLengthInRange(String str, int minLength, int maxLength) {
        if (isEmpty(str)) {
            return false;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * 验证数字是否在指定范围内
     * 
     * @param num 待验证数字
     * @param min 最小值
     * @param max 最大值
     * @return true 如果在范围内，false 否则
     */
    public static boolean isNumberInRange(Number num, Number min, Number max) {
        if (num == null || min == null || max == null) {
            return false;
        }
        double value = num.doubleValue();
        double minValue = min.doubleValue();
        double maxValue = max.doubleValue();
        return value >= minValue && value <= maxValue;
    }
    
    /**
     * 验证手机号格式（中国大陆）
     * 
     * @param phone 手机号
     * @return true 如果格式正确，false 否则
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * 验证邮箱格式
     * 
     * @param email 邮箱
     * @return true 如果格式正确，false 否则
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * 验证身份证号格式（中国大陆）
     * 
     * @param idCard 身份证号
     * @return true 如果格式正确，false 否则
     */
    public static boolean isValidIdCard(String idCard) {
        if (isEmpty(idCard)) {
            return false;
        }
        return ID_CARD_PATTERN.matcher(idCard.trim()).matches();
    }
    
    /**
     * 验证是否为有效的URL
     * 
     * @param url URL字符串
     * @return true 如果是有效URL，false 否则
     */
    public static boolean isValidUrl(String url) {
        if (isEmpty(url)) {
            return false;
        }
        try {
            new java.net.URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 验证是否为有效的IP地址
     * 
     * @param ip IP地址字符串
     * @return true 如果是有效IP地址，false 否则
     */
    public static boolean isValidIpAddress(String ip) {
        if (isEmpty(ip)) {
            return false;
        }
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        
        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 验证字符串是否只包含数字
     * 
     * @param str 待验证字符串
     * @return true 如果只包含数字，false 否则
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("^\\d+$");
    }
    
    /**
     * 验证字符串是否只包含字母
     * 
     * @param str 待验证字符串
     * @return true 如果只包含字母，false 否则
     */
    public static boolean isAlpha(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("^[a-zA-Z]+$");
    }
    
    /**
     * 验证字符串是否只包含字母和数字
     * 
     * @param str 待验证字符串
     * @return true 如果只包含字母和数字，false 否则
     */
    public static boolean isAlphanumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("^[a-zA-Z0-9]+$");
    }
} 