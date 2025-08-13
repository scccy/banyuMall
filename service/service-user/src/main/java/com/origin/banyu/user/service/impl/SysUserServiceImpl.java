package com.origin.banyu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.banyu.common.exception.BusinessException;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.user.dto.UserCreateRequest;
import com.origin.banyu.user.dto.UserQueryRequest;
import com.origin.banyu.user.dto.UserUpdateRequest;
import com.origin.banyu.common.entity.SysUser;
import com.origin.banyu.user.feign.OssFileFeignClient;
import com.origin.banyu.user.feign.AuthFeignClient;
import org.springframework.util.DigestUtils;
import com.origin.banyu.user.mapper.SysUserMapper;
import com.origin.banyu.user.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 系统用户基础服务实现类
 * 专注于用户基础CRUD操作和权限验证
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
    public SysUser createUserWithAvatar(UserCreateRequest request, MultipartFile avatarFile) {
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
            
            // 上传头像到OSS
            try {
                String avatarUrl = ossFileFeignClient.uploadFile(avatarFile).getData();
                user.setAvatar(avatarUrl);
                updateById(user);
                log.info("用户头像上传成功 - 用户ID: {}, 头像URL: {}", user.getUserId(), avatarUrl);
            } catch (Exception e) {
                log.error("用户头像上传失败 - 用户ID: {}", user.getUserId(), e);
                // 头像上传失败不影响用户创建，记录日志即可
            }
        } else {
            save(user);
        }
        
        log.info("用户创建成功（支持头像上传） - 用户ID: {}", user.getUserId());
        return user;
    }
    
    @Override
    public SysUser getUserById(String userId) {
        log.debug("根据用户ID获取用户信息 - 用户ID: {}", userId);
        
        if (StringUtils.hasText(userId)) {
            return getById(userId);
        }
        
        log.warn("用户ID为空，无法获取用户信息");
        return null;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser updateUser(String userId, UserUpdateRequest request) {
        log.info("更新用户信息 - 用户ID: {}, 请求参数: {}", userId, request);
        
        // 验证用户是否存在
        SysUser existingUser = validateUserExists(userId);
        
        // 构建更新后的用户信息
        SysUser updatedUser = buildUserFromUpdateRequest(request, userId);
        
        // 更新用户信息
        updateById(updatedUser);
        
        log.info("用户信息更新成功 - 用户ID: {}", userId);
        return updatedUser;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser updateUserWithAvatar(String userId, UserUpdateRequest request, MultipartFile avatarFile) {
        log.info("更新用户信息（支持头像上传） - 用户ID: {}, 请求参数: {}, 是否有头像: {}", userId, request, avatarFile != null);
        
        // 验证用户是否存在
        SysUser existingUser = validateUserExists(userId);
        
        // 构建更新后的用户信息
        SysUser updatedUser = buildUserFromUpdateRequest(request, userId);
        
        // 处理头像上传
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String avatarUrl = ossFileFeignClient.uploadFile(avatarFile).getData();
                updatedUser.setAvatar(avatarUrl);
                log.info("用户头像上传成功 - 用户ID: {}, 头像URL: {}", userId, avatarUrl);
            } catch (Exception e) {
                log.error("用户头像上传失败 - 用户ID: {}", userId, e);
                throw new BusinessException(ErrorCode.USER_AVATAR_UPLOAD_FAILED, "头像上传失败");
            }
        }
        
        // 更新用户信息
        updateById(updatedUser);
        
        log.info("用户信息更新成功（支持头像上传） - 用户ID: {}", userId);
        return updatedUser;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(String userId) {
        log.info("删除用户 - 用户ID: {}", userId);
        
        // 验证用户是否存在
        SysUser existingUser = validateUserExists(userId);
        
        // 软删除用户
        existingUser.setStatus(USER_STATUS_DELETED);
        existingUser.setUpdatedTime(LocalDateTime.now());
        
        boolean result = updateById(existingUser);
        
        if (result) {
            log.info("用户删除成功 - 用户ID: {}", userId);
        } else {
            log.error("用户删除失败 - 用户ID: {}", userId);
        }
        
        return result;
    }
    
    @Override
    public IPage<SysUser> getUserPage(UserQueryRequest request) {
        log.debug("分页查询用户列表 - 查询条件: {}", request);
        
        Page<SysUser> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<SysUser> queryWrapper = buildUserQueryWrapper(request);
        
        return page(page, queryWrapper);
    }
    
    @Override
    public boolean hasPermission(String userId, Integer requiredUserType) {
        log.debug("验证用户权限 - 用户ID: {}, 需要的用户类型: {}", userId, requiredUserType);
        
        SysUser user = getUserById(userId);
        if (user == null) {
            return false;
        }
        
        // 管理员拥有所有权限
        if (Objects.equals(USER_TYPE_ADMIN, user.getUserType())) {
            return true;
        }
        
        // 检查具体权限
        return Objects.equals(requiredUserType, user.getUserType());
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
    
    // 私有辅助方法
    
    private void validateUserCreateRequest(UserCreateRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户创建请求不能为空");
        }
        
        if (!StringUtils.hasText(request.getUsername())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户名不能为空");
        }
        
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "密码不能为空");
        }
        
        if (request.getUserType() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户类型不能为空");
        }
    }
    
    private SysUser buildUserFromRequest(UserCreateRequest request) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);
        
        // 设置用户ID
        user.setUserId(generateUserId());
        
        return user;
    }
    
    private SysUser buildUserFromUpdateRequest(UserUpdateRequest request, String userId) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);
        user.setUserId(userId);
        
        return user;
    }
    
    private void encryptUserPassword(SysUser user, String rawPassword) {
        if (StringUtils.hasText(rawPassword)) {
            String encryptedPassword = DigestUtils.md5DigestAsHex(rawPassword.getBytes());
            user.setPassword(encryptedPassword);
        }
    }
    
    private void setUserDefaults(SysUser user) {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedTime(now);
        user.setUpdatedTime(now);
        user.setStatus(USER_STATUS_NORMAL);
    }
    
    private SysUser validateUserExists(String userId) {
        SysUser user = getUserById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        return user;
    }
    
    private LambdaQueryWrapper<SysUser> buildUserQueryWrapper(UserQueryRequest request) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        
        // 状态过滤
        queryWrapper.eq(SysUser::getStatus, USER_STATUS_NORMAL);
        
        // 用户名模糊查询
        if (StringUtils.hasText(request.getUsername())) {
            queryWrapper.like(SysUser::getUsername, request.getUsername());
        }
        
        // 用户类型过滤
        if (request.getUserType() != null) {
            queryWrapper.eq(SysUser::getUserType, request.getUserType());
        }
        
        // 手机号模糊查询
        if (StringUtils.hasText(request.getPhone())) {
            queryWrapper.like(SysUser::getPhone, request.getPhone());
        }
        
        // 邮箱模糊查询
        if (StringUtils.hasText(request.getEmail())) {
            queryWrapper.like(SysUser::getEmail, request.getEmail());
        }
        
        // 创建时间范围查询
        if (StringUtils.hasText(request.getStartTime())) {
            queryWrapper.ge(SysUser::getCreatedTime, request.getStartTime());
        }
        if (StringUtils.hasText(request.getEndTime())) {
            queryWrapper.le(SysUser::getCreatedTime, request.getEndTime());
        }
        
        // 排序
        queryWrapper.orderByDesc(SysUser::getCreatedTime);
        
        return queryWrapper;
    }
    
    private String generateUserId() {
        return "U" + System.currentTimeMillis() + String.valueOf((int)(Math.random() * 1000));
    }
} 