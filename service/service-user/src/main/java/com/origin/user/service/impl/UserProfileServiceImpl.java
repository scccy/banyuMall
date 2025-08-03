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
 * 基于简化的权限控制，通过profile_id进行关联
 * 
 * @author scccy
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {
    
    @Override
    public UserProfile getProfileByProfileId(String profileId) {
        log.debug("根据扩展信息ID获取用户扩展信息 - 扩展信息ID: {}", profileId);
        
        if (!StringUtils.hasText(profileId)) {
            return null;
        }
        
        return getById(profileId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserProfile createProfile(UserUpdateRequest request) {
        log.info("创建用户扩展信息 - 请求参数: {}", request);
        
        UserProfile profile = new UserProfile();
        copyProfileFields(profile, request);
        
        // 设置创建时间
        profile.setCreatedTime(LocalDateTime.now());
        profile.setUpdatedTime(LocalDateTime.now());
        
        // 保存扩展信息
        save(profile);
        
        log.info("用户扩展信息创建成功 - 扩展信息ID: {}", profile.getProfileId());
        return profile;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserProfile updateProfile(String profileId, UserUpdateRequest request) {
        log.info("更新用户扩展信息 - 扩展信息ID: {}, 请求参数: {}", profileId, request);
        
        if (!StringUtils.hasText(profileId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "扩展信息ID不能为空");
        }
        
        // 检查扩展信息是否存在
        UserProfile existingProfile = getProfileByProfileId(profileId);
        if (existingProfile == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "扩展信息不存在");
        }
        
        // 更新扩展信息
        copyProfileFields(existingProfile, request);
        existingProfile.setUpdatedTime(LocalDateTime.now());
        
        // 保存更新
        updateById(existingProfile);
        
        log.info("用户扩展信息更新成功 - 扩展信息ID: {}", profileId);
        return existingProfile;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProfile(String profileId) {
        log.info("删除用户扩展信息 - 扩展信息ID: {}", profileId);
        
        if (!StringUtils.hasText(profileId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "扩展信息ID不能为空");
        }
        
        boolean result = removeById(profileId);
        
        if (result) {
            log.info("用户扩展信息删除成功 - 扩展信息ID: {}", profileId);
        }
        
        return result;
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