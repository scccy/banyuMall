package com.origin.banyu.aliyunOss.controller;

import com.origin.banyu.base.controller.BaseHealthController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 阿里云OSS服务健康检查控制器
 * 
 * @author origin
 * @since 2025-08-07
 */
@Tag(name = "阿里云OSS服务健康检查", description = "阿里云OSS服务健康检查接口")
@RestController
@RequestMapping("/tp/aliyunOss/health")
public class OssHealthController extends BaseHealthController {

    @Override
    protected String getHealthPath() {
        return "/tp/aliyunOss/health";
    }
} 