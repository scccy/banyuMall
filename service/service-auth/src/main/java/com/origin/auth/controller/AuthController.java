package com.origin.auth.controller;

import com.origin.auth.entity.SysUser;
import com.origin.common.dto.LoginRequest;
import com.origin.common.dto.LoginResponse;
import com.origin.auth.service.AuthService;
import com.origin.auth.service.SysUserService;
import com.origin.auth.util.JwtUtil;
import com.origin.common.dto.PasswordEncryptRequest;
import com.origin.common.dto.PasswordEncryptResponse;
import com.origin.common.dto.ResultData;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

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
            return ResultData.ok("登出失败：缺少有效的token");
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
        
        log.info("Auth Health Check - RequestId: {}, ClientIP: {}, UserAgent: {}", 
                requestId, clientIp, userAgent);
        
        return ResultData.ok("认证服务运行正常");
    }

    /**
     * 强制登出用户
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
        
        log.info("强制登出用户 - RequestId: {}, ClientIP: {}, UserAgent: {}, UserId: {}", 
                requestId, clientIp, userAgent, userId);
        
        try {
            authService.forceLogout(userId);
            return ResultData.ok("强制登出成功");
        } catch (Exception e) {
            log.error("强制登出失败: {}", e.getMessage());
            return ResultData.fail("强制登出失败：" + e.getMessage());
        }
    }

    /**
     * 密码加密
     *
     * @param request 密码加密请求
     * @return 加密后的密码
     */
    @Operation(summary = "密码加密", description = "为其他微服务提供密码加密功能")
    @PostMapping("/password/encrypt")
    public ResultData<PasswordEncryptResponse> encryptPassword(@RequestBody PasswordEncryptRequest request) {
        log.info("密码加密请求 - 用户名: {}", request.getUsername());
        
        try {
            String encryptedPassword = passwordEncoder.encode(request.getPassword());
            
            PasswordEncryptResponse response = new PasswordEncryptResponse();
            response.setUsername(request.getUsername());
            response.setEncryptedPassword(encryptedPassword);
            
            log.info("密码加密成功 - 用户名: {}", request.getUsername());
            return ResultData.ok("密码加密成功", response);
            
        } catch (Exception e) {
            log.error("密码加密失败 - 用户名: {}, 错误: {}", request.getUsername(), e.getMessage());
            // 从业务角度考虑，密码加密失败不应该影响服务可用性
            // 返回200状态码，通过业务状态码500表示加密失败
            return ResultData.fail("密码加密失败：" + e.getMessage());
        }
    }

    /**
     * 密码验证
     *
     * @param request 密码验证请求
     * @return 验证结果
     */
    @Operation(summary = "密码验证", description = "为其他微服务提供密码验证功能")
    @PostMapping("/password/verify")
    public ResultData<Boolean> verifyPassword(@RequestBody PasswordEncryptRequest request) {
        log.info("密码验证请求 - 用户名: {}", request.getUsername());
        
        try {
            SysUser user = sysUserService.getByUsername(request.getUsername());
            if (user == null) {
                return ResultData.ok("用户不存在", false);
            }
            
            boolean isValid = passwordEncoder.matches(request.getPassword(), user.getPassword());
            
            log.info("密码验证完成 - 用户名: {}, 验证结果: {}", request.getUsername(), isValid);
            return ResultData.ok("密码验证完成", isValid);
            
        } catch (Exception e) {
            log.error("密码验证失败 - 用户名: {}, 错误: {}", request.getUsername(), e.getMessage());
            return ResultData.fail("密码验证失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Operation(summary = "获取用户信息", description = "为其他微服务提供用户信息查询功能")
    @GetMapping("/user/info")
    public ResultData<SysUser> getUserInfo(@RequestParam("userId") String userId) {
        log.info("获取用户信息 - 用户ID: {}", userId);
        
        try {
            SysUser user = sysUserService.getById(userId);
            if (user == null) {
                return ResultData.ok("用户不存在", null);
            }
            
            return ResultData.ok("获取用户信息成功", user);
            
        } catch (Exception e) {
            log.error("获取用户信息失败 - 用户ID: {}, 错误: {}", userId, e.getMessage());
            // 从业务角度考虑，获取用户信息失败不应该影响服务可用性
            // 返回200状态码，通过业务状态码500表示查询失败
            return ResultData.fail("获取用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 验证JWT令牌
     *
     * @param token JWT令牌
     * @return 验证结果
     */
    @Operation(summary = "验证JWT令牌", description = "为其他微服务提供JWT令牌验证功能")
    @PostMapping("/validate")
    public ResultData<Boolean> validateToken(@RequestParam("token") String token) {
        log.debug("验证JWT令牌");
        
        try {
            boolean isValid = jwtUtil.validateToken(token);
            return ResultData.ok("令牌验证完成", isValid);
            
        } catch (Exception e) {
            log.error("令牌验证失败: {}", e.getMessage());
            return ResultData.fail("令牌验证失败：" + e.getMessage());
        }
    }
}