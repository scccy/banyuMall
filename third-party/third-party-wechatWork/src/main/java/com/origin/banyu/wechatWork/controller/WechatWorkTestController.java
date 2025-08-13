package com.origin.banyu.wechatWork.controller;

import com.origin.banyu.wechatWork.adapter.WechatWorkApiAdapter;
import com.origin.banyu.wechatWork.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tp/wechatWork/test")
public class WechatWorkTestController {
    @Autowired
    private WechatWorkApiAdapter wechatWorkApiAdapter;
    
    
    @Autowired
    private AccessTokenService accessTokenService;

    @GetMapping
    public String test() {
        try {
            // 获取access token
            String accessToken = accessTokenService.getAccessToken();
            
            // 调用getDepartmentIds方法获取部门ID列表
            List<Integer> departmentIds = wechatWorkApiAdapter.getDepartmentIds(accessToken);
            
            return "部门ID列表获取成功: " + departmentIds;
        } catch (Exception e) {
            return "获取部门ID列表失败: " + e.getMessage();
        }
    }
}
