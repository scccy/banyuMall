package com.origin.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户角色关联实体
 * 
 * @author origin
 * @since 2024-07-30
 */
@Data
@Accessors(chain = true)
@TableName("sys_user_role")
public class SysUserRole {
    
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
     * 角色ID
     */
    @TableField("role_id")
    private String roleId;
}