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

        // 校验密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
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

        // 将 token 标记为有效并存储到 Redis
        tokenBlacklistUtil.markAsValid(token, jwtExpiration / 1000);

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
}