package com.origin.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 系统角色实体
 * 
 * @author origin
 * @since 2024-07-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {
    
    /**
     * 角色ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 角色名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 角色编码
     */
    @TableField("code")
    private String code;
    
    /**
     * 角色描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 状态：0-禁用，1-正常
     */
    @TableField("status")
    private Integer status;
}