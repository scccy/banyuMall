package com.origin.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT测试工具类 - 用于生成和校验JWT令牌
 * 仅用于开发和测试环境
 * 
 * @author origin
 * @since 2025-07-31
 */
@Slf4j
public class JwtTestUtil {
    
    // JWT密钥 - 开发环境使用默认密钥
    private static final String SECRET = "nG9dT@e4^M7#pKc!Wz0qF8vRtLx*A6s1YhJ2BrCm";
    
    // 密钥对象
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    
    // 默认过期时间：1小时
    private static final long DEFAULT_EXPIRATION = 3600 * 1000L;
    
    /**
     * 生成JWT令牌
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT令牌
     */
    public static String generateToken(String userId, String username) {
        return generateToken(userId, username, DEFAULT_EXPIRATION);
    }
    
    /**
     * 生成JWT令牌（指定过期时间）
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param expiration 过期时间（毫秒）
     * @return JWT令牌
     */
    public static String generateToken(String userId, String username, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("roles", "ADMIN"); // 默认角色
        claims.put("permissions", "user:read,user:write"); // 默认权限
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 验证JWT令牌
     * 
     * @param token JWT令牌
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 从JWT令牌中获取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID
     */
    public static String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            return claims.get("userId", String.class);
        } catch (Exception e) {
            log.error("从JWT令牌获取用户ID失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从JWT令牌中获取用户名
     * 
     * @param token JWT令牌
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            return claims.get("username", String.class);
        } catch (Exception e) {
            log.error("从JWT令牌获取用户名失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从JWT令牌中获取过期时间
     * 
     * @param token JWT令牌
     * @return 过期时间戳
     */
    public static long getExpirationTime(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            return claims.getExpiration().getTime();
        } catch (Exception e) {
            log.error("从JWT令牌获取过期时间失败: {}", e.getMessage());
            return 0;
        }
    }
    
    /**
     * 解析JWT令牌的所有声明
     * 
     * @param token JWT令牌
     * @return 所有声明
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (Exception e) {
            log.error("解析JWT令牌失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 检查JWT令牌是否过期
     * 
     * @param token JWT令牌
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("检查JWT令牌过期状态失败: {}", e.getMessage());
            return true;
        }
    }


    /**
     * 测试方法 - 生成测试令牌
     */
    public static void main(String[] args) {
        // 生成测试令牌
        String userId = "1";
        String username = "admin";
        
        System.out.println("=== JWT测试工具 ===");
        System.out.println("用户ID: " + userId);
        System.out.println("用户名: " + username);
        
        // 生成令牌
        String token = generateToken(userId, username);
        System.out.println("\n生成的JWT令牌:");
        System.out.println(token);
        
        // 验证令牌
        boolean isValid = validateToken(token);
        System.out.println("\n令牌验证结果: " + isValid);
        
        // 解析令牌
        Claims claims = parseToken(token);
        if (claims != null) {
            System.out.println("\n令牌内容:");
            System.out.println("用户ID: " + claims.get("userId"));
            System.out.println("用户名: " + claims.get("username"));
            System.out.println("角色: " + claims.get("roles"));
            System.out.println("权限: " + claims.get("permissions"));
            System.out.println("签发时间: " + claims.getIssuedAt());
            System.out.println("过期时间: " + claims.getExpiration());
        }
        
        // 检查是否过期
        boolean isExpired = isTokenExpired(token);
        System.out.println("\n令牌是否过期: " + isExpired);
        
        // 获取剩余时间
        long expirationTime = getExpirationTime(token);
        long currentTime = System.currentTimeMillis();
        long remainingTime = expirationTime - currentTime;
        System.out.println("剩余时间: " + (remainingTime / 1000) + " 秒");
    }
} 