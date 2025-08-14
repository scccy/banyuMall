package com.origin.banyu.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 企业微信用户信息DTO
 * @author scccy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WechatWorkUserInfo {
    /** 企业微信用户ID（兼容旧字段名） */
    private String userid;
    /** 企业微信用户ID（新字段名，符合命名规范） */
    private String wechatworkUserId;
    /** 用户姓名 */
    private String name;
    /** 手机号 */
    private String mobile;
    /** 部门ID列表 */
    private List<Integer> department;
    /** 职位 */
    private String position;
    /** 性别：0-未知，1-男，2-女 */
    private Integer gender;
    /** 邮箱 */
    private String email;
    /** 头像URL */
    private String avatar;
    /** 状态：1-已激活，2-已禁用，4-未激活，5-退出企业 */
    private Integer status;
    /** 是否启用：1-启用，0-禁用 */
    private Integer enable;
    /** 是否部门领导：1-是，0-否 */
    private Integer isleader;
    /** 获取企业微信用户ID（优先使用新字段名） */
    public String getWechatworkUserId() {
        return wechatworkUserId != null ? wechatworkUserId : userid;
    }
    /** 设置企业微信用户ID（同时设置新旧字段） */
    public void setWechatworkUserId(String wechatworkUserId) {
        this.wechatworkUserId = wechatworkUserId;
        this.userid = wechatworkUserId; // 保持向后兼容
    }
}

