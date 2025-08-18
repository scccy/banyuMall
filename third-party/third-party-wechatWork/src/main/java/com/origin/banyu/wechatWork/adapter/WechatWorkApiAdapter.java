package com.origin.banyu.wechatWork.adapter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.origin.banyu.base.manager.OkHttpManager;
import com.origin.banyu.wechatWork.dto.WechatWorkDepartmentInfo;
import com.origin.banyu.wechatWork.exception.WechatWorkServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业微信API适配器
 * 符合第三方架构特殊规则：使用适配器模式封装第三方API
 * 
 * @author scccy
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WechatWorkApiAdapter {
    
    private final OkHttpManager okHttpManager;
    
    /**
     * 获取所有部门详细信息
     * 
     * @param accessToken 访问令牌
     * @return 部门信息列表
     * @throws WechatWorkServiceException 当API调用失败时抛出
     */
    public List<WechatWorkDepartmentInfo> getAllDepartments(String accessToken) {
        try {
            String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s",
                    accessToken);
            
            log.info("调用企业微信API获取所有部门详细信息");
            String responseBody = okHttpManager.get(url);
            JSONObject result = JSON.parseObject(responseBody);
            
            if (result.getInteger("errcode") != 0) {
                String errorMsg = result.getString("errmsg");
                log.error("获取所有部门详细信息失败: errcode={}, errmsg={}", 
                        result.getInteger("errcode"), errorMsg);
                throw new WechatWorkServiceException("WECHATWORK_ALL_DEPARTMENTS_GET_FAILED", 
                        "获取所有部门详细信息失败: " + errorMsg);
            }
            
            JSONArray departmentList = result.getJSONArray("department");
            List<WechatWorkDepartmentInfo> departments = new ArrayList<>();

            for (int i = 0; i < departmentList.size(); i++) {
                JSONObject dept = departmentList.getJSONObject(i);
                WechatWorkDepartmentInfo deptInfo = convertToDepartmentInfo(dept);
                departments.add(deptInfo);
            }

            log.info("获取所有部门详细信息成功: count={}", departments.size());
            return departments;
            
        } catch (WechatWorkServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用企业微信API获取所有部门详细信息异常", e);
            throw new WechatWorkServiceException("WECHATWORK_API_ERROR", 
                    "调用企业微信API异常: " + e.getMessage());
        }
    }
    
    /**
     * 转换API响应为部门信息对象
     * 
     * @param result API响应结果
     * @return 部门信息对象
     */
    private WechatWorkDepartmentInfo convertToDepartmentInfo(JSONObject result) {
        WechatWorkDepartmentInfo deptInfo = new WechatWorkDepartmentInfo();
        deptInfo.setId(result.getInteger("id"));
        deptInfo.setName(result.getString("name"));
        deptInfo.setParentid(result.getInteger("parentid"));
        deptInfo.setOrder(result.getInteger("order"));
        
        // 处理部门负责人列表
        JSONArray leaderArray = result.getJSONArray("department_leader");
        if (leaderArray != null) {
            List<String> leaders = new ArrayList<>();
            for (int i = 0; i < leaderArray.size(); i++) {
                leaders.add(leaderArray.getString(i));
            }
            deptInfo.setDepartmentLeader(leaders);
        }
        
        deptInfo.setStatus(result.getInteger("status"));
        deptInfo.setDescription(result.getString("description"));
        deptInfo.setCreateTime(result.getLong("create_time"));
        deptInfo.setUpdateTime(result.getLong("update_time"));
        
        return deptInfo;
    }
} 