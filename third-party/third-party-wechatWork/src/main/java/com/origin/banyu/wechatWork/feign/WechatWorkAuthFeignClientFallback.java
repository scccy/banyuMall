package com.origin.banyu.wechatWork.feign;

import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.entity.ThirdPartyConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
/**
 * 企业微信认证服务Feign降级处理
 * 
 * @author scccy
 */
@Slf4j
@Component
public class WechatWorkAuthFeignClientFallback implements WechatWorkAuthFeignClient {
    
    @Override
    public ResultData<ThirdPartyConfig> getWechatWorkConfig() {
        log.error("获取企业微信配置失败，服务降级");
        return ResultData.fail(ErrorCode.SERVICE_UNAVAILABLE, "auth服务不可用");
    }

} 