package com.origin.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.origin.auth.entity.SysPermission;

import java.util.List;

/**
 * 系统权限Service接口
 */
public interface SysPermissionService extends IService<SysPermission> {

    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<SysPermission> getPermissionsByUserId(String userId);

    /**
     * 根据用户ID查询权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> getPermissionCodesByUserId(String userId);

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<SysPermission> getPermissionsByRoleId(String roleId);
}