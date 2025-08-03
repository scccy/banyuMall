package com.origin.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 系统用户实体（用户服务专用）
 * 基于简化的权限控制，使用用户类型字段直接控制权限
 * 
 * @author scccy
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {
    
    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private String userId;
    
    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;
    
    /**
     * 微信用户ID
     */
    @TableField("wechat_id")
    private String wechatId;
    
    /**
     * 有赞用户ID
     */
    @TableField("youzan_id")
    private String youzanId;
    
    /**
     * 用户名
     */
    @TableField("username")
    private String username;
    
    /**
     * 密码（BCrypt加密）
     */
    @TableField("password")
    private String password;
    
    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;
    
    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;
    
    /**
     * 邮箱
     */
    @TableField("email")
    private String email;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    @TableField("gender")
    private Integer gender;
    
    /**
     * 生日
     */
    @TableField("birthday")
    private LocalDate birthday;
    
    /**
     * 状态：1-正常，2-禁用，3-待审核，4-已删除
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 用户类型：1-系统管理员，2-发布者，3-接受者
     */
    @TableField("user_type")
    private Integer userType;
    
    /**
     * 扩展信息ID
     */
    @TableField("profile_id")
    private String profileId;
    
    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
} 