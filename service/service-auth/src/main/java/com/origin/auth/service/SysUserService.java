package com.origin.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.origin.auth.dto.LoginRequest;
import com.origin.auth.dto.LoginResponse;
import com.origin.auth.entity.SysUser;

/**
 * 系统用户服务接口
 * 
 * @author origin
 * @since 2024-07-30
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);
}