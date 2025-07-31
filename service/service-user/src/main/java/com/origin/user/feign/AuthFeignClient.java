package com.origin.user.feign;

import com.origin.common.dto.ResultData;

import com.origin.user.entity.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 认证服务Feign客户端
 * 
 * @author scccy
 * @since 2024-07-30
 */
@FeignClient(name = "service-auth", fallback = AuthFeignClientFallback.class)
public interface AuthFeignClient {
    
    /**
     * 验证JWT令牌
     *
     * @param token JWT令牌
     * @return 验证结果
     */
    @PostMapping("/auth/validate")
    ResultData<Boolean> validateToken(@RequestParam("token") String token);
    
    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/auth/user/info")
    ResultData<SysUser> getUserInfo(@RequestParam("userId") String userId);
    
    /**
     * 获取用户权限
     *
     * @param userId 用户ID
     * @return 用户权限信息
     */
    @GetMapping("/auth/user/permissions")
    ResultData<Map<String, Object>> getUserPermissions(@RequestParam("userId") String userId);
} 