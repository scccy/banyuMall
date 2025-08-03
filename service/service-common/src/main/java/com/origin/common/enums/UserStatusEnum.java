package com.origin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 * 作者: scccy
 * 创建时间: 2025-01-27
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum {
    
    /**
     * 正常
     */
    NORMAL(1, "正常"),
    
    /**
     * 禁用
     */
    DISABLED(2, "禁用"),
    
    /**
     * 待审核
     */
    PENDING(3, "待审核"),
    
    /**
     * 已删除
     */
    DELETED(4, "已删除");
    
    /**
     * 状态值
     */
    private final Integer value;
    
    /**
     * 状态描述
     */
    private final String description;
    
    /**
     * 根据值获取枚举
     */
    public static UserStatusEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (UserStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 判断是否为正常状态
     */
    public boolean isNormal() {
        return this == NORMAL;
    }
    
    /**
     * 判断是否为禁用状态
     */
    public boolean isDisabled() {
        return this == DISABLED;
    }
    
    /**
     * 判断是否为待审核状态
     */
    public boolean isPending() {
        return this == PENDING;
    }
    
    /**
     * 判断是否为已删除状态
     */
    public boolean isDeleted() {
        return this == DELETED;
    }
    
    /**
     * 判断是否可以登录
     */
    public boolean canLogin() {
        return this == NORMAL;
    }
    
    /**
     * 判断是否可以操作
     */
    public boolean canOperate() {
        return this == NORMAL;
    }
} 