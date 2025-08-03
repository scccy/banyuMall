package com.origin.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.origin.auth.entity.SysUser;

/**
 * 系统用户服务接口（认证服务专用）
 * 基于简化的权限控制，专注于认证相关的用户管理
 * 
 * @author scccy
 * @since 2025-01-27
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);
    
    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    SysUser getByPhone(String phone);
    
    /**
     * 根据微信ID查询用户
     * 
     * @param wechatId 微信ID
     * @return 用户信息
     */
    SysUser getByWechatId(String wechatId);
    
    /**
     * 根据有赞ID查询用户
     * 
     * @param youzanId 有赞ID
     * @return 用户信息
     */
    SysUser getByYouzanId(String youzanId);
    
    /**
     * 更新用户最后登录时间
     * 
     * @param userId 用户ID
     */
    void updateLastLoginTime(String userId);
    
    /**
     * 验证用户密码
     * 
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean validatePassword(String rawPassword, String encodedPassword);
    
    /**
     * 加密密码
     * 
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    String encodePassword(String rawPassword);
    
    /**
     * 检查用户状态是否正常
     * 
     * @param user 用户信息
     * @return 是否正常
     */
    boolean isUserStatusNormal(SysUser user);
}