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
    
    /**
     * 获取企业微信JS-SDK使用权限签名
     * 根据企业微信官方文档：https://developer.work.weixin.qq.com/document/path/90539
     * 
     * @param accessToken 访问令牌
     * @param jsapiTicket JS-SDK使用权限签名
     * @param noncestr 随机字符串
     * @param timestamp 时间戳
     * @param url 当前网页的URL
     * @return 签名
     * @throws WechatWorkServiceException 当API调用失败时抛出
     */
    public String getJsApiSignature(String jsapiTicket, String noncestr, String timestamp, String url) {
        try {
            // 按照企业微信JS-SDK签名算法生成签名
            String string1 = String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%s&url=%s",
                    jsapiTicket, noncestr, timestamp, url);
            
            // 使用SHA1算法生成签名
            java.security.MessageDigest crypt = java.security.MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            String signature = bytesToHex(crypt.digest());
            
            log.info("生成JS-SDK签名成功: url={}", url);
            return signature;
            
        } catch (Exception e) {
            log.error("生成JS-SDK签名失败", e);
            throw new WechatWorkServiceException("WECHATWORK_JSAPI_SIGNATURE_GENERATE_FAILED", 
                    "生成JS-SDK签名失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取JS-SDK使用权限签名
     * 根据企业微信官方文档：https://developer.work.weixin.qq.com/document/path/90539
     * 
     * @param accessToken 访问令牌
     * @return JS-SDK使用权限签名
     * @throws WechatWorkServiceException 当API调用失败时抛出
     */
    public String getJsApiTicket(String accessToken) {
        try {
            String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=%s",
                    accessToken);
            
            log.info("调用企业微信API获取JS-SDK使用权限签名");
            String responseBody = okHttpManager.get(url);
            JSONObject result = JSON.parseObject(responseBody);
            
            if (result.getInteger("errcode") != 0) {
                String errorMsg = result.getString("errmsg");
                log.error("获取JS-SDK使用权限签名失败: errcode={}, errmsg={}", 
                        result.getInteger("errcode"), errorMsg);
                throw new WechatWorkServiceException("WECHATWORK_JSAPI_TICKET_GET_FAILED", 
                        "获取JS-SDK使用权限签名失败: " + errorMsg);
            }
            
            String ticket = result.getString("ticket");
            Integer expiresIn = result.getInteger("expires_in");
            
            log.info("获取JS-SDK使用权限签名成功: expiresIn={}秒", expiresIn);
            return ticket;
            
        } catch (WechatWorkServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用企业微信API获取JS-SDK使用权限签名异常", e);
            throw new WechatWorkServiceException("WECHATWORK_API_ERROR", 
                    "调用企业微信API异常: " + e.getMessage());
        }
    }
    
    /**
     * 字节数组转十六进制字符串
     * 
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
