package com.origin.banyu.wechatWork.adapter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.origin.banyu.base.manager.OkHttpManager;
import com.origin.banyu.wechatWork.dto.WechatWorkUserInfo;
import com.origin.banyu.wechatWork.exception.WechatWorkServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业微信用户API适配器
 * 符合第三方架构特殊规则：使用适配器模式封装第三方API
 * 
 * @author scccy
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WechatWorkUserApiAdapter {
    
    private final OkHttpManager okHttpManager;
    
    /**
     * 获取部门成员详情
     * 根据企业微信官方文档：https://developer.work.weixin.qq.com/document/path/90337
     * 
     * @param accessToken 访问令牌
     * @param departmentId 部门ID
     * @param fetchChild 是否递归获取子部门下面的成员：1-是，0-否
     * @return 用户信息列表
     * @throws WechatWorkServiceException 当API调用失败时抛出
     */
    public List<WechatWorkUserInfo> getDepartmentUsers(String accessToken, Integer departmentId, Integer fetchChild) {
        try {
            String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=%s&department_id=%d&fetch_child=%d",
                    accessToken, departmentId, fetchChild != null ? fetchChild : 0);
            
            log.info("调用企业微信API获取部门成员详情: departmentId={}, fetchChild={}", departmentId, fetchChild);
            String responseBody = okHttpManager.get(url);
            JSONObject result = JSON.parseObject(responseBody);
            
            if (result.getInteger("errcode") != 0) {
                String errorMsg = result.getString("errmsg");
                log.error("获取部门成员详情失败: departmentId={}, errcode={}, errmsg={}", 
                        departmentId, result.getInteger("errcode"), errorMsg);
                throw new WechatWorkServiceException("WECHATWORK_DEPARTMENT_USERS_GET_FAILED", 
                        "获取部门成员详情失败: " + errorMsg);
            }
            
            JSONArray userList = result.getJSONArray("userlist");
            List<WechatWorkUserInfo> users = new ArrayList<>();

            for (int i = 0; i < userList.size(); i++) {
                JSONObject user = userList.getJSONObject(i);
                // 使用DTO的静态构造方法进行数据转换
                WechatWorkUserInfo userInfo = WechatWorkUserInfo.fromApiResponse(user);
                users.add(userInfo);
            }

            log.info("获取部门成员详情成功: departmentId={}, count={}", departmentId, users.size());
            return users;
            
        } catch (WechatWorkServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用企业微信API获取部门成员详情异常: departmentId={}", departmentId, e);
            throw new WechatWorkServiceException("WECHATWORK_API_ERROR", 
                    "调用企业微信API异常: " + e.getMessage());
        }
    }
}
