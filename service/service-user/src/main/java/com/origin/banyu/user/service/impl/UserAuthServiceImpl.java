package com.origin.banyu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.banyu.common.entity.SysUser;
import com.origin.banyu.user.mapper.SysUserMapper;
import com.origin.banyu.user.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户认证服务实现类
 * 专注于认证相关的用户查询功能，供认证服务调用
 * 
 * @author scccy
 * @since 2025-08-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserAuthService {
    
    // 常量定义
    private static final int USER_STATUS_NORMAL = 1;
    
    @Override
    public SysUser getUserByUsername(String username) {
        log.debug("根据用户名查询用户 - 用户名: {}", username);
        
        if (!StringUtils.hasText(username)) {
            return null;
        }
        
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username)
                   .eq(SysUser::getStatus, USER_STATUS_NORMAL);
        
        return getOne(queryWrapper);
    }
    
    @Override
    public SysUser getUserByPhone(String phone) {
        log.debug("根据手机号查询用户 - 手机号: {}", phone);
        
        if (!StringUtils.hasText(phone)) {
            return null;
        }
        
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhone, phone)
                   .eq(SysUser::getStatus, USER_STATUS_NORMAL);
        
        return getOne(queryWrapper);
    }
    
    @Override
    public SysUser getUserByWechatId(String wechatId) {
        log.debug("根据微信ID查询用户 - 微信ID: {}", wechatId);
        
        if (!StringUtils.hasText(wechatId)) {
            return null;
        }
        
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getWechatId, wechatId)
                   .eq(SysUser::getStatus, USER_STATUS_NORMAL);
        
        return getOne(queryWrapper);
    }
    
    @Override
    public SysUser getUserByYouzanId(String youzanId) {
        log.debug("根据有赞ID查询用户 - 有赞ID: {}", youzanId);
        
        if (!StringUtils.hasText(youzanId)) {
            return null;
        }
        
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getYouzanId, youzanId)
                   .eq(SysUser::getStatus, USER_STATUS_NORMAL);
        
        return getOne(queryWrapper);
    }
    
    @Override
    public SysUser getUserByEmail(String email) {
        log.debug("根据邮箱查询用户 - 邮箱: {}", email);
        
        if (!StringUtils.hasText(email)) {
            return null;
        }
        
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getEmail, email)
                   .eq(SysUser::getStatus, USER_STATUS_NORMAL);
        
        return getOne(queryWrapper);
    }
}

