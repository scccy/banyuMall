package com.origin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 平台类型枚举
 * 
 * @author scccy
 * @since 2025-08-05
 */
@Getter
@AllArgsConstructor
public enum PlatformType {

    /**
     * 企业微信
     */
    WECHATWORK(1, "企业微信"),

    /**
     * 钉钉
     */
    DINGTALK(2, "钉钉"),

    /**
     * 飞书
     */
    FEISHU(3, "飞书"),

    /**
     * 有赞
     */
    YOUZAN(4, "有赞"),

    /**
     * 其他平台
     */
    OTHER(999999999, "其他");

    /**
     * 平台类型编码
     */
    private final Integer code;

    /**
     * 平台名称
     */
    private final String name;

    /**
     * 根据编码获取平台类型
     * 
     * @param code 平台编码
     * @return 平台类型
     */
    public static PlatformType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PlatformType platformType : values()) {
            if (platformType.getCode().equals(code)) {
                return platformType;
            }
        }
        return OTHER;
    }

    /**
     * 根据名称获取平台类型
     * 
     * @param name 平台名称
     * @return 平台类型
     */
    public static PlatformType getByName(String name) {
        if (name == null) {
            return null;
        }
        for (PlatformType platformType : values()) {
            if (platformType.getName().equals(name)) {
                return platformType;
            }
        }
        return OTHER;
    }

    /**
     * 判断是否为有效的平台类型
     * 
     * @param code 平台编码
     * @return true-有效，false-无效
     */
    public static boolean isValid(Integer code) {
        return getByCode(code) != OTHER;
    }
} 