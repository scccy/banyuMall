package com.origin.banyu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.banyu.common.entity.SysUser;
import com.origin.banyu.user.mapper.SysUserMapper;
import com.origin.banyu.user.service.UserBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户批量操作服务实现类
 * 专注于用户批量操作功能
 * 
 * @author scccy
 * @since 2025-08-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserBatchServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserBatchService {
    
    // 常量定义
    private static final int USER_STATUS_NORMAL = 1;
    private static final int USER_STATUS_DELETED = 3;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteUsers(List<String> userIds) {
        log.info("批量删除用户 - 用户ID列表: {}", userIds);
        
        if (userIds == null || userIds.isEmpty()) {
            log.warn("批量删除用户 - 用户ID列表为空");
            return false;
        }
        
        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysUser::getUserId, userIds)
               .eq(SysUser::getStatus, USER_STATUS_NORMAL);
        
        // 查询存在的用户
        List<SysUser> existingUsers = list(wrapper);
        if (existingUsers.isEmpty()) {
            log.warn("批量删除用户 - 未找到任何有效的用户");
            return false;
        }
        
        // 批量软删除
        for (SysUser user : existingUsers) {
            user.setStatus(USER_STATUS_DELETED);
            user.setUpdatedTime(LocalDateTime.now());
        }
        
        // 批量更新
        boolean result = updateBatchById(existingUsers);
        log.info("批量删除用户成功 - 删除数量: {}", existingUsers.size());
        return result;
    }
    
    @Override
    public List<SysUser> getBatchUserInfo(List<String> userIds) {
        log.info("批量获取用户信息 - 用户ID列表: {}", userIds);
        
        if (userIds == null || userIds.isEmpty()) {
            log.warn("批量获取用户信息 - 用户ID列表为空");
            return List.of();
        }
        
        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysUser::getUserId, userIds)
               .eq(SysUser::getStatus, USER_STATUS_NORMAL)
               .select(
                   SysUser::getUserId,
                   SysUser::getUsername,
                   SysUser::getPhone,
                   SysUser::getEmail,
                   SysUser::getNickname,
                   SysUser::getAvatar,
                   SysUser::getUserType,
                   SysUser::getStatus,
                   SysUser::getGender,
                   SysUser::getCreatedTime,
                   SysUser::getUpdatedTime
               );
        
        // 批量查询用户信息
        List<SysUser> users = list(wrapper);
        log.info("批量获取用户信息成功 - 查询到用户数量: {}", users.size());
        
        return users;
    }
}
