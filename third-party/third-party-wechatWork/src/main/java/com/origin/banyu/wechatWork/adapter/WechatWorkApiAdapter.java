package com.origin.banyu.wechatWork.adapter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.origin.banyu.base.manager.OkHttpManager;
import com.origin.banyu.common.dto.WechatWorkUserInfo;
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
     * 获取用户信息
     * 
     * @param accessToken 访问令牌
     * @param userid 用户ID
     * @return 用户信息
     * @throws WechatWorkServiceException 当API调用失败时抛出
     */
    public WechatWorkUserInfo getUserInfo(String accessToken, String userid) {
        try {
            String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s",
                    accessToken, userid);
            
            log.info("调用企业微信API获取用户信息: userid={}", userid);
            String responseBody = okHttpManager.get(url);
            JSONObject result = JSON.parseObject(responseBody);
            
            if (result.getInteger("errcode") != 0) {
                String errorMsg = result.getString("errmsg");
                log.error("获取用户信息失败: userid={}, errcode={}, errmsg={}", 
                        userid, result.getInteger("errcode"), errorMsg);
                throw new WechatWorkServiceException("WECHATWORK_USER_GET_FAILED", 
                        "获取用户信息失败: " + errorMsg);
            }
            
            log.info("获取用户信息成功: userid={}", userid);

            return convertToUserInfo(result);
            
        } catch (WechatWorkServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用企业微信API获取用户信息异常: userid={}", userid, e);
            throw new WechatWorkServiceException("WECHATWORK_API_ERROR", 
                    "调用企业微信API异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取部门用户列表
     * 
     * @param accessToken 访问令牌
     * @param departmentId 部门ID
     * @return 用户信息列表
     * @throws WechatWorkServiceException 当API调用失败时抛出
     */
    public List<WechatWorkUserInfo> getDepartmentUsers(String accessToken, Integer departmentId) {
        try {
            String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?access_token=%s&department_id=%d&fetch_child=1",
                    accessToken, departmentId);
            
            log.info("调用企业微信API获取部门用户列表: departmentId={}", departmentId);
            String responseBody = okHttpManager.get(url);
            JSONObject result = JSON.parseObject(responseBody);
            
            if (result.getInteger("errcode") != 0) {
                String errorMsg = result.getString("errmsg");
                log.error("获取部门用户列表失败: departmentId={}, errcode={}, errmsg={}", 
                        departmentId, result.getInteger("errcode"), errorMsg);
                throw new WechatWorkServiceException("WECHATWORK_DEPARTMENT_USERS_GET_FAILED", 
                        "获取部门用户列表失败: " + errorMsg);
            }
            
            JSONArray userList = result.getJSONArray("userlist");
            List<WechatWorkUserInfo> users = new ArrayList<>();
            
            for (int i = 0; i < userList.size(); i++) {
                JSONObject user = userList.getJSONObject(i);
                String userid = user.getString("userid");
                
                try {
                    WechatWorkUserInfo userInfo = getUserInfo(accessToken, userid);
                    users.add(userInfo);
                } catch (Exception e) {
                    log.error("获取部门用户详情失败: userid={}", userid, e);
                    // 继续处理其他用户，不中断整个流程
                }
            }
            
            log.info("获取部门用户列表成功: departmentId={}, count={}", departmentId, users.size());
            return users;
            
        } catch (WechatWorkServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用企业微信API获取部门用户列表异常: departmentId={}", departmentId, e);
            throw new WechatWorkServiceException("WECHATWORK_API_ERROR", 
                    "调用企业微信API异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取部门列表
     * 
     * @param accessToken 访问令牌
     * @return 部门ID列表
     * @throws WechatWorkServiceException 当API调用失败时抛出
     */
    public List<Integer> getDepartmentIds(String accessToken) {
        try {
            String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s",
                    accessToken);
            
            log.info("调用企业微信API获取部门列表");
            String responseBody = okHttpManager.get(url);
            JSONObject result = JSON.parseObject(responseBody);
            System.out.println( result);
            if (result.getInteger("errcode") != 0) {
                String errorMsg = result.getString("errmsg");
                log.error("获取部门列表失败: errcode={}, errmsg={}", 
                        result.getInteger("errcode"), errorMsg);
                throw new WechatWorkServiceException("WECHATWORK_DEPARTMENT_LIST_GET_FAILED", 
                        "获取部门列表失败: " + errorMsg);
            }
            
            JSONArray departmentList = result.getJSONArray("department");
            List<Integer> departmentIds = new ArrayList<>();

            for (int i = 0; i < departmentList.size(); i++) {
                JSONObject dept = departmentList.getJSONObject(i);
                departmentIds.add(dept.getInteger("id"));
            }

            log.info("获取部门列表成功: count={}", departmentIds.size());

            return departmentIds;
            
        } catch (WechatWorkServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用企业微信API获取部门列表异常", e);
            throw new WechatWorkServiceException("WECHATWORK_API_ERROR", 
                    "调用企业微信API异常: " + e.getMessage());
        }
    }

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
     * 批量获取部门信息（支持分页，避免一次性获取过多数据）
     * 
     * @param accessToken 访问令牌
     * @param batchSize 批次大小，建议100-1000
     * @return 部门信息列表
     * @throws WechatWorkServiceException 当API调用失败时抛出
     */
    public List<WechatWorkDepartmentInfo> getDepartmentsBatch(String accessToken, int batchSize) {
        try {
            String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s",
                    accessToken);
            
            log.info("调用企业微信API批量获取部门信息，批次大小: {}", batchSize);
            String responseBody = okHttpManager.get(url);
            JSONObject result = JSON.parseObject(responseBody);
            
            if (result.getInteger("errcode") != 0) {
                String errorMsg = result.getString("errmsg");
                log.error("批量获取部门信息失败: errcode={}, errmsg={}", 
                        result.getInteger("errcode"), errorMsg);
                throw new WechatWorkServiceException("WECHATWORK_DEPARTMENTS_BATCH_GET_FAILED", 
                        "批量获取部门信息失败: " + errorMsg);
            }
            
            JSONArray departmentList = result.getJSONArray("department");
            List<WechatWorkDepartmentInfo> departments = new ArrayList<>();

            // 分批处理，避免内存占用过大
            int totalCount = departmentList.size();
            int processedCount = 0;
            
            for (int i = 0; i < totalCount; i++) {
                JSONObject dept = departmentList.getJSONObject(i);
                WechatWorkDepartmentInfo deptInfo = convertToDepartmentInfo(dept);
                departments.add(deptInfo);
                processedCount++;
                
                // 每处理一批次，记录进度
                if (processedCount % batchSize == 0) {
                    log.info("部门信息批量处理进度: {}/{}", processedCount, totalCount);
                }
            }

            log.info("批量获取部门信息成功: 总数={}, 批次大小={}", totalCount, batchSize);
            return departments;
            
        } catch (WechatWorkServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用企业微信API批量获取部门信息异常", e);
            throw new WechatWorkServiceException("WECHATWORK_API_ERROR", 
                    "调用企业微信API异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取外部联系人列表
     * 
     * @param accessToken 访问令牌
     * @param userid 企业微信用户ID
     * @return 外部联系人列表
     * @throws WechatWorkServiceException 当API调用失败时抛出
     */
    public List<String> getExternalContacts(String accessToken, String userid) {
        try {
            String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/list?access_token=%s&userid=%s",
                    accessToken, userid);
            
            log.info("调用企业微信API获取外部联系人列表: userid={}", userid);
            String responseBody = okHttpManager.get(url);
            JSONObject result = JSON.parseObject(responseBody);
            
            if (result.getInteger("errcode") != 0) {
                String errorMsg = result.getString("errmsg");
                log.error("获取外部联系人列表失败: userid={}, errcode={}, errmsg={}", 
                        userid, result.getInteger("errcode"), errorMsg);
                throw new WechatWorkServiceException("WECHATWORK_EXTERNAL_CONTACTS_GET_FAILED", 
                        "获取外部联系人列表失败: " + errorMsg);
            }
            
            JSONArray externalUserList = result.getJSONArray("external_userid");
            List<String> externalUserIds = new ArrayList<>();
            
            for (int i = 0; i < externalUserList.size(); i++) {
                externalUserIds.add(externalUserList.getString(i));
            }
            
            log.info("获取外部联系人列表成功: userid={}, count={}", userid, externalUserIds.size());
            return externalUserIds;
            
        } catch (WechatWorkServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用企业微信API获取外部联系人列表异常: userid={}", userid, e);
            throw new WechatWorkServiceException("WECHATWORK_API_ERROR", 
                    "调用企业微信API异常: " + e.getMessage());
        }
    }
    
    /**
     * 转换API响应为用户信息对象
     * 
     * @param result API响应结果
     * @return 用户信息对象
     */
    private WechatWorkUserInfo convertToUserInfo(JSONObject result) {
        WechatWorkUserInfo userInfo = new WechatWorkUserInfo();
        userInfo.setWechatworkUserId(result.getString("userid"));
        userInfo.setName(result.getString("name"));
        userInfo.setMobile(result.getString("mobile"));
        
        // 正确处理部门ID列表的转换
        JSONArray departmentArray = result.getJSONArray("department");
        if (departmentArray != null) {
            List<Integer> departmentList = new ArrayList<>();
            for (int i = 0; i < departmentArray.size(); i++) {
                departmentList.add(departmentArray.getInteger(i));
            }
            userInfo.setDepartment(departmentList);
        }
        
        userInfo.setPosition(result.getString("position"));
        userInfo.setGender(result.getInteger("gender"));
        userInfo.setEmail(result.getString("email"));
        userInfo.setAvatar(result.getString("avatar"));
        userInfo.setStatus(result.getInteger("status"));
        userInfo.setEnable(result.getInteger("enable"));
        userInfo.setIsleader(result.getInteger("isleader"));
        return userInfo;
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