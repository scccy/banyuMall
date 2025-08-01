package com.origin.auth.controller;

import com.origin.auth.dto.LoginRequest;
import com.origin.auth.dto.LoginResponse;
import com.origin.auth.service.AuthService;
import com.origin.auth.util.JwtUtil;
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
@RequestMapping("/service/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

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
        
        LoginResponse loginResponse = authService.login(loginRequest);
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
        // 从请求头中获取链路追踪信息
        String requestId = request.getHeader("X-Request-ID");
        String clientIp = request.getHeader("X-Client-IP");
        String userAgent = request.getHeader("X-User-Agent");
        
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            log.warn("登出失败 - 缺少有效的Authorization头");
            return ResultData.fail("登出失败：缺少有效的token");
        }
        
        token = token.substring(7);
        
        try {
            // 从token中提取用户信息用于日志记录
            String userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            
            // 记录登出日志
            log.info("用户登出 - 用户ID: {}, 用户名: {}, 客户端IP: {}, 用户代理: {}, 请求ID: {}", 
                    userId, username, clientIp, userAgent, requestId);
            
            // 调用业务层处理登出逻辑
            authService.logout(token);
            
            return ResultData.ok("登出成功");
            
        } catch (Exception e) {
            log.error("登出失败: {}", e.getMessage());
            return ResultData.fail("登出失败：" + e.getMessage());
        }
    }

    /**
     * 认证服务健康检查
     *
     * @param request HTTP请求
     * @return 健康检查结果
     */
    @Operation(summary = "认证服务健康检查", description = "用于验证认证服务是否正常运行的接口")
    @GetMapping("/test")
    public ResultData<String> test(HttpServletRequest request) {
        // 从请求头中获取链路追踪信息
        String requestId = request.getHeader("X-Request-ID");
        String clientIp = request.getHeader("X-Client-IP");
        String userAgent = request.getHeader("X-User-Agent");
        
        log.info("Auth Test - RequestId: {}, ClientIP: {}, UserAgent: {}", 
                requestId, clientIp, userAgent);
        
        return ResultData.ok("Auth Service is running!");
    }

    /**
     * 强制登出用户（管理员功能）
     *
     * @param userId 用户ID
     * @param request HTTP请求
     * @return 强制登出结果
     */
    @Operation(summary = "强制登出用户", description = "管理员强制登出指定用户")
    @PostMapping("/logout/force/{userId}")
    public ResultData<String> forceLogout(@PathVariable String userId, HttpServletRequest request) {
        // 从请求头中获取链路追踪信息
        String requestId = request.getHeader("X-Request-ID");
        String clientIp = request.getHeader("X-Client-IP");
        String userAgent = request.getHeader("X-User-Agent");
        
        // TODO: 添加管理员权限验证
        // @PreAuthorize("hasRole('ADMIN')")
        
        try {
            // 调用业务层处理强制登出逻辑
            authService.forceLogout(userId);
            
            log.info("管理员强制登出用户 - 用户ID: {}, 操作者IP: {}, 请求ID: {}", 
                    userId, clientIp, requestId);
            
            return ResultData.ok("强制登出成功");
            
        } catch (Exception e) {
            log.error("强制登出失败 - 用户ID: {}, 错误: {}", userId, e.getMessage());
            return ResultData.fail("强制登出失败：" + e.getMessage());
        }
    }
}