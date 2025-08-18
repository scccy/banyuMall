package com.origin.banyu.wechatWork.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 企业微信部门实体
 * 
 * @author scccy
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wechatwork_department")
public class WechatworkDepartment {

    /**
     * 部门ID
     */
    @TableId(value = "dep_id", type = IdType.INPUT)
    private Integer depId;

    /**
     * 部门名称
     */
    private String depName;

    /**
     * 父部门ID
     */
    private Integer parentid;

    /**
     * 排序
     */
    private String order;

    /**
     * 部门负责人
     */
    private String departmentLeader;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新人
     */
    private String updatedBy;
}