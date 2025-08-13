package com.origin.banyu.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.origin.banyu.user.dto.UserCreateRequest;
import com.origin.banyu.user.dto.UserQueryRequest;
import com.origin.banyu.user.dto.UserUpdateRequest;
import com.origin.banyu.common.entity.SysUser;

/**
 * 系统用户基础服务接口
 * 专注于用户基础CRUD操作和权限验证
 * 
 * @author scccy
 * @since 2025-07-31
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 创建用户
     *
     * @param request 创建请求
     * @return 创建的用户信息
     */
    SysUser createUser(UserCreateRequest request);
    
    /**
     * 创建用户（支持头像上传）
     *
     * @param request 创建请求
     * @param avatarFile 头像文件（可选）
     * @return 创建的用户信息
     */
    SysUser createUserWithAvatar(UserCreateRequest request, org.springframework.web.multipart.MultipartFile avatarFile);
    
    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUser getUserById(String userId);
    
    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    SysUser updateUser(String userId, UserUpdateRequest request);
    
    /**
     * 更新用户信息（支持头像上传）
     *
     * @param userId 用户ID
     * @param request 更新请求
     * @param avatarFile 头像文件（可选）
     * @return 更新后的用户信息
     */
    SysUser updateUserWithAvatar(String userId, UserUpdateRequest request, org.springframework.web.multipart.MultipartFile avatarFile);
    
    /**
     * 删除用户（软删除）
     *
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(String userId);
    
    /**
     * 分页查询用户列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    IPage<SysUser> getUserPage(UserQueryRequest request);
    
    /**
     * 验证用户权限
     *
     * @param userId 用户ID
     * @param requiredUserType 需要的用户类型
     * @return 是否有权限
     */
    boolean hasPermission(String userId, Integer requiredUserType);
    
    /**
     * 验证用户是否为管理员
     *
     * @param userId 用户ID
     * @return 是否为管理员
     */
    boolean isAdmin(String userId);
    
    /**
     * 验证用户是否为发布者
     *
     * @param userId 用户ID
     * @return 是否为发布者
     */
    boolean isPublisher(String userId);
    
    /**
     * 验证用户是否为接受者
     *
     * @param userId 用户ID
     * @return 是否为接受者
     */
    boolean isReceiver(String userId);
} 