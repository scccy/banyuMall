package com.origin.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.common.exception.BusinessException;
import com.origin.common.entity.ErrorCode;
import com.origin.user.dto.UserCreateRequest;
import com.origin.user.dto.UserQueryRequest;
import com.origin.user.dto.UserUpdateRequest;
import com.origin.user.entity.SysUser;
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
        
        // 设置默认值
        user.setStatus(1); // 正常状态
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 保存用户
        save(user);
        
        log.info("用户创建成功 - 用户ID: {}", user.getId());
        return user;
    }
    
    @Override
    @Cacheable(value = "user:info", key = "#userId", unless = "#result == null")
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
    @CacheEvict(value = "user:info", key = "#userId")
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
        user.setUpdateTime(LocalDateTime.now());
        
        // 只更新非空字段
        updateById(user);
        
        // 清除缓存
        clearUserCache(userId);
        
        log.info("用户信息更新成功 - 用户ID: {}", userId);
        return getUserById(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "user:info", key = "#userId")
    public boolean deleteUser(String userId) {
        log.info("删除用户 - 用户ID: {}", userId);
        
        // 检查用户是否存在
        SysUser user = getUserById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户不存在");
        }
        
        // 软删除
        user.setStatus(3); // 已删除状态
        user.setUpdateTime(LocalDateTime.now());
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
    @Cacheable(value = "user:list", key = "#request.hashCode()", unless = "#result == null")
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
            queryWrapper.ge(SysUser::getCreateTime, request.getStartTime());
        }
        if (StringUtils.hasText(request.getEndTime())) {
            queryWrapper.le(SysUser::getCreateTime, request.getEndTime());
        }
        
        // 排序
        queryWrapper.orderByDesc(SysUser::getCreateTime);
        
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
} 