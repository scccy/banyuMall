package com.origin.banyu.wechatWork.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Access Token定时刷新任务
 * 
 * @author scccy
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AccessTokenRefreshTask {
    
    private final AccessTokenService accessTokenService;
    
    @Scheduled(fixedRate = 7000000) // 每1小时55分钟刷新一次
    public void refreshAccessToken() {
        try {
            accessTokenService.refreshAccessToken();
            log.info("定时刷新access_token成功");
        } catch (Exception e) {
            log.error("定时刷新access_token失败", e);
        }
    }
} 