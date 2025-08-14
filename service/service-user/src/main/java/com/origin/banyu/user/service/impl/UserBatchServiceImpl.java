package com.origin.banyu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.banyu.base.service.BaseService;
import com.origin.banyu.common.entity.SysUser;
import com.origin.banyu.user.mapper.SysUserMapper;
import com.origin.banyu.user.service.UserBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;
import com.origin.banyu.user.mapper.UserProfileMapper;

/**
 * 用户批量操作服务实现类
 * 专注于用户批量操作功能
 * 继承BaseService，异常处理由AOP自动完成
 * 
 * @author scccy
 * @since 2025-08-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserBatchServiceImpl implements UserBatchService {
    
    private final SysUserMapper sysUserMapper;
    private final UserProfileMapper userProfileMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteUsers(List<String> userIds) {
        log.info("批量删除用户 - 用户ID列表: {}", userIds);
        
        if (userIds == null || userIds.isEmpty()) {
            log.warn("批量删除用户 - 用户ID列表为空");
            return false;
        }
        
        // 软删除用户 - 使用现有的updateBatchById方法
        List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
        if (users.isEmpty()) {
            log.warn("批量删除用户 - 未找到任何用户");
            return false;
        }
        
        // 设置删除状态
        for (SysUser user : users) {
            user.setStatus(3); // 3表示删除状态
        }
        
        // 批量更新
        int deletedCount = 0;
        for (SysUser user : users) {
            if (sysUserMapper.updateById(user) > 0) {
                deletedCount++;
            }
        }
        
        log.info("批量删除用户完成 - 成功删除 {} 个用户", deletedCount);
        
        return deletedCount > 0;
    }
    
    @Override
    public List<SysUser> getBatchUserInfo(List<String> userIds) {
        log.info("批量获取用户信息 - 用户ID列表: {}", userIds);
        
        if (userIds == null || userIds.isEmpty()) {
            log.warn("批量获取用户信息 - 用户ID列表为空");
            return Collections.emptyList();
        }
        
        // 批量查询用户信息
        List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
        log.info("批量获取用户信息完成 - 成功获取 {} 个用户信息", users.size());
        
        return users;
    }
}
