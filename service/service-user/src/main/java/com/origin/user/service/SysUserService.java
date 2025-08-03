package com.origin.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.origin.user.dto.AvatarResponse;
import com.origin.user.dto.UserCreateRequest;
import com.origin.user.dto.UserQueryRequest;
import com.origin.user.dto.UserUpdateRequest;
import com.origin.user.entity.SysUser;

import java.util.List;

/**
 * 系统用户服务接口（用户服务专用）
 * 基于简化的权限控制，专注于用户管理功能
 * 
 * @author scccy
 * @since 2025-01-27
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
     * 批量删除用户（软删除）
     *
     * @param userIds 用户ID列表
     * @return 删除成功的数量
     */
    int batchDeleteUsers(List<String> userIds);
    
    /**
     * 分页查询用户列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    IPage<SysUser> getUserPage(UserQueryRequest request);
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getUserByUsername(String username);
    
    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    SysUser getUserByPhone(String phone);
    
    /**
     * 根据微信ID查询用户
     *
     * @param wechatId 微信ID
     * @return 用户信息
     */
    SysUser getUserByWechatId(String wechatId);
    
    /**
     * 根据有赞ID查询用户
     *
     * @param youzanId 有赞ID
     * @return 用户信息
     */
    SysUser getUserByYouzanId(String youzanId);
    
    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    SysUser getUserByEmail(String email);
    
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