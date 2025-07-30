package com.origin.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.origin.auth.entity.SysRole;

import java.util.List;

/**
 * 系统角色服务接口
 * 
 * @author origin
 * @since 2024-07-30
 */
public interface SysRoleService extends IService<SysRole> {
    
    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> getRolesByUserId(String userId);
    
    /**
     * 根据用户ID查询角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> getRoleCodesByUserId(String userId);
}