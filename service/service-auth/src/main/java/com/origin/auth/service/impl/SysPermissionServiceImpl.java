package com.origin.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.auth.entity.SysPermission;
import com.origin.auth.mapper.SysPermissionMapper;
import com.origin.auth.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统权限Service实现类
 */
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public List<SysPermission> getPermissionsByUserId(String userId) {
        return sysPermissionMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public List<String> getPermissionCodesByUserId(String userId) {
        List<SysPermission> permissions = getPermissionsByUserId(userId);
        return permissions.stream()
                .map(SysPermission::getCode)
                .collect(Collectors.toList());
    }

    @Override
    public List<SysPermission> getPermissionsByRoleId(String roleId) {
        return sysPermissionMapper.selectPermissionsByRoleId(roleId);
    }
}