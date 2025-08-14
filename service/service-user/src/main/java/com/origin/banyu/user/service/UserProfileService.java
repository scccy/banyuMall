package com.origin.banyu.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.origin.banyu.user.dto.AvatarResponse;
import com.origin.banyu.user.entity.UserProfile;

/**
 * 用户档案服务接口
 * 专注于用户档案管理，包括头像、扩展信息等
 * 
 * @author scccy
 * @since 2025-07-31
 */
public interface UserProfileService extends IService<UserProfile> {
    
    /**
     * 获取用户扩展信息
     *
     * @param profileId 扩展信息ID
     * @return 用户扩展信息
     */
    UserProfile getProfileByProfileId(String profileId);
    
    /**
     * 创建用户扩展信息
     *
     * @param request 创建请求
     * @return 创建的用户扩展信息
     */
    UserProfile createProfile(Object request);
    
    /**
     * 更新用户扩展信息
     *
     * @param profileId 扩展信息ID
     * @param request 更新请求
     * @return 更新后的用户扩展信息
     */
    UserProfile updateProfile(String profileId, Object request);
    
    /**
     * 删除用户扩展信息
     *
     * @param profileId 扩展信息ID
     * @return 是否删除成功
     */
    boolean deleteProfile(String profileId);
    
    /**
     * 上传用户头像
     *
     * @param userId 用户ID
     * @param file 头像文件
     * @return 头像上传结果
     */
    AvatarResponse uploadAvatar(String userId, org.springframework.web.multipart.MultipartFile file);
    
    /**
     * 获取用户头像信息
     *
     * @param userId 用户ID
     * @return 头像信息
     */
    AvatarResponse getAvatarInfo(String userId);
} 