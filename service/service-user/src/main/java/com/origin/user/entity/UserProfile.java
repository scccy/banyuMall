package com.origin.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户扩展信息实体
 * 
 * @author scccy
 * @since 2024-07-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("user_profile")
public class UserProfile extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;
    
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
     * 行业
     */
    @TableField("industry")
    private String industry;
    
    /**
     * 描述
     */
    @TableField("description")
    private String description;
} 