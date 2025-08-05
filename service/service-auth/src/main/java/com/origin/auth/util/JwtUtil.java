package com.origin.auth.util;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT工具类，用于生成和验证JWT令牌
 * 优化版本 - 支持FastJSON2和角色权限
 * 
 * @author origin
 * @since 2025-07-31
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    /**
     * 生成JWT令牌
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT令牌
     */
    public String generateToken(String userId, String username) {
        return generateToken(userId, username, (Map<String, Object>) null);
    }

    /**
     * 生成JWT令牌（带自定义声明）
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param claims 自定义声明
     * @return JWT令牌
     */
    public String generateToken(String userId, String username, Map<String, Object> claims) {
        Map<String, Object> tokenClaims = new HashMap<>();
        tokenClaims.put("userId", userId);
        tokenClaims.put("username", username);
        
        // 添加自定义声明
        if (claims != null && !claims.isEmpty()) {
            tokenClaims.putAll(claims);
        }
        
        return createToken(tokenClaims);
    }

    /**
     * 生成JWT令牌（带角色和权限）
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param roles 角色列表
     * @param permissions 权限列表
     * @return JWT令牌
     */
    public String generateToken(String userId, String username, List<String> roles, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        
        if (roles != null && !roles.isEmpty()) {
            claims.put("roles", JSON.toJSONString(roles));
        }
        
        if (permissions != null && !permissions.isEmpty()) {
            claims.put("permissions", JSON.toJSONString(permissions));
        }
        
        return createToken(claims);
    }

    /**
     * 生成刷新令牌
     *
     * @param userId 用户ID
     * @return 刷新令牌
     */
    public String generateRefreshToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        return createRefreshToken(claims);
    }

    /**
     * 从令牌中获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("userId", String.class);
        } catch (Exception e) {
            log.error("从JWT令牌获取用户ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("username", String.class);
        } catch (Exception e) {
            log.error("从JWT令牌获取用户名失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从令牌中获取角色列表
     *
     * @param token 令牌
     * @return 角色列表
     */
    public List<String> getRolesFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String rolesJson = claims.get("roles", String.class);
            if (rolesJson != null) {
                return JSON.parseArray(rolesJson, String.class);
            }
            return null;
        } catch (Exception e) {
            log.error("从JWT令牌获取角色失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从令牌中获取权限列表
     *
     * @param token 令牌
     * @return 权限列表
     */
    public List<String> getPermissionsFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String permissionsJson = claims.get("permissions", String.class);
            if (permissionsJson != null) {
                return JSON.parseArray(permissionsJson, String.class);
            }
            return null;
        } catch (Exception e) {
            log.error("从JWT令牌获取权限失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从令牌中获取指定声明
     *
     * @param token 令牌
     * @param claimName 声明名称
     * @param claimType 声明类型
     * @return 声明值
     */
    public <T> T getClaimFromToken(String token, String claimName, Class<T> claimType) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get(claimName, claimType);
        } catch (Exception e) {
            log.error("从JWT令牌获取声明失败 - 声明名: {}, 错误: {}", claimName, e.getMessage());
            return null;
        }
    }

    /**
     * 验证令牌是否有效
     *
     * @param token 令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            boolean isValid = !expiration.before(new Date());
            log.debug("JWT令牌验证结果: {}, 过期时间: {}", isValid, expiration);
            return isValid;
        } catch (Exception e) {
            log.error("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取令牌的剩余过期时间（秒）
     *
     * @param token 令牌
     * @return 剩余过期时间（秒），如果已过期或无效返回0
     */
    public long getExpirationTime(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            Date now = new Date();
            if (expiration.before(now)) {
                return 0;
            }
            return (expiration.getTime() - now.getTime()) / 1000;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 从令牌中获取声明
     *
     * @param token 令牌
     * @return 声明
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 创建令牌
     *
     * @param claims 声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 创建刷新令牌
     *
     * @param claims 声明
     * @return 刷新令牌
     */
    private String createRefreshToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
} 