package com.origin.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 系统用户实体（共享表，与service-user共同使用）
 * 
 * @author origin
 * @since 2024-07-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {
    
    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 用户名
     */
    @TableField("username")
    private String username;
    
    /**
     * 密码（service-auth管理）
     */
    @TableField("password")
    private String password;
    
    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;
    
    /**
     * 头像
     */
    @TableField("avatar")
    private String avatar;
    
    /**
     * 邮箱
     */
    @TableField("email")
    private String email;
    
    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;
    
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
     * 状态：0-禁用，1-正常，2-待审核，3-已删除
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 用户类型：1-最高权限，2-普通发布者
     */
    @TableField("user_type")
    private Integer userType;
    
    /**
     * 最后登录时间（service-auth管理）
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
}