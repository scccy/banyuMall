package com.origin.user.feign;

import com.origin.common.dto.PasswordEncryptRequest;
import com.origin.common.dto.PasswordEncryptResponse;
import com.origin.common.dto.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 认证模块密码管理Feign客户端降级处理
 * 
 * @author scccy
 * @since 2025-08-01
 */
@Slf4j
@Component
public class AuthPasswordFeignClientFallback implements AuthPasswordFeignClient {
    
    @Override
    public ResultData<PasswordEncryptResponse> encryptPassword(PasswordEncryptRequest request) {
        log.error("密码加密服务调用失败，触发降级处理 - 用户名: {}", request.getUsername());
        return ResultData.fail("密码加密服务暂时不可用");
    }
    
    @Override
    public ResultData<Boolean> verifyPassword(PasswordEncryptRequest request) {
        log.error("密码验证服务调用失败，触发降级处理 - 用户名: {}", request.getUsername());
        return ResultData.fail("密码验证服务暂时不可用");
    }
} 