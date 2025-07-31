package com.origin.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.auth.dto.LoginRequest;
import com.origin.auth.dto.LoginResponse;
import com.origin.auth.entity.SysUser;

import com.origin.auth.mapper.SysUserMapper;
import com.origin.auth.service.SysPermissionService;
import com.origin.auth.service.SysRoleService;
import com.origin.auth.service.SysUserService;

import com.origin.auth.util.JwtUtil;
import com.origin.auth.util.TokenBlacklistUtil;
import com.origin.auth.util.PasswordUtil;
import com.origin.common.exception.BusinessException;
import com.origin.common.entity.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;


/**
 * 系统用户服务实现类
 * 
 * @author origin
 * @since 2024-07-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    private final SysRoleService sysRoleService;
    private final SysPermissionService sysPermissionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistUtil tokenBlacklistUtil;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    @Value("${jwt.token-prefix}")
    private String tokenPrefix;
    
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 根据用户名查询用户
        SysUser user = getByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }

        // 添加调试日志
        log.info("登录验证 - 用户名: {}, 输入密码: {}, 数据库密码: {}", 
                loginRequest.getUsername(), 
                maskPassword(loginRequest.getPassword()), 
                user.getPassword());
        
        // 使用注入的PasswordEncoder进行密码验证
        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        log.info("PasswordEncoder验证结果: {}", passwordMatches);
        
        // 同时使用PasswordUtil进行验证（用于对比）
        boolean passwordUtilMatches = PasswordUtil.matches(loginRequest.getPassword(), user.getPassword());
        log.info("PasswordUtil验证结果: {}", passwordUtilMatches);
        
        if (!passwordMatches) {
            log.error("密码验证失败 - 用户名: {}, 输入密码: {}, 数据库密码: {}", 
                    loginRequest.getUsername(), 
                    maskPassword(loginRequest.getPassword()), 
                    user.getPassword());
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }

        // 校验用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "账号已被禁用");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        updateById(user);

        // 查询用户角色
        List<String> roles = sysRoleService.getRoleCodesByUserId(user.getId());

        // 查询用户权限
        List<String> permissions = sysPermissionService.getPermissionCodesByUserId(user.getId());

        // 生成JWT令牌（包含角色和权限）
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roles, permissions);

        // 更新用户token到Redis（避免重复存储）
        tokenBlacklistUtil.updateUserToken(user.getId(), token, jwtExpiration / 1000);

        // 构建登录响应
        return new LoginResponse()
                .setUserId(user.getId())
                .setUsername(user.getUsername())
                .setNickname(user.getNickname())
                .setAvatar(user.getAvatar())
                .setRoles(roles)
                .setPermissions(permissions)
                .setToken(token)
                .setTokenType(tokenPrefix.trim())
                .setExpiresIn(jwtExpiration / 1000);
    }
    
    @Override
    public SysUser getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }
    
    /**
     * 掩码密码（用于日志安全）
     */
    private String maskPassword(String password) {
        if (password == null || password.length() <= 2) {
            return "***";
        }
        return password.substring(0, 1) + "***" + password.substring(password.length() - 1);
    }
}