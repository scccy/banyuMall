package com.origin.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户扩展信息实体
 * 基于简化的权限控制，与sys_user通过profile_id关联
 * 
 * @author scccy
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("user_profile")
public class UserProfile extends BaseEntity {
    
    /**
     * 扩展信息ID
     */
    @TableId(value = "profile_id", type = IdType.ASSIGN_ID)
    private String profileId;
    
    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;
    
    /**
     * 公司名称
     */
    @TableField("company_name")
    private String companyName;
    
    /**
     * 公司地址
     */
    @TableField("company_address")
    private String companyAddress;
    
    /**
     * 联系人
     */
    @TableField("contact_person")
    private String contactPerson;
    
    /**
     * 联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;
    
    /**
     * 所属行业
     */
    @TableField("industry")
    private String industry;
    
    /**
     * 个人简介
     */
    @TableField("description")
    private String description;
} 