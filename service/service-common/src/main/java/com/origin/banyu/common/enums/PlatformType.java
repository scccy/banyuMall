package com.origin.banyu.common.enums;

/**
 * 平台类型枚举
 * 
 * @author scccy
 * @since 2025-08-07
 */
public enum  PlatformType {
    WECHAT_WORK(1, "企业微信"),
    WECHAT_PERSONAL(2, "个人微信"),
    YOUZAN(3, "有赞"),
    DINGTALK(4, "钉钉"),
    FEISHU(5, "飞书");
    
    private final int code;
    private final String name;
    
    PlatformType(int code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public static PlatformType fromCode(int code) {
        for (PlatformType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
} 