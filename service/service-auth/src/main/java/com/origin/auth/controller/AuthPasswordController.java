package com.origin.auth.controller;

import com.origin.common.dto.PasswordEncryptRequest;
import com.origin.common.dto.PasswordEncryptResponse;
import com.origin.auth.service.SysUserService;
import com.origin.common.dto.ResultData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证密码管理控制器
 * 
 * @author scccy
 * @since 2025-08-01
 */
@Slf4j
@RestController
@RequestMapping("/auth/password")
@RequiredArgsConstructor
public class AuthPasswordController {
    
    private final PasswordEncoder passwordEncoder;
    private final SysUserService sysUserService;
    
    /**
     * 密码加密
     *
     * @param request 密码加密请求
     * @return 加密后的密码
     */
    @PostMapping("/encrypt")
    public ResultData<PasswordEncryptResponse> encryptPassword(@RequestBody PasswordEncryptRequest request) {
        log.info("密码加密请求 - 用户名: {}", request.getUsername());
        
        try {
            // 使用BCrypt加密密码
            String encryptedPassword = passwordEncoder.encode(request.getPassword());
            
            PasswordEncryptResponse response = new PasswordEncryptResponse()
                    .setEncryptedPassword(encryptedPassword)
                    .setUsername(request.getUsername());
            
            log.info("密码加密成功 - 用户名: {}", request.getUsername());
            return ResultData.success(response);
        } catch (Exception e) {
            log.error("密码加密失败 - 用户名: {}, 错误: {}", request.getUsername(), e.getMessage());
            return ResultData.fail("密码加密失败");
        }
    }
    
    /**
     * 密码验证
     *
     * @param request 密码验证请求
     * @return 验证结果
     */
    @PostMapping("/verify")
    public ResultData<Boolean> verifyPassword(@RequestBody PasswordEncryptRequest request) {
        log.info("密码验证请求 - 用户名: {}", request.getUsername());
        
        try {
            // 根据用户名获取用户信息
            var user = sysUserService.getByUsername(request.getUsername());
            if (user == null) {
                log.warn("用户不存在 - 用户名: {}", request.getUsername());
                return ResultData.success(false);
            }
            
            // 验证密码
            boolean isValid = passwordEncoder.matches(request.getPassword(), user.getPassword());
            
            log.info("密码验证完成 - 用户名: {}, 验证结果: {}", request.getUsername(), isValid);
            return ResultData.success(isValid);
        } catch (Exception e) {
            log.error("密码验证失败 - 用户名: {}, 错误: {}", request.getUsername(), e.getMessage());
            return ResultData.fail("密码验证失败");
        }
    }
} 