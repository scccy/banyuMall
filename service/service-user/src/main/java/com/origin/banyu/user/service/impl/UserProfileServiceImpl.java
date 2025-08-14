package com.origin.banyu.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.entity.SysUser;
import com.origin.banyu.common.exception.BusinessException;
import com.origin.banyu.user.dto.AvatarResponse;
import com.origin.banyu.user.dto.UserUpdateRequest;
import com.origin.banyu.user.entity.UserProfile;
import com.origin.banyu.user.feign.OssFileFeignClient;
import com.origin.banyu.user.mapper.UserProfileMapper;
import com.origin.banyu.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * 用户档案服务实现类
 * 专注于用户档案管理，包括头像、扩展信息等
 * 继承BaseService，异常处理由AOP自动完成
 * 
 * @author scccy
 * @since 2025-07-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {
    
    private final OssFileFeignClient ossFileFeignClient;
    
    @Override
    public UserProfile getProfileByProfileId(String profileId) {
        log.debug("根据档案ID获取用户档案 - 档案ID: {}", profileId);
        
        if (StringUtils.hasText(profileId)) {
            return getById(profileId);
        }
        
        log.warn("档案ID为空，无法获取用户档案");
        return null;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserProfile createProfile(Object request) {
        log.info("创建用户档案 - 请求参数: {}", request);
        
        if (request == null) {
            throw new IllegalArgumentException("用户档案请求不能为空");
        }
        
        // 创建用户档案
        UserProfile profile = new UserProfile();
        // 这里需要根据request类型来设置字段
        // 暂时使用默认值
        
        // 设置创建时间
        profile.setCreatedTime(LocalDateTime.now());
        profile.setUpdatedTime(LocalDateTime.now());
        
        // 保存档案
        save(profile);
        
        log.info("用户档案创建成功 - 档案ID: {}", profile.getProfileId());
        return profile;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserProfile updateProfile(String profileId, Object request) {
        log.info("更新用户档案 - 档案ID: {}, 请求参数: {}", profileId, request);
        
        if (!StringUtils.hasText(profileId)) {
            throw new IllegalArgumentException("档案ID不能为空");
        }
        
        // 检查档案是否存在
        UserProfile existingProfile = getProfileByProfileId(profileId);
        if (existingProfile == null) {
            throw new IllegalArgumentException("档案不存在");
        }
        
        // 这里需要根据request类型来更新字段
        // 暂时只更新时间
        
        // 设置更新时间
        existingProfile.setUpdatedTime(LocalDateTime.now());
        
        // 更新档案
        updateById(existingProfile);
        
        log.info("用户档案更新成功 - 档案ID: {}", profileId);
        return existingProfile;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProfile(String profileId) {
        log.info("删除用户档案 - 档案ID: {}", profileId);
        
        if (!StringUtils.hasText(profileId)) {
            throw new IllegalArgumentException("档案ID不能为空");
        }
        
        // 删除档案
        boolean result = removeById(profileId);
        
        if (result) {
            log.info("用户档案删除成功 - 档案ID: {}", profileId);
        } else {
            log.warn("用户档案删除失败 - 档案ID: {}", profileId);
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AvatarResponse uploadAvatar(String userId, MultipartFile file) {
        log.info("上传用户头像 - 用户ID: {}, 文件名: {}", userId, file.getOriginalFilename());
        
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("头像文件不能为空");
        }
        
        // 验证文件大小
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new IllegalArgumentException("头像文件大小不能超过5MB");
        }
        
        // 上传文件到OSS
        ResultData<String> uploadResult = ossFileFeignClient.uploadFile(file);
        
        if (uploadResult.getCode() != 200) {
            throw new RuntimeException("头像上传失败: " + uploadResult.getMessage());
        }
        
        String avatarUrl = uploadResult.getData();
        log.info("用户头像上传成功 - 用户ID: {}, 头像URL: {}", userId, avatarUrl);
        
        // 构建响应
        AvatarResponse response = new AvatarResponse();
        response.setUserId(userId);
        response.setAvatarUrl(avatarUrl);
        
        return response;
    }
    
    @Override
    public AvatarResponse getAvatarInfo(String userId) {
        log.debug("获取用户头像信息 - 用户ID: {}", userId);
        
        // 构建响应
        AvatarResponse response = new AvatarResponse();
        response.setUserId(userId);
        response.setAvatarUrl("/default-avatar.png"); // 默认头像
        
        return response;
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
    
    /**
     * 验证头像文件
     */
    private void validateAvatarFile(MultipartFile file) {
        // 检查文件格式
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImageFile(originalFilename)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不支持的文件格式，仅支持JPG、PNG、GIF格式");
        }
        
        // 检查文件大小（5MB限制）
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new IllegalArgumentException("头像文件大小不能超过5MB");
        }
    }
    
    /**
     * 更新用户头像
     */
    private void updateUserAvatar(String userId, String avatarUrl) {
        SysUser updateUser = new SysUser();
        updateUser.setUserId(userId);
        updateUser.setAvatar(avatarUrl);
        updateUser.setUpdatedTime(LocalDateTime.now());
        // This method is no longer used as the logic is moved to uploadAvatar
    }
    
    /**
     * 构建头像响应
     */
    private AvatarResponse buildAvatarResponse(String userId, MultipartFile file, String avatarUrl) {
        AvatarResponse response = new AvatarResponse();
        response.setUserId(userId);
        response.setAvatarUrl(avatarUrl);
        response.setFileSize(file.getSize());
        response.setMimeType(file.getContentType());
        response.setOriginalFileName(file.getOriginalFilename());
        return response;
    }
    
    /**
     * 检查是否为有效的图片文件
     */
    private boolean isValidImageFile(String filename) {
        if (filename == null) {
            return false;
        }
        
        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith(".jpg") || 
               lowerFilename.endsWith(".jpeg") || 
               lowerFilename.endsWith(".png") || 
               lowerFilename.endsWith(".gif");
    }
} 