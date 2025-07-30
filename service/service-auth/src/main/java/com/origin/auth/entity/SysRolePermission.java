package com.origin.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 角色权限关联实体类
 */
@Data
@Accessors(chain = true)
@TableName("sys_role_permission")
public class SysRolePermission {

    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private String roleId;

    /**
     * 权限ID
     */
    @TableField("permission_id")
    private String permissionId;
}