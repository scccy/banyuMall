package com.origin.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.auth.entity.SysUser;
import com.origin.auth.mapper.SysUserMapper;
import com.origin.auth.service.SysUserService;
import com.origin.common.entity.ErrorCode;
import com.origin.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 系统用户服务实现类（认证服务专用）
 * 基于简化的权限控制，专注于认证相关的用户管理
 * 
 * @author scccy
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return getOne(wrapper);
    }
    
    @Override
    public SysUser getByPhone(String phone) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, phone);
        return getOne(wrapper);
    }
    
    @Override
    public SysUser getByWechatId(String wechatId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getWechatId, wechatId);
        return getOne(wrapper);
    }
    
    @Override
    public SysUser getByYouzanId(String youzanId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getYouzanId, youzanId);
        return getOne(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginTime(String userId) {
        SysUser user = getById(userId);
        if (user != null) {
            user.setLastLoginTime(LocalDateTime.now());
            updateById(user);
            log.info("更新用户最后登录时间，用户ID：{}", userId);
        }
    }
    
    @Override
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    @Override
    public boolean isUserStatusNormal(SysUser user) {
        if (user == null) {
            return false;
        }
        // 状态为1表示正常
        return user.getStatus() != null && user.getStatus() == 1;
    }
}