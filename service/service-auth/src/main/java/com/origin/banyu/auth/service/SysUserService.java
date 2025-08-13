package com.origin.banyu.auth.service;

import com.origin.banyu.common.entity.SysUser;

/**
 * 系统用户服务接口（认证服务使用的抽象）
 * 重构后，内部通过 Feign 访问 user 服务获取数据。
 */
public interface SysUserService {

    SysUser getByUsername(String username);

    SysUser getById(String userId);

    void updateLastLoginTime(String userId);

    boolean validatePassword(String rawPassword, String encodedPassword);

    String encodePassword(String rawPassword);

    boolean isUserStatusNormal(SysUser user);
}