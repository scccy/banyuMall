package com.origin.banyu.wechatWork.dto;

import com.origin.banyu.common.dto.WechatWorkUserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 授权回调响应DTO
 * 
 * @author scccy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthCallbackResponse {
    
    /**
     * 企业微信用户ID
     */
    private String userid;
    
    /**
     * 用户信息
     */
    private WechatWorkUserInfo userInfo;
    
    /**
     * 是否已绑定
     */
    private Boolean isBound;
    
    /**
     * 状态参数
     */
    private String state;
} 