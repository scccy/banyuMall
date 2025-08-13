package com.origin.banyu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.banyu.common.exception.BusinessException;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.user.dto.AvatarResponse;
import com.origin.banyu.user.dto.UserUpdateRequest;
import com.origin.banyu.user.entity.UserProfile;
import com.origin.banyu.user.feign.OssFileFeignClient;
import com.origin.banyu.user.mapper.UserProfileMapper;
import com.origin.banyu.user.service.UserProfileService;
import com.origin.banyu.user.service.SysUserService;
import com.origin.banyu.common.entity.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * 用户档案服务实现类
 * 专注于用户档案管理，包括头像、扩展信息等
 * 
 * @author scccy
 * @since 2025-07-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {
    
    private final OssFileFeignClient ossFileFeignClient;
    private final SysUserService sysUserService;
    
    // 常量定义
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
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
    public UserProfile createProfile(Object request) {
        log.info("创建用户扩展信息 - 请求参数: {}", request);
        
        UserProfile profile = new UserProfile();
        if (request instanceof UserUpdateRequest) {
            copyProfileFields(profile, (UserUpdateRequest) request);
        }
        
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
    public UserProfile updateProfile(String profileId, Object request) {
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
        if (request instanceof UserUpdateRequest) {
            copyProfileFields(existingProfile, (UserUpdateRequest) request);
        }
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
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AvatarResponse uploadAvatar(String userId, MultipartFile file) {
        log.info("上传用户头像 - 用户ID: {}, 文件名: {}", userId, file.getOriginalFilename());
        
        // 检查用户是否存在
        SysUser user = sysUserService.getUserById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 验证文件
        validateAvatarFile(file);
        
        try {
            // 上传文件到OSS
            String avatarUrl = ossFileFeignClient.uploadFile(file).getData();
            
            // 更新用户头像信息
            updateUserAvatar(userId, avatarUrl);
            
            // 构建响应
            AvatarResponse response = buildAvatarResponse(userId, file, avatarUrl);
            log.info("用户头像上传成功 - 用户ID: {}, 头像URL: {}", userId, avatarUrl);
            return response;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("头像上传异常 - 用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "头像上传失败，请稍后重试");
        }
    }
    
    @Override
    public AvatarResponse getAvatarInfo(String userId) {
        log.debug("获取用户头像信息 - 用户ID: {}", userId);
        
        // 检查用户是否存在
        SysUser user = sysUserService.getUserById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 构建响应
        AvatarResponse response = new AvatarResponse();
        response.setUserId(userId);
        response.setAvatarUrl(user.getAvatar());
        
        // 如果没有头像，返回默认头像
        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            response.setAvatarUrl("/default-avatar.png"); // 默认头像URL
        }
        
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
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件大小不能超过5MB");
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
        sysUserService.updateById(updateUser);
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