package com.origin.auth.controller;

import com.origin.auth.dto.LoginRequest;
import com.origin.auth.dto.LoginResponse;
import com.origin.auth.service.SysUserService;
import com.origin.common.ResultData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
public class AuthController {

    private final SysUserService sysUserService;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求参数
     * @return 登录结果
     */
    @Operation(summary = "用户登录", description = "处理用户登录请求，返回用户信息和JWT令牌")
    @PostMapping("/login")
    public ResultData<LoginResponse> login(@RequestBody @Validated LoginRequest loginRequest) {
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
                long expirationTime = com.origin.common.util.JwtUtil.getExpirationTime(token);
                if (expirationTime > 0) {
                    com.origin.common.util.TokenBlacklistUtil.addToBlacklist(token, expirationTime);
                }
            } catch (Exception e) {
                // 如果无法解析token，设置一个默认的过期时间（1小时）
                com.origin.common.util.TokenBlacklistUtil.addToBlacklist(token, 3600);
            }
            
            // 从有效token列表中移除
            com.origin.common.util.TokenBlacklistUtil.removeFromValid(token);
        }
        
        return ResultData.ok("登出成功");
    }
}