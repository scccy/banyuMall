package com.origin.common.util;

import com.origin.common.enums.UserTypeEnum;
import com.origin.common.enums.UserStatusEnum;

/**
 * 用户权限检查工具类
 * 作者: scccy
 * 创建时间: 2025-01-27
 */
public class UserPermissionUtil {
    
    /**
     * 检查用户是否有发布权限
     * 
     * @param userType 用户类型
     * @return 是否有发布权限
     */
    public static boolean canPublish(Integer userType) {
        UserTypeEnum type = UserTypeEnum.getByValue(userType);
        return type != null && type.canPublish();
    }
    
    /**
     * 检查用户是否有接受权限
     * 
     * @param userType 用户类型
     * @return 是否有接受权限
     */
    public static boolean canReceive(Integer userType) {
        UserTypeEnum type = UserTypeEnum.getByValue(userType);
        return type != null && type.canReceive();
    }
    
    /**
     * 检查用户是否为管理员
     * 
     * @param userType 用户类型
     * @return 是否为管理员
     */
    public static boolean isAdmin(Integer userType) {
        UserTypeEnum type = UserTypeEnum.getByValue(userType);
        return type != null && type.isAdmin();
    }
    
    /**
     * 检查用户是否为发布者
     * 
     * @param userType 用户类型
     * @return 是否为发布者
     */
    public static boolean isPublisher(Integer userType) {
        UserTypeEnum type = UserTypeEnum.getByValue(userType);
        return type != null && type.isPublisher();
    }
    
    /**
     * 检查用户是否为接受者
     * 
     * @param userType 用户类型
     * @return 是否为接受者
     */
    public static boolean isReceiver(Integer userType) {
        UserTypeEnum type = UserTypeEnum.getByValue(userType);
        return type != null && type.isReceiver();
    }
    
    /**
     * 检查用户是否可以登录
     * 
     * @param userStatus 用户状态
     * @return 是否可以登录
     */
    public static boolean canLogin(Integer userStatus) {
        UserStatusEnum status = UserStatusEnum.getByValue(userStatus);
        return status != null && status.canLogin();
    }
    
    /**
     * 检查用户是否可以操作
     * 
     * @param userStatus 用户状态
     * @return 是否可以操作
     */
    public static boolean canOperate(Integer userStatus) {
        UserStatusEnum status = UserStatusEnum.getByValue(userStatus);
        return status != null && status.canOperate();
    }
    
    /**
     * 检查用户是否有指定权限
     * 
     * @param userType 用户类型
     * @param userStatus 用户状态
     * @param permission 权限类型：publish, receive, admin
     * @return 是否有权限
     */
    public static boolean hasPermission(Integer userType, Integer userStatus, String permission) {
        // 首先检查用户状态
        if (!canOperate(userStatus)) {
            return false;
        }
        
        // 然后检查具体权限
        switch (permission.toLowerCase()) {
            case "publish":
                return canPublish(userType);
            case "receive":
                return canReceive(userType);
            case "admin":
                return isAdmin(userType);
            default:
                return false;
        }
    }
    
    /**
     * 获取用户类型描述
     * 
     * @param userType 用户类型
     * @return 类型描述
     */
    public static String getUserTypeDescription(Integer userType) {
        UserTypeEnum type = UserTypeEnum.getByValue(userType);
        return type != null ? type.getDescription() : "未知类型";
    }
    
    /**
     * 获取用户状态描述
     * 
     * @param userStatus 用户状态
     * @return 状态描述
     */
    public static String getUserStatusDescription(Integer userStatus) {
        UserStatusEnum status = UserStatusEnum.getByValue(userStatus);
        return status != null ? status.getDescription() : "未知状态";
    }
} 