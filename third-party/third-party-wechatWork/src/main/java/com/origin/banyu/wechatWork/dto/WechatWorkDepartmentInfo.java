package com.origin.banyu.wechatWork.dto;

import lombok.Data;

import java.util.List;

/**
 * 企业微信部门信息DTO
 * 用于API数据传输
 * 
 * @author scccy
 */
@Data
public class WechatWorkDepartmentInfo {

    /**
     * 部门ID
     */
    private Integer id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 父部门ID
     */
    private Integer parentid;

    /**
     * 排序
     */
    private Integer order;

    /**
     * 部门负责人列表
     */
    private List<String> departmentLeader;

    /**
     * 子部门列表
     */
    private List<WechatWorkDepartmentInfo> children;

    /**
     * 部门层级路径
     */
    private String path;

    /**
     * 部门状态 (1-启用, 0-禁用)
     */
    private Integer status;

    /**
     * 部门描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;
}
