package com.origin.banyu.wechatWork.controller;

import com.origin.banyu.base.controller.BaseHealthController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业微信服务健康检查控制器
 * 
 * @author origin
 * @since 2025-08-07
 */
@Tag(name = "企业微信服务健康检查", description = "企业微信服务健康检查接口")
@RestController
@RequestMapping("/tp/wechatWork/health")
public class WechatWorkHealthController extends BaseHealthController {

    @Override
    protected String getHealthPath() {
        return "/tp/wechatWork/health";
    }
} 