package com.origin.banyu.wechatWork.controller;

import com.origin.banyu.wechatWork.adapter.WechatWorkApiAdapter;
import com.origin.banyu.wechatWork.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tp/wechatWork/test")
public class WechatWorkTestController {
    @Autowired
    private WechatWorkApiAdapter wechatWorkApiAdapter;
    
    
    @Autowired
    private AccessTokenService accessTokenService;

    @GetMapping
    public void test() {

            // 获取access token
            String accessToken = accessTokenService.getAccessToken();

            // 调用getDepartmentIds方法获取部门ID列表
            wechatWorkApiAdapter.getDepartmentIds(accessToken);

    }
}
