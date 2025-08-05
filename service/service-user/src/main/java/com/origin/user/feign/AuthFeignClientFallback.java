package com.origin.user.feign;

import com.origin.common.dto.LoginRequest;
import com.origin.common.dto.PasswordEncryptRequest;
import com.origin.common.dto.PasswordEncryptResponse;
import com.origin.common.dto.ResultData;
import com.origin.common.entity.ThirdPartyConfig;
import com.origin.user.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 认证服务Feign客户端降级处理
 * 
 * @author scccy
 * @since 2025-07-31
 */
@Slf4j
@Component
public class AuthFeignClientFallback implements AuthFeignClient {
    
    // ==================== 用户认证功能降级处理 ====================
    
    @Override
    public ResultData login(LoginRequest loginRequest) {
        log.error("认证服务调用失败 - login, username: {}", loginRequest.getUsername());
        return ResultData.fail("认证服务暂时不可用");
    }
    
    @Override
    public ResultData logout(String authorization) {
        log.error("认证服务调用失败 - logout");
        return ResultData.fail("认证服务暂时不可用");
    }
    
    @Override
    public ResultData<Boolean> validateToken(String token) {
        log.error("认证服务调用失败 - validateToken, token: {}", token);
        return ResultData.fail("认证服务暂时不可用");
    }
    
    @Override
    public ResultData<SysUser> getUserInfo(String userId) {
        log.error("认证服务调用失败 - getUserInfo, userId: {}", userId);
        return ResultData.fail("认证服务暂时不可用");
    }
    
    @Override
    public ResultData<Map<String, Object>> getUserPermissions(String userId) {
        log.error("认证服务调用失败 - getUserPermissions, userId: {}", userId);
        return ResultData.fail("认证服务暂时不可用");
    }
    
    // ==================== 密码管理功能降级处理 ====================
    
    @Override
    public ResultData<PasswordEncryptResponse> encryptPassword(PasswordEncryptRequest request) {
        log.error("认证服务调用失败 - encryptPassword, username: {}", request.getUsername());
        return ResultData.fail("密码加密服务暂时不可用");
    }
    
    @Override
    public ResultData<Boolean> verifyPassword(PasswordEncryptRequest request) {
        log.error("认证服务调用失败 - verifyPassword, username: {}", request.getUsername());
        return ResultData.fail("密码验证服务暂时不可用");
    }
    
    // ==================== 第三方配置管理功能降级处理 ====================
    
    @Override
    public ResultData<ThirdPartyConfig> getConfigByPlatformType(Integer platformType) {
        log.error("认证服务调用失败 - getConfigByPlatformType, platformType: {}", platformType);
        return ResultData.fail("第三方配置服务暂时不可用");
    }
    
    @Override
    public ResultData<ThirdPartyConfig> getConfigById(Integer configId) {
        log.error("认证服务调用失败 - getConfigById, configId: {}", configId);
        return ResultData.fail("第三方配置服务暂时不可用");
    }
    
    @Override
    public ResultData<String> testThirdPartyConfig() {
        log.error("认证服务调用失败 - testThirdPartyConfig");
        return ResultData.fail("第三方配置服务暂时不可用");
    }
} 