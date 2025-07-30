package com.origin.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.auth.entity.SysRole;
import com.origin.auth.mapper.SysRoleMapper;
import com.origin.auth.service.SysRoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统角色服务实现类
 * 
 * @author origin
 * @since 2024-07-30
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    
    @Override
    public List<SysRole> getRolesByUserId(String userId) {
        return baseMapper.selectRolesByUserId(userId);
    }
    
    @Override
    public List<String> getRoleCodesByUserId(String userId) {
        List<SysRole> roles = getRolesByUserId(userId);
        return roles.stream()
                .map(SysRole::getCode)
                .collect(Collectors.toList());
    }
}