package com.origin.banyu.wechatWork.feign;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.ThirdPartyConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 企业微信认证服务Feign客户端
 * 
 * @author scccy
 */
@FeignClient(name = "service-auth", fallback = WechatWorkAuthFeignClientFallback.class)
public interface WechatWorkAuthFeignClient {
    
    /**
     * 获取企业微信配置信息
     * 
     * @return 企业微信配置
     */
    @GetMapping("/service/auth/tp/config/platform/1")
    ResultData<ThirdPartyConfig> getWechatWorkConfig();


} 