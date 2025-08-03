package com.origin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户类型枚举
 * 作者: scccy
 * 创建时间: 2025-01-27
 */
@Getter
@AllArgsConstructor
public enum UserTypeEnum {
    
    /**
     * 系统管理员（最高权限）
     */
    ADMIN(1, "系统管理员"),
    
    /**
     * 发布者（可以发布任务）
     */
    PUBLISHER(2, "发布者"),
    
    /**
     * 接受者（可以接受任务）
     */
    RECEIVER(3, "接受者");
    
    /**
     * 类型值
     */
    private final Integer value;
    
    /**
     * 类型描述
     */
    private final String description;
    
    /**
     * 根据值获取枚举
     */
    public static UserTypeEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (UserTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 判断是否为管理员
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    /**
     * 判断是否为发布者
     */
    public boolean isPublisher() {
        return this == PUBLISHER;
    }
    
    /**
     * 判断是否为接受者
     */
    public boolean isReceiver() {
        return this == RECEIVER;
    }
    
    /**
     * 判断是否有发布权限
     */
    public boolean canPublish() {
        return this == ADMIN || this == PUBLISHER;
    }
    
    /**
     * 判断是否有接受权限
     */
    public boolean canReceive() {
        return this == ADMIN || this == RECEIVER;
    }
} 