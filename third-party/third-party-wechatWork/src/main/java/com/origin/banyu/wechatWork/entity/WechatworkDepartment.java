package com.origin.banyu.wechatWork.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.origin.banyu.wechatWork.dto.WechatWorkDepartmentInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 企业微信部门实体
 * 对应wechatwork_department表
 * 注意：这是维度表，不需要继承基础父类
 * 使用dep_id作为主键，因为企业微信部门ID本身就是唯一的
 * 
 * @author scccy
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wechatwork_department")
public class WechatworkDepartment {

    /**
     * 企业微信部门ID（主键）
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
     * 部门负责人列表（JSON格式）
     */
    private String departmentLeader;

    /**
     * 根据企业微信部门DTO构建实体
     */
    public static WechatworkDepartment fromDto(WechatWorkDepartmentInfo deptInfo) {
        WechatworkDepartment department = new WechatworkDepartment();
        department.setDepId(deptInfo.getId());
        department.setDepName(deptInfo.getName());
        department.setParentid(deptInfo.getParentid());
        department.setOrder(deptInfo.getOrder() != null ? deptInfo.getOrder().toString() : null);
        department.setDepartmentLeader(deptInfo.getDepartmentLeader() != null ? String.join(",", deptInfo.getDepartmentLeader()) : null);
        return department;
    }
}