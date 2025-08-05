package com.origin.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.common.dto.ResultData;
import com.origin.common.exception.BusinessException;
import com.origin.common.entity.ErrorCode;
import com.origin.common.dto.AliyunOssFileUploadRequest;
import com.origin.common.dto.AliyunOssFileUploadResponse;
import com.origin.user.dto.AvatarResponse;
import com.origin.user.dto.UserCreateRequest;
import com.origin.user.dto.UserQueryRequest;
import com.origin.user.dto.UserUpdateRequest;
import com.origin.user.entity.SysUser;
import com.origin.user.feign.OssFileFeignClient;
import com.origin.user.feign.AuthFeignClient;
import com.origin.common.dto.PasswordEncryptRequest;
import com.origin.common.dto.PasswordEncryptResponse;
import com.origin.user.mapper.SysUserMapper;
import com.origin.user.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

/**
 * 系统用户服务实现类（用户服务专用）
 * 基于简化的权限控制，专注于用户管理功能
 * 
 * @author scccy
 * @since 2025-07-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    private final OssFileFeignClient ossFileFeignClient;
    private final AuthFeignClient authFeignClient;
    
    // 常量定义
    private static final int USER_STATUS_NORMAL = 1;
    private static final int USER_STATUS_DELETED = 3;
    private static final int USER_TYPE_ADMIN = 1;
    private static final int USER_TYPE_PUBLISHER = 2;
    private static final int USER_TYPE_RECEIVER = 3;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser createUser(UserCreateRequest request) {
        log.info("创建用户 - 请求参数: {}", request);
        
        // 验证用户创建参数
        validateUserCreateRequest(request);
        
        // 创建用户实体
        SysUser user = buildUserFromRequest(request);
        
        // 加密密码
        encryptUserPassword(user, request.getPassword());
        
        // 设置默认值并保存
        setUserDefaults(user);
        save(user);
        
        log.info("用户创建成功 - 用户ID: {}", user.getUserId());
        return user;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser createUserWithAvatar(UserCreateRequest request, org.springframework.web.multipart.MultipartFile avatarFile) {
        log.info("创建用户（支持头像上传） - 请求参数: {}, 是否有头像: {}", request, avatarFile != null);
        
        // 验证用户创建参数
        validateUserCreateRequest(request);
        
        // 创建用户实体
        SysUser user = buildUserFromRequest(request);
        
        // 加密密码
        encryptUserPassword(user, request.getPassword());
        
        // 设置默认值
        setUserDefaults(user);
        
        // 处理头像上传
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // 先保存用户（不包含头像）
            save(user);
            
            try {
                // 上传头像
                AvatarResponse avatarResponse = uploadAvatar(user.getUserId(), avatarFile);
                // 更新用户头像信息
                user.setAvatar(avatarResponse.getAvatarUrl());
                updateById(user);
                
                log.info("用户创建成功（包含头像） - 用户ID: {}, 头像URL: {}", user.getUserId(), avatarResponse.getAvatarUrl());
            } catch (Exception e) {
                log.error("头像上传失败，但用户创建成功 - 用户ID: {}, 错误: {}", user.getUserId(), e.getMessage());
                // 头像上传失败不影响用户创建，只记录日志
            }
        } else {
            // 没有头像文件，直接保存用户
            save(user);
            log.info("用户创建成功（无头像） - 用户ID: {}", user.getUserId());
        }
        
        return user;
    }
    
    @Override
    public SysUser getUserById(String userId) {
        log.debug("根据用户ID获取用户信息 - 用户ID: {}", userId);
        
        SysUser user = getById(userId);
        if (user == null) {
            log.warn("用户不存在 - 用户ID: {}", userId);
            return null;
        }
        
        return user;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser updateUser(String userId, UserUpdateRequest request) {
        log.info("更新用户信息 - 用户ID: {}, 请求参数: {}", userId, request);
        
        // 检查用户是否存在
        SysUser existingUser = validateUserExists(userId);
        
        // 更新用户信息
        SysUser user = buildUserFromUpdateRequest(request, userId);
        updateById(user);
        
        // 清除缓存
        clearUserCache(userId);
        
        log.info("用户信息更新成功 - 用户ID: {}", userId);
        return getUserById(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser updateUserWithAvatar(String userId, UserUpdateRequest request, org.springframework.web.multipart.MultipartFile avatarFile) {
        log.info("更新用户信息（支持头像上传） - 用户ID: {}, 请求参数: {}, 是否有头像: {}", userId, request, avatarFile != null);
        
        // 检查用户是否存在
        SysUser existingUser = validateUserExists(userId);
        
        // 更新用户信息
        SysUser user = buildUserFromUpdateRequest(request, userId);
        
        // 处理头像上传
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                // 上传头像
                AvatarResponse avatarResponse = uploadAvatar(userId, avatarFile);
                // 更新头像URL
                user.setAvatar(avatarResponse.getAvatarUrl());
                log.info("头像上传成功 - 用户ID: {}, 头像URL: {}", userId, avatarResponse.getAvatarUrl());
            } catch (Exception e) {
                log.error("头像上传失败，但用户信息更新继续 - 用户ID: {}, 错误: {}", userId, e.getMessage());
                // 头像上传失败不影响用户信息更新，只记录日志
            }
        }
        
        // 更新用户信息
        updateById(user);
        
        // 清除缓存
        clearUserCache(userId);
        
        log.info("用户信息更新成功 - 用户ID: {}", userId);
        return getUserById(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(String userId) {
        log.info("删除用户 - 用户ID: {}", userId);
        
        // 检查用户是否存在
        SysUser user = getUserById(userId);
        if (user == null) {
            log.warn("删除用户失败 - 用户不存在: {}", userId);
            return false;
        }
        
        // 软删除
        user.setStatus(USER_STATUS_DELETED);
        user.setUpdatedTime(LocalDateTime.now());
        boolean result = updateById(user);
        
        if (result) {
            // 清除缓存
            clearUserCache(userId);
            log.info("用户删除成功 - 用户ID: {}", userId);
        } else {
            log.warn("用户删除失败 - 数据库更新失败: {}", userId);
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteUsers(List<String> userIds) {
        log.info("批量删除用户 - 用户ID列表: {}", userIds);
        
        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户ID列表不能为空");
        }
        
        int successCount = 0;
        for (String userId : userIds) {
            try {
                if (deleteUser(userId)) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("删除用户失败 - 用户ID: {}, 错误: {}", userId, e.getMessage());
            }
        }
        
        log.info("批量删除用户完成 - 成功删除: {}/{}", successCount, userIds.size());
        return successCount;
    }
    
    @Override
    public IPage<SysUser> getUserPage(UserQueryRequest request) {
        log.debug("分页查询用户列表 - 请求参数: {}", request);
        
        // 构建分页参数
        Page<SysUser> page = new Page<>(request.getCurrent(), request.getSize());
        
        // 构建查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = buildUserQueryWrapper(request);
        
        return page(page, queryWrapper);
    }
    
    @Override
    public SysUser getUserByUsername(String username) {
        return queryUserByField(username, SysUser::getUsername, "用户名");
    }
    
    @Override
    public SysUser getUserByPhone(String phone) {
        return queryUserByField(phone, SysUser::getPhone, "手机号");
    }
    
    @Override
    public SysUser getUserByWechatId(String wechatId) {
        return queryUserByField(wechatId, SysUser::getWechatId, "微信ID");
    }
    
    @Override
    public SysUser getUserByYouzanId(String youzanId) {
        return queryUserByField(youzanId, SysUser::getYouzanId, "有赞ID");
    }
    
    @Override
    public SysUser getUserByEmail(String email) {
        return queryUserByField(email, SysUser::getEmail, "邮箱");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AvatarResponse uploadAvatar(String userId, org.springframework.web.multipart.MultipartFile file) {
        log.info("上传用户头像 - 用户ID: {}, 文件名: {}", userId, file.getOriginalFilename());
        
        // 检查用户是否存在
        SysUser user = validateUserExists(userId);
        
        // 验证文件
        validateAvatarFile(file);
        
        try {
            // 构建OSS上传请求
            AliyunOssFileUploadRequest uploadRequest = buildFileUploadRequest(file, userId, user.getUsername());
            
            // 调用OSS服务上传文件
            ResultData<AliyunOssFileUploadResponse> uploadResult = ossFileFeignClient.uploadFile(uploadRequest);
            
            if (!uploadResult.isSuccess()) {
                throw new BusinessException(ErrorCode.USER_AVATAR_UPLOAD_FAILED, "头像上传失败: " + uploadResult.getMessage());
            }
            
            AliyunOssFileUploadResponse uploadResponse = uploadResult.getData();
            
            // 更新用户头像信息
            updateUserAvatar(userId, uploadResponse.getAccessUrl());
            
            // 构建响应
            AvatarResponse response = buildAvatarResponse(userId, file, uploadResponse.getAccessUrl());
            
            log.info("用户头像上传成功 - 用户ID: {}, 头像URL: {}", userId, uploadResponse.getAccessUrl());
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
        SysUser user = validateUserExists(userId);
        
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
    
    @Override
    public boolean hasPermission(String userId, Integer requiredUserType) {
        SysUser user = getUserById(userId);
        if (user == null || user.getStatus() != USER_STATUS_NORMAL) {
            return false;
        }
        
        // 管理员拥有所有权限
        if (user.getUserType() == USER_TYPE_ADMIN) {
            return true;
        }
        
        // 检查用户类型是否匹配
        return user.getUserType().equals(requiredUserType);
    }
    
    @Override
    public boolean isAdmin(String userId) {
        return hasPermission(userId, USER_TYPE_ADMIN);
    }
    
    @Override
    public boolean isPublisher(String userId) {
        return hasPermission(userId, USER_TYPE_PUBLISHER);
    }
    
    @Override
    public boolean isReceiver(String userId) {
        return hasPermission(userId, USER_TYPE_RECEIVER);
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 验证用户创建请求
     */
    private void validateUserCreateRequest(UserCreateRequest request) {
        // 检查用户名是否已存在
        SysUser existingUser = getUserByUsername(request.getUsername());
        if (existingUser != null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名已存在");
        }
        
        // 检查手机号是否已存在
        SysUser existingPhone = getUserByPhone(request.getPhone());
        if (existingPhone != null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "手机号已存在");
        }
    }
    
    /**
     * 从请求构建用户实体
     */
    private SysUser buildUserFromRequest(UserCreateRequest request) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);
        return user;
    }
    
    /**
     * 从更新请求构建用户实体
     */
    private SysUser buildUserFromUpdateRequest(UserUpdateRequest request, String userId) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);
        user.setUserId(userId);
        user.setUpdatedTime(LocalDateTime.now());
        return user;
    }
    
    /**
     * 加密用户密码
     */
    private void encryptUserPassword(SysUser user, String rawPassword) {
        try {
            PasswordEncryptRequest encryptRequest = new PasswordEncryptRequest()
                    .setUsername(user.getUsername())
                    .setPassword(rawPassword);
            
            ResultData<PasswordEncryptResponse> encryptResult = authFeignClient.encryptPassword(encryptRequest);
            if (encryptResult.isSuccess() && encryptResult.getData() != null) {
                user.setPassword(encryptResult.getData().getEncryptedPassword());
                log.info("密码加密成功 - 用户名: {}", user.getUsername());
            } else {
                log.error("密码加密失败 - 用户名: {}, 错误: {}", user.getUsername(), encryptResult.getMessage());
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "密码加密失败");
            }
        } catch (Exception e) {
            log.error("调用密码加密服务失败 - 用户名: {}, 错误: {}", user.getUsername(), e.getMessage());
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE, "密码加密服务不可用");
        }
    }
    
    /**
     * 设置用户默认值
     */
    private void setUserDefaults(SysUser user) {
        user.setStatus(USER_STATUS_NORMAL);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
    }
    
    /**
     * 验证用户是否存在
     */
    private SysUser validateUserExists(String userId) {
        SysUser user = getUserById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        return user;
    }
    
    /**
     * 通用字段查询方法
     */
    private SysUser queryUserByField(String value, SFunction<SysUser, ?> fieldGetter, String fieldName) {
        log.debug("根据{}查询用户 - {}: {}", fieldName, fieldName, value);
        
        if (!StringUtils.hasText(value)) {
            return null;
        }

        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(fieldGetter, value);

        // 对于用户名和手机号，排除已删除的用户
        if (fieldName.equals("用户名") || fieldName.equals("手机号")) {
            queryWrapper.ne(SysUser::getStatus, USER_STATUS_DELETED);
        }

        return getOne(queryWrapper);
    }
    
    /**
     * 构建用户查询条件
     */
    private LambdaQueryWrapper<SysUser> buildUserQueryWrapper(UserQueryRequest request) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        
        // 用户名模糊查询
        if (StringUtils.hasText(request.getUsername())) {
            queryWrapper.like(SysUser::getUsername, request.getUsername());
        }
        
        // 昵称模糊查询
        if (StringUtils.hasText(request.getNickname())) {
            queryWrapper.like(SysUser::getNickname, request.getNickname());
        }
        
        // 手机号精确查询
        if (StringUtils.hasText(request.getPhone())) {
            queryWrapper.eq(SysUser::getPhone, request.getPhone());
        }
        
        // 邮箱模糊查询
        if (StringUtils.hasText(request.getEmail())) {
            queryWrapper.like(SysUser::getEmail, request.getEmail());
        }
        
        // 用户类型查询
        if (request.getUserType() != null) {
            queryWrapper.eq(SysUser::getUserType, request.getUserType());
        }
        
        // 状态查询
        if (request.getStatus() != null) {
            queryWrapper.eq(SysUser::getStatus, request.getStatus());
        }
        
        // 性别查询
        if (request.getGender() != null) {
            queryWrapper.eq(SysUser::getGender, request.getGender());
        }
        
        // 时间范围查询
        if (StringUtils.hasText(request.getStartTime())) {
            queryWrapper.ge(SysUser::getCreatedTime, request.getStartTime());
        }
        if (StringUtils.hasText(request.getEndTime())) {
            queryWrapper.le(SysUser::getCreatedTime, request.getEndTime());
        }
        
        // 排除已删除用户
        queryWrapper.ne(SysUser::getStatus, USER_STATUS_DELETED);
        
        // 按创建时间倒序
        queryWrapper.orderByDesc(SysUser::getCreatedTime);
        
        return queryWrapper;
    }
    
    /**
     * 验证头像文件
     */
    private void validateAvatarFile(org.springframework.web.multipart.MultipartFile file) {
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
     * 构建文件上传请求
     */
    private AliyunOssFileUploadRequest buildFileUploadRequest(org.springframework.web.multipart.MultipartFile file, String userId, String username) {
        AliyunOssFileUploadRequest uploadRequest = new AliyunOssFileUploadRequest();
        uploadRequest.setFile(file);
        uploadRequest.setSourceService("service-user");
        uploadRequest.setBusinessType("avatar");
        uploadRequest.setFilePath("avatar/" + java.time.LocalDate.now());
        uploadRequest.setUploadUserId(Long.valueOf(userId));
        uploadRequest.setUploadUserName(username);
        return uploadRequest;
    }
    
    /**
     * 更新用户头像
     */
    private void updateUserAvatar(String userId, String avatarUrl) {
        SysUser updateUser = new SysUser();
        updateUser.setUserId(userId);
        updateUser.setAvatar(avatarUrl);
        updateUser.setUpdatedTime(LocalDateTime.now());
        updateById(updateUser);
        
        // 清除缓存
        clearUserCache(userId);
    }
    
    /**
     * 构建头像响应
     */
    private AvatarResponse buildAvatarResponse(String userId, org.springframework.web.multipart.MultipartFile file, String avatarUrl) {
        AvatarResponse response = new AvatarResponse();
        response.setUserId(userId);
        response.setAvatarUrl(avatarUrl);
        response.setFileSize(file.getSize());
        response.setMimeType(file.getContentType());
        response.setOriginalFileName(file.getOriginalFilename());
        return response;
    }
    
    /**
     * 清除用户相关缓存
     */
    private void clearUserCache(String userId) {
        // 清除用户信息缓存
        // 清除用户列表缓存（这里简化处理，实际可能需要更复杂的缓存策略）
        log.debug("清除用户缓存 - 用户ID: {}", userId);
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