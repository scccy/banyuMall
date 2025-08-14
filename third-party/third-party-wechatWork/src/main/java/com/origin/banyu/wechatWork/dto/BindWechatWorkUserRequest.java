package com.origin.banyu.wechatWork.dto;

import com.origin.banyu.common.dto.WechatWorkUserInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 绑定企业微信用户请求DTO
 * 
 * @author scccy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BindWechatWorkUserRequest {
    
    /**
     * 企业微信用户ID
     */
    @NotBlank(message = "企业微信用户ID不能为空")
    private String userid;
    
    /**
     * 系统用户ID
     */
    @NotBlank(message = "系统用户ID不能为空")
    private String sysUserId;
    
    /**
     * 用户信息
     */
    @NotNull(message = "用户信息不能为空")
    private WechatWorkUserInfo userInfo;
} 