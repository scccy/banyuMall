package com.origin.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 系统权限实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_permission")
public class SysPermission extends BaseEntity {

    /**
     * 权限ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 权限名称
     */
    @TableField("name")
    private String name;

    /**
     * 权限编码
     */
    @TableField("code")
    private String code;

    /**
     * 权限类型：1-菜单，2-按钮，3-接口
     */
    @TableField("type")
    private Integer type;

    /**
     * 父权限ID
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 路径
     */
    @TableField("path")
    private String path;

    /**
     * 组件
     */
    @TableField("component")
    private String component;

    /**
     * 图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 状态：0-禁用，1-正常
     */
    @TableField("status")
    private Integer status;
}