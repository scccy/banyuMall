package com.origin.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.common.dto.ResultData;
import com.origin.user.entity.UserProfile;
import com.origin.user.feign.AuthFeignClient;
import com.origin.user.mapper.UserProfileMapper;
import com.origin.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户扩展信息服务实现类
 *
 * @author origin
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {

    private final AuthFeignClient authFeignClient;

    @Override
    public UserProfile getByUserId(String userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrUpdateProfile(String userId, UserProfile userProfile) {
        // 验证用户是否存在
        try {
            ResultData<Boolean> existsResult = authFeignClient.checkUserExists(userId, "Bearer token");
            if (!existsResult.isSuccess() || !Boolean.TRUE.equals(existsResult.getData(Boolean.class))) {
                log.error("用户不存在，用户ID: {}", userId);
                return false;
            }
        } catch (Exception e) {
            log.error("调用认证服务验证用户失败，用户ID: {}", userId, e);
            // 在开发环境可以继续，生产环境需要根据业务需求决定
        }
        
        // 设置用户ID
        userProfile.setUserId(userId);
        
        // 查询是否已存在
        UserProfile existingProfile = getByUserId(userId);
        
        if (existingProfile != null) {
            // 更新
            userProfile.setId(existingProfile.getId());
            return updateById(userProfile);
        } else {
            // 创建
            return save(userProfile);
        }
    }
} 