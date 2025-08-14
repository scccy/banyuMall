package com.origin.banyu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.banyu.base.service.BaseService;
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
 * 继承BaseService，异常处理由AOP自动完成
 * 
 * @author scccy
 * @since 2025-08-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    
    private final SysUserMapper sysUserMapper;
    
    @Override
    public SysUser getUserByUsername(String username) {
        log.debug("根据用户名查询用户 - 用户名: {}", username);
        
        if (StringUtils.hasText(username)) {
            return sysUserMapper.selectByUsername(username);
        }
        
        log.warn("用户名为空，无法查询用户");
        return null;
    }
    
    @Override
    public SysUser getUserByPhone(String phone) {
        log.debug("根据手机号查询用户 - 手机号: {}", phone);
        
        if (StringUtils.hasText(phone)) {
            return sysUserMapper.selectByPhone(phone);
        }
        
        log.warn("手机号为空，无法查询用户");
        return null;
    }
    
    @Override
    public SysUser getUserByWechatId(String wechatId) {
        log.debug("根据微信ID查询用户 - 微信ID: {}", wechatId);
        
        if (StringUtils.hasText(wechatId)) {
            return sysUserMapper.selectByWechatId(wechatId);
        }
        
        log.warn("微信ID为空，无法查询用户");
        return null;
    }
    
    @Override
    public SysUser getUserByYouzanId(String youzanId) {
        log.debug("根据有赞ID查询用户 - 有赞ID: {}", youzanId);
        
        if (StringUtils.hasText(youzanId)) {
            return sysUserMapper.selectByYouzanId(youzanId);
        }
        
        log.warn("有赞ID为空，无法查询用户");
        return null;
    }
    
    @Override
    public SysUser getUserByEmail(String email) {
        log.debug("根据邮箱查询用户 - 邮箱: {}", email);
        
        if (StringUtils.hasText(email)) {
            return sysUserMapper.selectByEmail(email);
        }
        
        log.warn("邮箱为空，无法查询用户");
        return null;
    }
}

