package com.origin.auth.controller;

import com.origin.auth.dto.LoginRequest;
import com.origin.auth.dto.LoginResponse;
import com.origin.auth.service.SysUserService;
import com.origin.auth.util.JwtUtil;
import com.origin.auth.util.TokenBlacklistUtil;
import com.origin.common.dto.ResultData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "处理用户登录、登出等认证相关操作")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistUtil tokenBlacklistUtil;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求参数
     * @param request HTTP请求
     * @return 登录结果
     */
    @Operation(summary = "用户登录", description = "处理用户登录请求，返回用户信息和JWT令牌")
    @PostMapping("/login")
    public ResultData<LoginResponse> login(@RequestBody @Validated LoginRequest loginRequest, 
                                         HttpServletRequest request) {
        // 从请求头中获取链路追踪信息
        String requestId = request.getHeader("X-Request-ID");
        String clientIp = request.getHeader("X-Client-IP");
        String userAgent = request.getHeader("X-User-Agent");
        
        log.info("Auth Login - RequestId: {}, ClientIP: {}, UserAgent: {}, Username: {}", 
                requestId, clientIp, userAgent, loginRequest.getUsername());
        
        LoginResponse loginResponse = sysUserService.login(loginRequest);
        return ResultData.ok("登录成功", loginResponse);
    }

    /**
     * 用户登出
     *
     * @param request HTTP请求
     * @return 登出结果
     */
    @Operation(summary = "用户登出", description = "处理用户登出请求，清除用户会话信息")
    @PostMapping("/logout")
    public ResultData<String> logout(HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
            
            // 将token加入黑名单
            try {
                // 获取token的剩余过期时间
                long expirationTime = jwtUtil.getExpirationTime(token);
                if (expirationTime > 0) {
                    tokenBlacklistUtil.addToBlacklist(token, expirationTime);
                }
            } catch (Exception e) {
                // 如果无法解析token，设置一个默认的过期时间（1小时）
                tokenBlacklistUtil.addToBlacklist(token, 3600);
            }
            
            // 从有效token列表中移除
            tokenBlacklistUtil.removeFromValid(token);
        }
        
        return ResultData.ok("登出成功");
    }
}