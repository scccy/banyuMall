package com.origin.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.origin.user.dto.UserUpdateRequest;
import com.origin.user.entity.UserProfile;

/**
 * 用户扩展信息服务接口
 * 
 * @author scccy
 * @since 2024-07-30
 */
public interface UserProfileService extends IService<UserProfile> {
    
    /**
     * 根据用户ID获取扩展信息
     *
     * @param userId 用户ID
     * @return 用户扩展信息
     */
    UserProfile getProfileByUserId(String userId);
    
    /**
     * 创建或更新用户扩展信息
     *
     * @param userId 用户ID
     * @param request 更新请求
     * @return 扩展信息
     */
    UserProfile createOrUpdateProfile(String userId, UserUpdateRequest request);
    
    /**
     * 删除用户扩展信息
     *
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteProfileByUserId(String userId);
} 