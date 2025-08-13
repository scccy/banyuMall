package com.origin.banyu.user.controller;

import com.origin.banyu.base.controller.BaseHealthController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户服务健康检查控制器
 * 
 * @author origin
 * @since 2025-08-07
 */
@Tag(name = "用户服务健康检查", description = "用户服务健康检查接口")
@RestController
@RequestMapping("/service/user/health")
public class UserHealthController extends BaseHealthController {

    @Override
    protected String getHealthPath() {
        return "/service/user/health";
    }
} 