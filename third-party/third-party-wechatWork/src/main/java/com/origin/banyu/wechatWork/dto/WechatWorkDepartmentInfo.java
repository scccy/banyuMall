package com.origin.banyu.wechatWork.dto;

import com.alibaba.fastjson2.JSONArray;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业微信部门信息DTO
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
     * 部门状态
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

    /**
     * 子部门列表
     */
    private List<WechatWorkDepartmentInfo> children;

    /**
     * 部门路径
     */
    private String path;

    /**
     * 从企业微信API响应创建WechatWorkDepartmentInfo对象
     * 静态构造方法，负责数据转换逻辑
     * 
     * @param apiResponse 企业微信API响应结果
     * @return WechatWorkDepartmentInfo对象
     */
    public static WechatWorkDepartmentInfo fromApiResponse(com.alibaba.fastjson2.JSONObject apiResponse) {
        WechatWorkDepartmentInfo deptInfo = new WechatWorkDepartmentInfo();
        deptInfo.setId(apiResponse.getInteger("id"));
        deptInfo.setName(apiResponse.getString("name"));
        deptInfo.setParentid(apiResponse.getInteger("parentid"));
        deptInfo.setOrder(apiResponse.getInteger("order"));
        
        // 处理部门负责人列表
        com.alibaba.fastjson2.JSONArray leaderArray = apiResponse.getJSONArray("department_leader");
        if (leaderArray != null) {
            List<String> leaders = new ArrayList<>();
            for (int i = 0; i < leaderArray.size(); i++) {
                leaders.add(leaderArray.getString(i));
            }
            deptInfo.setDepartmentLeader(leaders);
        }
        
        deptInfo.setStatus(apiResponse.getInteger("status"));
        deptInfo.setDescription(apiResponse.getString("description"));
        deptInfo.setCreateTime(apiResponse.getLong("create_time"));
        deptInfo.setUpdateTime(apiResponse.getLong("update_time"));
        
        return deptInfo;
    }
}
