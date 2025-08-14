package com.origin.banyu.auth.service.impl;

import com.origin.banyu.auth.dto.UserInfoResponse;
import com.origin.banyu.auth.service.AuthService;
import com.origin.banyu.auth.service.SysUserService;
import com.origin.banyu.auth.util.JwtTokenManager;
import com.origin.banyu.auth.util.JwtUtil;
import com.origin.banyu.common.dto.LoginRequest;
import com.origin.banyu.common.dto.LoginResponse;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.entity.SysUser;
import com.origin.banyu.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现类
 * 基于简化的权限控制，使用用户类型字段直接控制权限
 * 
 * @author scccy
 * @since 2025-07-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "auth:jwt")
public class AuthServiceImpl implements AuthService {

    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;
    private final JwtTokenManager jwtTokenManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录 - 用户名: {}", request.getUsername());
        
        // 验证用户ID(=手机号)和密码
       // 采用user_id登入
        SysUser user = sysUserService.getById(request.getUsername());
        if (user == null || !sysUserService.validatePassword(request.getPassword(), user.getPassword())) {
            log.warn("登录失败 - 用户名或密码错误: {}", request.getUsername());
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }
        
        // 检查用户状态
        if (!sysUserService.isUserStatusNormal(user)) {
            log.warn("登录失败 - 用户状态异常: {}", request.getUsername());
            throw new BusinessException(ErrorCode.FORBIDDEN, "账号已被禁用或待审核");
        }
        
        // 更新最后登录时间
        sysUserService.updateLastLoginTime(user.getUserId());
        
        // 生成JWT令牌（包含用户类型信息）
        Map<String, Object> claims = new HashMap<>();
        claims.put("userType", user.getUserType());
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), claims);
        
        // 将JWT存储到Redis中
        long expirationTime = jwtUtil.getExpirationTime(token);
        if (expirationTime > 0) {
            // 使用基于用户ID的token管理（推荐）
            jwtTokenManager.updateUserToken(user.getUserId(), token, expirationTime);
            // 或者使用token标记为有效的方式
            // jwtTokenManager.markAsValid(token, expirationTime);
        }
        
        log.info("用户登录成功 - 用户名: {}, 用户类型: {}, Token已存储到Redis", request.getUsername(), user.getUserType());
        
        return new LoginResponse()
                .setToken(token)
                .setUsername(user.getUsername())
                .setUserId(user.getUserId())
                .setUserType(user.getUserType())
                .setUserTypeDesc(getUserTypeDesc(user.getUserType()))
                .setNickname(user.getNickname())
                .setAvatar(user.getAvatar());
    }

    @Override
    @Cacheable(key = "#token")
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        
        return jwtUtil.validateToken(token);
    }

    @Override
    @CacheEvict(key = "#token")
    public void logout(String token) {
        log.info("用户登出 - 令牌: {}", token);
        
        // 从token中提取用户信息
        String userId = jwtUtil.getUserIdFromToken(token);
        
        if (userId != null) {
            // 使用基于用户ID的token管理
            jwtTokenManager.removeUserToken(userId);
            
            // 将token加入黑名单
            long expirationTime = jwtUtil.getExpirationTime(token);
            if (expirationTime > 0) {
                jwtTokenManager.addToBlacklist(token, expirationTime);
            }
            
            log.info("用户登出成功 - 用户ID: {}", userId);
        }
    }

    @Override
    public void forceLogout(String userId) {
        log.info("强制登出用户 - 用户ID: {}", userId);
        
        jwtTokenManager.removeUserToken(userId);
        log.info("强制登出成功 - 用户ID: {}", userId);
    }

    @Override
    @Cacheable(key = "#token")
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }
    
    @Override
    @Cacheable(key = "#token")
    public String getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }
    
    @Override
    @Cacheable(key = "#token")
    public Integer getUserTypeFromToken(String token) {
        return jwtUtil.getClaimFromToken(token, "userType", Integer.class);
    }
    
    @Override
    public boolean hasPermission(String userId, Integer requiredUserType) {
        SysUser user = sysUserService.getById(userId);
        if (user == null || !sysUserService.isUserStatusNormal(user)) {
            return false;
        }
        
        // 管理员拥有所有权限
        if (user.getUserType() == 1) {
            return true;
        }
        
        // 检查用户类型是否匹配
        return user.getUserType().equals(requiredUserType);
    }
    
    @Override
    public boolean isAdmin(String userId) {
        return hasPermission(userId, 1);
    }
    
    @Override
    public boolean isPublisher(String userId) {
        return hasPermission(userId, 2);
    }
    
    @Override
    public boolean isReceiver(String userId) {
        return hasPermission(userId, 3);
    }
    
    @Override
    public UserInfoResponse getUserInfo(String userId) {
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        return new UserInfoResponse()
                .setUserId(user.getUserId())
                .setUsername(user.getUsername())
                .setNickname(user.getNickname())
                .setAvatar(user.getAvatar())
                .setEmail(user.getEmail())
                .setPhone(user.getPhone())
                .setWechatId(user.getWechatId())
                .setYouzanId(user.getYouzanId())
                .setGender(user.getGender())
                .setBirthday(user.getBirthday())
                .setStatus(user.getStatus())
                .setUserType(user.getUserType())
                .setUserTypeDesc(getUserTypeDesc(user.getUserType()))
                .setProfileId(user.getProfileId())
                .setLastLoginTime(user.getLastLoginTime())
                .setCreatedTime(user.getCreatedTime());
    }
    
    @Override
    public boolean verifyPassword(String username, String password) {
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            return false; // 用户不存在，密码验证失败
        }
        
        return sysUserService.validatePassword(password, user.getPassword());
    }
    
    @Override
    public SysUser getUserInfoEntity(String userId) {
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }
    
    /**
     * 获取用户类型描述
     */
    private String getUserTypeDesc(Integer userType) {
        if (userType == null) {
            return "未知";
        }
        switch (userType) {
            case 1: return "系统管理员";
            case 2: return "发布者";
            case 3: return "接受者";
            default: return "未知";
        }
    }
}