package com.origin.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.common.exception.BusinessException;
import com.origin.common.entity.ErrorCode;
import com.origin.user.dto.UserUpdateRequest;
import com.origin.user.entity.UserProfile;
import com.origin.user.mapper.UserProfileMapper;
import com.origin.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户扩展信息服务实现类
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {
    
    @Override
    @Cacheable(value = "user:profile", key = "#userId", unless = "#result == null")
    public UserProfile getProfileByUserId(String userId) {
        log.debug("根据用户ID获取扩展信息 - 用户ID: {}", userId);
        
        if (!StringUtils.hasText(userId)) {
            return null;
        }
        
        LambdaQueryWrapper<UserProfile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfile::getUserId, userId);
        
        return getOne(queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "user:profile", key = "#userId")
    public UserProfile createOrUpdateProfile(String userId, UserUpdateRequest request) {
        log.info("创建或更新用户扩展信息 - 用户ID: {}, 请求参数: {}", userId, request);
        
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        
        // 检查是否已存在扩展信息
        UserProfile existingProfile = getProfileByUserId(userId);
        
        if (existingProfile != null) {
            // 更新扩展信息
            return updateProfile(existingProfile, request);
        } else {
            // 创建扩展信息
            return createProfile(userId, request);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "user:profile", key = "#userId")
    public boolean deleteProfileByUserId(String userId) {
        log.info("删除用户扩展信息 - 用户ID: {}", userId);
        
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户ID不能为空");
        }
        
        LambdaQueryWrapper<UserProfile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfile::getUserId, userId);
        
        boolean result = remove(queryWrapper);
        
        if (result) {
            log.info("用户扩展信息删除成功 - 用户ID: {}", userId);
        }
        
        return result;
    }
    
    /**
     * 创建用户扩展信息
     *
     * @param userId 用户ID
     * @param request 更新请求
     * @return 扩展信息
     */
    private UserProfile createProfile(String userId, UserUpdateRequest request) {
        log.debug("创建用户扩展信息 - 用户ID: {}", userId);
        
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setCreateTime(LocalDateTime.now());
        profile.setUpdateTime(LocalDateTime.now());
        
        // 复制扩展信息字段
        copyProfileFields(profile, request);
        
        // 保存扩展信息
        save(profile);
        
        log.info("用户扩展信息创建成功 - 用户ID: {}", userId);
        return profile;
    }
    
    /**
     * 更新用户扩展信息
     *
     * @param profile 现有扩展信息
     * @param request 更新请求
     * @return 更新后的扩展信息
     */
    private UserProfile updateProfile(UserProfile profile, UserUpdateRequest request) {
        log.debug("更新用户扩展信息 - 用户ID: {}", profile.getUserId());
        
        // 复制扩展信息字段
        copyProfileFields(profile, request);
        profile.setUpdateTime(LocalDateTime.now());
        
        // 更新扩展信息
        updateById(profile);
        
        log.info("用户扩展信息更新成功 - 用户ID: {}", profile.getUserId());
        return profile;
    }
    
    /**
     * 复制扩展信息字段
     *
     * @param profile 扩展信息实体
     * @param request 更新请求
     */
    private void copyProfileFields(UserProfile profile, UserUpdateRequest request) {
        if (request.getRealName() != null) {
            profile.setRealName(request.getRealName());
        }
        if (request.getCompanyName() != null) {
            profile.setCompanyName(request.getCompanyName());
        }
        if (request.getCompanyAddress() != null) {
            profile.setCompanyAddress(request.getCompanyAddress());
        }
        if (request.getContactPerson() != null) {
            profile.setContactPerson(request.getContactPerson());
        }
        if (request.getContactPhone() != null) {
            profile.setContactPhone(request.getContactPhone());
        }
        if (request.getIndustry() != null) {
            profile.setIndustry(request.getIndustry());
        }
        if (request.getDescription() != null) {
            profile.setDescription(request.getDescription());
        }
    }
} 