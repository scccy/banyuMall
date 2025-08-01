package com.origin.user.feign;

import com.origin.common.dto.PasswordEncryptRequest;
import com.origin.common.dto.PasswordEncryptResponse;
import com.origin.common.dto.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 认证模块密码管理Feign客户端
 * 
 * @author scccy
 * @since 2025-08-01
 */
@FeignClient(name = "service-auth", fallback = AuthPasswordFeignClientFallback.class)
public interface AuthPasswordFeignClient {
    
    /**
     * 密码加密
     *
     * @param request 密码加密请求
     * @return 加密后的密码
     */
    @PostMapping("/auth/password/encrypt")
    ResultData<PasswordEncryptResponse> encryptPassword(@RequestBody PasswordEncryptRequest request);
    
    /**
     * 密码验证
     *
     * @param request 密码验证请求
     * @return 验证结果
     */
    @PostMapping("/auth/password/verify")
    ResultData<Boolean> verifyPassword(@RequestBody PasswordEncryptRequest request);
} 