package com.origin.auth.controller;

import com.origin.auth.util.JwtTestUtil;
import com.origin.common.dto.ResultData;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT测试控制器 - 用于测试JWT功能
 * 仅用于开发和测试环境
 * 
 * @author origin
 * @since 2025-07-31
 */
@Tag(name = "JWT测试", description = "JWT令牌生成和校验测试接口")
@RestController
@RequestMapping("/jwt-test")
@Slf4j
public class JwtTestController {

    /**
     * 生成JWT令牌
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT令牌
     */
    @Operation(summary = "生成JWT令牌", description = "根据用户ID和用户名生成JWT令牌")
    @PostMapping("/generate")
    public ResultData<Map<String, Object>> generateToken(@RequestParam String userId, 
                                                        @RequestParam String username) {
        try {
            String token = JwtTestUtil.generateToken(userId, username);
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userId", userId);
            result.put("username", username);
            result.put("expirationTime", JwtTestUtil.getExpirationTime(token));
            
            log.info("生成JWT令牌成功 - 用户ID: {}, 用户名: {}", userId, username);
            return ResultData.ok("JWT令牌生成成功", result);
        } catch (Exception e) {
            log.error("生成JWT令牌失败: {}", e.getMessage());
            return ResultData.fail("JWT令牌生成失败: " + e.getMessage());
        }
    }

    /**
     * 验证JWT令牌
     * 
     * @param token JWT令牌
     * @return 验证结果
     */
    @Operation(summary = "验证JWT令牌", description = "验证JWT令牌的有效性")
    @PostMapping("/validate")
    public ResultData<Map<String, Object>> validateToken(@RequestParam String token) {
        try {
            boolean isValid = JwtTestUtil.validateToken(token);
            boolean isExpired = JwtTestUtil.isTokenExpired(token);
            
            Map<String, Object> result = new HashMap<>();
            result.put("valid", isValid);
            result.put("expired", isExpired);
            
            if (isValid && !isExpired) {
                result.put("userId", JwtTestUtil.getUserIdFromToken(token));
                result.put("username", JwtTestUtil.getUsernameFromToken(token));
                result.put("expirationTime", JwtTestUtil.getExpirationTime(token));
            }
            
            log.info("JWT令牌验证完成 - 有效: {}, 过期: {}", isValid, isExpired);
            return ResultData.ok("JWT令牌验证完成", result);
        } catch (Exception e) {
            log.error("JWT令牌验证失败: {}", e.getMessage());
            return ResultData.fail("JWT令牌验证失败: " + e.getMessage());
        }
    }

    /**
     * 解析JWT令牌
     * 
     * @param token JWT令牌
     * @return 令牌内容
     */
    @Operation(summary = "解析JWT令牌", description = "解析JWT令牌的所有内容")
    @PostMapping("/parse")
    public ResultData<Map<String, Object>> parseToken(@RequestParam String token) {
        try {
            Claims claims = JwtTestUtil.parseToken(token);
            if (claims == null) {
                return ResultData.fail("JWT令牌解析失败");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", claims.get("userId"));
            result.put("username", claims.get("username"));
            result.put("roles", claims.get("roles"));
            result.put("permissions", claims.get("permissions"));
            result.put("issuedAt", claims.getIssuedAt());
            result.put("expiration", claims.getExpiration());
            result.put("valid", JwtTestUtil.validateToken(token));
            result.put("expired", JwtTestUtil.isTokenExpired(token));
            
            log.info("JWT令牌解析成功 - 用户ID: {}, 用户名: {}", 
                    claims.get("userId"), claims.get("username"));
            return ResultData.ok("JWT令牌解析成功", result);
        } catch (Exception e) {
            log.error("JWT令牌解析失败: {}", e.getMessage());
            return ResultData.fail("JWT令牌解析失败: " + e.getMessage());
        }
    }

    /**
     * 获取令牌信息
     * 
     * @param token JWT令牌
     * @return 令牌信息
     */
    @Operation(summary = "获取令牌信息", description = "获取JWT令牌的基本信息")
    @GetMapping("/info")
    public ResultData<Map<String, Object>> getTokenInfo(@RequestParam String token) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("valid", JwtTestUtil.validateToken(token));
            result.put("expired", JwtTestUtil.isTokenExpired(token));
            result.put("userId", JwtTestUtil.getUserIdFromToken(token));
            result.put("username", JwtTestUtil.getUsernameFromToken(token));
            result.put("expirationTime", JwtTestUtil.getExpirationTime(token));
            
            // 计算剩余时间
            long expirationTime = JwtTestUtil.getExpirationTime(token);
            long currentTime = System.currentTimeMillis();
            long remainingTime = expirationTime - currentTime;
            result.put("remainingTimeSeconds", remainingTime / 1000);
            result.put("remainingTimeMinutes", remainingTime / (1000 * 60));
            
            log.info("获取JWT令牌信息成功");
            return ResultData.ok("获取JWT令牌信息成功", result);
        } catch (Exception e) {
            log.error("获取JWT令牌信息失败: {}", e.getMessage());
            return ResultData.fail("获取JWT令牌信息失败: " + e.getMessage());
        }
    }
} 