package com.origin.banyu.user.service;

import com.origin.banyu.common.entity.SysUser;

/**
 * 用户认证服务接口
 * 专注于认证相关的用户查询功能，供认证服务调用
 * 
 * @author scccy
 * @since 2025-08-12
 */
public interface UserAuthService {
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getUserByUsername(String username);
    
    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    SysUser getUserByPhone(String phone);
    
    /**
     * 根据微信ID查询用户
     *
     * @param wechatId 微信ID
     * @return 用户信息
     */
    SysUser getUserByWechatId(String wechatId);
    
    /**
     * 根据有赞ID查询用户
     *
     * @param youzanId 有赞ID
     * @return 用户信息
     */
    SysUser getUserByYouzanId(String youzanId);
    
    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    SysUser getUserByEmail(String email);
}

