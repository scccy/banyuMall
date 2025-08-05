package com.origin.user.feign;

import com.origin.common.dto.LoginRequest;
import com.origin.common.dto.PasswordEncryptRequest;
import com.origin.common.dto.PasswordEncryptResponse;
import com.origin.common.dto.ResultData;
import com.origin.common.entity.ThirdPartyConfig;
import com.origin.user.entity.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 认证服务Feign客户端（包含用户认证、密码管理和第三方配置功能）
 * 
 * @author scccy
 * @since 2025-07-31
 */
@FeignClient(name = "service-auth", path = "/service/auth", fallback = AuthFeignClientFallback.class)
public interface AuthFeignClient {
    
    // ==================== 用户认证功能 ====================
    
    /**
     * 用户登录
     *
     * @param loginRequest 登录请求参数
     * @return 登录结果
     */
    @PostMapping("/login")
    ResultData login(@RequestBody LoginRequest loginRequest);

    /**
     * 用户登出
     *
     * @param authorization 授权头
     * @return 登出结果
     */
    @PostMapping("/logout")
    ResultData logout(@RequestHeader("Authorization") String authorization);
    
    /**
     * 验证JWT令牌
     *
     * @param token JWT令牌
     * @return 验证结果
     */
    @PostMapping("/validate")
    ResultData<Boolean> validateToken(@RequestParam("token") String token);
    
    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/user/info")
    ResultData<SysUser> getUserInfo(@RequestParam("userId") String userId);
    
    /**
     * 获取用户权限
     *
     * @param userId 用户ID
     * @return 用户权限信息
     */
    @GetMapping("/user/permissions")
    ResultData<Map<String, Object>> getUserPermissions(@RequestParam("userId") String userId);
    
    // ==================== 密码管理功能 ====================
    
    /**
     * 密码加密
     *
     * @param request 密码加密请求
     * @return 加密后的密码
     */
    @PostMapping("/password/encrypt")
    ResultData<PasswordEncryptResponse> encryptPassword(@RequestBody PasswordEncryptRequest request);
    
    /**
     * 密码验证
     *
     * @param request 密码验证请求
     * @return 验证结果
     */
    @PostMapping("/password/verify")
    ResultData<Boolean> verifyPassword(@RequestBody PasswordEncryptRequest request);
    
    // ==================== 第三方配置管理功能 ====================
    
    /**
     * 根据平台类型查询配置
     * 
     * @param platformType 平台类型
     * @return 配置信息
     */
    @GetMapping("/third-party/config/platform/{platformType}")
    ResultData<ThirdPartyConfig> getConfigByPlatformType(@PathVariable("platformType") Integer platformType);

    /**
     * 根据配置ID查询配置
     * 
     * @param configId 配置ID
     * @return 配置信息
     */
    @GetMapping("/third-party/config/{configId}")
    ResultData<ThirdPartyConfig> getConfigById(@PathVariable("configId") Integer configId);

    /**
     * 第三方配置健康检查
     * 
     * @return 服务状态
     */
    @GetMapping("/third-party/config/test")
    ResultData<String> testThirdPartyConfig();
} 