package com.origin.banyu.wechatWork.adapter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.origin.banyu.base.manager.OkHttpManager;
import com.origin.banyu.wechatWork.exception.WechatWorkServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 企业微信权限API适配器
 * 符合第三方架构特殊规则：使用适配器模式封装第三方API
 * 仅保留当前使用场景：获取access_token
 * 
 * @author scccy
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WechatworkAuthAdapter {
    
    private final OkHttpManager okHttpManager;
    
    /**
     * 获取企业微信访问令牌
     * 根据企业微信官方文档：https://developer.work.weixin.qq.com/document/path/91039
     * 
     * @param corpid 企业ID
     * @param corpsecret 应用的凭证密钥
     * @return 访问令牌
     * @throws WechatWorkServiceException 当API调用失败时抛出
     */
    public String getAccessToken(String corpid, String corpsecret) {
        try {
            String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s",
                    corpid, corpsecret);
            
            log.info("调用企业微信API获取访问令牌: corpid={}", corpid);
            String responseBody = okHttpManager.get(url);
            JSONObject result = JSON.parseObject(responseBody);
            
            if (result.getInteger("errcode") != 0) {
                String errorMsg = result.getString("errmsg");
                log.error("获取访问令牌失败: errcode={}, errmsg={}", 
                        result.getInteger("errcode"), errorMsg);
                throw new WechatWorkServiceException("WECHATWORK_ACCESS_TOKEN_GET_FAILED", 
                        "获取访问令牌失败: " + errorMsg);
            }
            
            String accessToken = result.getString("access_token");
            Integer expiresIn = result.getInteger("expires_in");
            
            log.info("获取访问令牌成功: expiresIn={}秒", expiresIn);
            return accessToken;
            
        } catch (WechatWorkServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用企业微信API获取访问令牌异常", e);
            throw new WechatWorkServiceException("WECHATWORK_API_ERROR", 
                    "调用企业微信API异常: " + e.getMessage());
        }
    }
}
