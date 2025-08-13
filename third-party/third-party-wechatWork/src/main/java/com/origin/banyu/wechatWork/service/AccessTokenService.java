package com.origin.banyu.wechatWork.service;
import com.origin.banyu.common.dto.ResultData;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.origin.banyu.common.entity.ThirdPartyConfig;
import com.origin.banyu.common.dto.ThirdPartyPlatformConfigDTO;
import com.origin.banyu.wechatWork.feign.WechatWorkAuthFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.origin.banyu.base.manager.OkHttpManager;
import com.origin.banyu.common.util.ThirdPartyConfigParser;

import java.util.concurrent.TimeUnit;

/**
 * 企业微信Access Token服务
 * 
 * @author scccy
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AccessTokenService {
    
    private final WechatWorkAuthFeignClient authFeignClient;
    private final RedisTemplate<String, String> redisTemplate;

    private final OkHttpManager okHttpManager;
    
    private static final String ACCESS_TOKEN_KEY = "wechatwork:access_token";
    private static final String ACCESS_TOKEN_EXPIRE_KEY = "wechatwork:access_token_expire";
    private static final long TOKEN_EXPIRE_TIME = 7200L; // 2小时
    private static final long REFRESH_THRESHOLD = 300L; // 提前5分钟刷新
    
    /**
     * 获取access_token
     */
    public String getAccessToken() {
        // 1. 从Redis获取
        String cachedToken = redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY);
        if (cachedToken != null && !cachedToken.isEmpty()) {
            return cachedToken;
        }
        
        // 2. 从企业微信API获取
        return refreshAccessToken();
    }
    
    /**
     * 刷新access_token
     */
    public String refreshAccessToken() {
        try {
            // 1. 获取配置信息
            ResultData<ThirdPartyConfig> configResult = authFeignClient.getWechatWorkConfig();
            if (configResult.getCode() == null || !configResult.getCode().equals(200)) {
                throw new RuntimeException("获取企业微信配置失败");
            }
            
            ThirdPartyConfig config = configResult.getData();
            
            // 2. 获取WechatWorkConfig
            Object platformConfigObj = config.getPlatformConfig();
            String configJsonString;
            if (platformConfigObj instanceof String) {
                configJsonString = (String) platformConfigObj;
            } else if (platformConfigObj instanceof java.util.Map) {
                configJsonString = com.alibaba.fastjson2.JSON.toJSONString(platformConfigObj);
            } else {
                configJsonString = platformConfigObj != null ? platformConfigObj.toString() : null;
            }
            ThirdPartyPlatformConfigDTO.WechatWorkConfig wechatWorkConfig = (ThirdPartyPlatformConfigDTO.WechatWorkConfig) ThirdPartyConfigParser.parseConfigByType(
                config.getPlatformType(), configJsonString);
            if (wechatWorkConfig == null || wechatWorkConfig.getCorpId() == null || wechatWorkConfig.getCorpSecret() == null) {
                throw new RuntimeException("企业微信配置信息不完整");
            }
            
            // 3. 调用企业微信API获取token
            String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s",
                    wechatWorkConfig.getCorpId(), wechatWorkConfig.getCorpSecret());
            
            String responseBody = okHttpManager.get(url);
            JSONObject result = JSON.parseObject(responseBody);
            
            if (result.getInteger("errcode") != 0) {
                throw new RuntimeException("获取access_token失败: " + result.getString("errmsg"));
            }
            
            String accessToken = result.getString("access_token");
            long expiresIn = result.getLong("expires_in");
            
            // 4. 缓存到Redis（优先执行，确保token可用）
            redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY, accessToken, expiresIn - REFRESH_THRESHOLD, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(ACCESS_TOKEN_EXPIRE_KEY, String.valueOf(System.currentTimeMillis() + expiresIn * 1000));
            
            log.info("access_token刷新成功，已缓存到Redis");
            return accessToken;
            
        } catch (Exception e) {
            log.error("刷新access_token失败", e);
            throw new RuntimeException("获取access_token失败", e);
        }
    }
}