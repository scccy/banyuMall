package com.origin.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.origin.user.dto.UserUpdateRequest;
import com.origin.user.entity.UserProfile;

/**
 * 用户扩展信息服务接口
 * 基于简化的权限控制，通过profile_id进行关联
 * 
 * @author scccy
 * @since 2025-07-31
 */
public interface UserProfileService extends IService<UserProfile> {
    
    /**
     * 根据扩展信息ID获取用户扩展信息
     *
     * @param profileId 扩展信息ID
     * @return 用户扩展信息
     */
    UserProfile getProfileByProfileId(String profileId);
    
    /**
     * 创建用户扩展信息
     *
     * @param request 更新请求
     * @return 扩展信息
     */
    UserProfile createProfile(UserUpdateRequest request);
    
    /**
     * 更新用户扩展信息
     *
     * @param profileId 扩展信息ID
     * @param request 更新请求
     * @return 扩展信息
     */
    UserProfile updateProfile(String profileId, UserUpdateRequest request);
    
    /**
     * 删除用户扩展信息
     *
     * @param profileId 扩展信息ID
     * @return 是否删除成功
     */
    boolean deleteProfile(String profileId);
} 