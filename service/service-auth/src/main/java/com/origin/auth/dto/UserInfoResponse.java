package com.origin.auth.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息响应DTO
 * 
 * @author scccy
 * @since 2025-01-27
 */
@Data
@Accessors(chain = true)
public class UserInfoResponse {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 微信用户ID
     */
    private String wechatId;
    
    /**
     * 有赞用户ID
     */
    private String youzanId;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;
    
    /**
     * 生日
     */
    private LocalDate birthday;
    
    /**
     * 状态：1-正常，2-禁用，3-待审核，4-已删除
     */
    private Integer status;
    
    /**
     * 用户类型：1-系统管理员，2-发布者，3-接受者
     */
    private Integer userType;
    
    /**
     * 用户类型描述
     */
    private String userTypeDesc;
    
    /**
     * 扩展信息ID
     */
    private String profileId;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
} 