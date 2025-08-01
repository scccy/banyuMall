package com.origin.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.common.dto.ResultData;
import com.origin.common.exception.BusinessException;
import com.origin.common.entity.ErrorCode;
import com.origin.oss.dto.FileUploadRequest;
import com.origin.oss.dto.FileUploadResponse;
import com.origin.user.dto.AvatarResponse;
import com.origin.user.dto.UserCreateRequest;
import com.origin.user.dto.UserQueryRequest;
import com.origin.user.dto.UserUpdateRequest;
import com.origin.user.entity.SysUser;
import com.origin.user.feign.OssFileFeignClient;
import com.origin.user.feign.AuthFeignClient;
import com.origin.common.dto.PasswordEncryptRequest;
import com.origin.common.dto.PasswordEncryptResponse;
import com.origin.common.dto.ResultData;
import com.origin.user.mapper.SysUserMapper;
import com.origin.user.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统用户服务实现类
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    private final OssFileFeignClient ossFileFeignClient;
    private final AuthFeignClient authFeignClient;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser createUser(UserCreateRequest request) {
        log.info("创建用户 - 请求参数: {}", request);
        
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
        
        // 创建用户实体
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);
        
        // 通过认证模块加密密码
        try {
            PasswordEncryptRequest encryptRequest = new PasswordEncryptRequest()
                    .setUsername(request.getUsername())
                    .setPassword(request.getPassword());
            
            ResultData<PasswordEncryptResponse> encryptResult = authFeignClient.encryptPassword(encryptRequest);
            if (encryptResult.isSuccess() && encryptResult.getData() != null) {
                user.setPassword(encryptResult.getData().getEncryptedPassword());
                log.info("密码加密成功 - 用户名: {}", request.getUsername());
            } else {
                log.error("密码加密失败 - 用户名: {}, 错误: {}", request.getUsername(), encryptResult.getMessage());
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "密码加密失败");
            }
        } catch (Exception e) {
            log.error("调用密码加密服务失败 - 用户名: {}, 错误: {}", request.getUsername(), e.getMessage());
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE, "密码加密服务不可用");
        }
        
        // 设置默认值
        user.setStatus(1); // 正常状态
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        
        // 保存用户
        save(user);
        
        log.info("用户创建成功 - 用户ID: {}", user.getId());
        return user;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser createUserWithAvatar(UserCreateRequest request, org.springframework.web.multipart.MultipartFile avatarFile) {
        log.info("创建用户（支持头像上传） - 请求参数: {}, 是否有头像: {}", request, avatarFile != null);
        
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
        
        // 创建用户实体
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);
        
        // 通过认证模块加密密码
        try {
            PasswordEncryptRequest encryptRequest = new PasswordEncryptRequest()
                    .setUsername(request.getUsername())
                    .setPassword(request.getPassword());
            
            ResultData<PasswordEncryptResponse> encryptResult = authFeignClient.encryptPassword(encryptRequest);
            if (encryptResult.isSuccess() && encryptResult.getData() != null) {
                user.setPassword(encryptResult.getData().getEncryptedPassword());
                log.info("密码加密成功 - 用户名: {}", request.getUsername());
            } else {
                log.error("密码加密失败 - 用户名: {}, 错误: {}", request.getUsername(), encryptResult.getMessage());
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "密码加密失败");
            }
        } catch (Exception e) {
            log.error("调用密码加密服务失败 - 用户名: {}, 错误: {}", request.getUsername(), e.getMessage());
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE, "密码加密服务不可用");
        }
        
        // 设置默认值
        user.setStatus(1); // 正常状态
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        
        // 如果有头像文件，先保存用户获取用户ID，然后上传头像
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // 先保存用户（不包含头像）
            save(user);
            
            try {
                // 上传头像
                AvatarResponse avatarResponse = uploadAvatar(user.getId(), avatarFile);
                // 更新用户头像信息
                user.setAvatar(avatarResponse.getAvatarUrl());
                updateById(user);
                
                log.info("用户创建成功（包含头像） - 用户ID: {}, 头像URL: {}", user.getId(), avatarResponse.getAvatarUrl());
            } catch (Exception e) {
                log.error("头像上传失败，但用户创建成功 - 用户ID: {}, 错误: {}", user.getId(), e.getMessage());
                // 头像上传失败不影响用户创建，只记录日志
            }
        } else {
            // 没有头像文件，直接保存用户
            save(user);
            log.info("用户创建成功（无头像） - 用户ID: {}", user.getId());
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
        SysUser existingUser = getUserById(userId);
        if (existingUser == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户不存在");
        }
        
        // 更新用户信息
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);
        user.setId(userId);
        user.setUpdatedTime(LocalDateTime.now());
        
        // 只更新非空字段
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
        SysUser existingUser = getUserById(userId);
        if (existingUser == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户不存在");
        }
        
        // 更新用户信息
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);
        user.setId(userId);
        user.setUpdatedTime(LocalDateTime.now());
        
        // 如果有头像文件，先上传头像
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
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户不存在");
        }
        
        // 软删除
        user.setStatus(3); // 已删除状态
        user.setUpdatedTime(LocalDateTime.now());
        boolean result = updateById(user);
        
        if (result) {
            // 清除缓存
            clearUserCache(userId);
            log.info("用户删除成功 - 用户ID: {}", userId);
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
        
        // 排序
        queryWrapper.orderByDesc(SysUser::getCreatedTime);
        
        // 执行查询
        return page(page, queryWrapper);
    }
    
    @Override
    public SysUser getUserByUsername(String username) {
        log.debug("根据用户名查询用户 - 用户名: {}", username);
        
        if (!StringUtils.hasText(username)) {
            return null;
        }
        
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        queryWrapper.ne(SysUser::getStatus, 3); // 排除已删除的用户
        
        return getOne(queryWrapper);
    }
    
    @Override
    public SysUser getUserByPhone(String phone) {
        log.debug("根据手机号查询用户 - 手机号: {}", phone);
        
        if (!StringUtils.hasText(phone)) {
            return null;
        }
        
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhone, phone);
        queryWrapper.ne(SysUser::getStatus, 3); // 排除已删除的用户
        
        return getOne(queryWrapper);
    }
    
    /**
     * 清除用户相关缓存
     *
     * @param userId 用户ID
     */
    private void clearUserCache(String userId) {
        // 清除用户信息缓存
        // 清除用户列表缓存（这里简化处理，实际可能需要更复杂的缓存策略）
        log.debug("清除用户缓存 - 用户ID: {}", userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AvatarResponse uploadAvatar(String userId, org.springframework.web.multipart.MultipartFile file) {
        log.info("上传用户头像 - 用户ID: {}, 文件名: {}", userId, file.getOriginalFilename());
        
        // 检查用户是否存在
        SysUser user = getUserById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户不存在");
        }
        
        // 检查文件格式
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImageFile(originalFilename)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不支持的文件格式，仅支持JPG、PNG、GIF格式");
        }
        
        // 检查文件大小（5MB限制）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件大小不能超过5MB");
        }
        
        try {
            // 构建OSS上传请求
            FileUploadRequest uploadRequest = new FileUploadRequest();
            uploadRequest.setFile(file);
            uploadRequest.setSourceService("service-user");
            uploadRequest.setBusinessType("avatar");
            uploadRequest.setFilePath("avatar/" + java.time.LocalDate.now());
            uploadRequest.setUploadUserId(Long.valueOf(userId));
            uploadRequest.setUploadUserName(user.getUsername());
            
            // 调用OSS服务上传文件
            ResultData<FileUploadResponse> uploadResult = ossFileFeignClient.uploadFile(uploadRequest);
            
            if (!uploadResult.isSuccess()) {
                throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, "头像上传失败: " + uploadResult.getMessage());
            }
            
            FileUploadResponse uploadResponse = uploadResult.getData();
            
            // 更新用户头像信息
            SysUser updateUser = new SysUser();
            updateUser.setId(userId);
            updateUser.setAvatar(uploadResponse.getAccessUrl());
            updateUser.setUpdatedTime(LocalDateTime.now());
            updateById(updateUser);
            
            // 清除缓存
            clearUserCache(userId);
            
            // 构建响应
            AvatarResponse response = new AvatarResponse();
            response.setUserId(userId);
            response.setAvatarUrl(uploadResponse.getAccessUrl());
            response.setFileSize(file.getSize());
            response.setMimeType(file.getContentType());
            response.setOriginalFileName(originalFilename);
            
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
        SysUser user = getUserById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户不存在");
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
     * 检查是否为有效的图片文件
     *
     * @param filename 文件名
     * @return 是否为有效图片
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