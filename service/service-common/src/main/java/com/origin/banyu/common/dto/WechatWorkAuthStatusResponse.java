package com.origin.banyu.common.dto;

import com.origin.banyu.common.entity.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 企业微信授权状态响应DTO
 * @author scccy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WechatWorkAuthStatusResponse extends BaseEntity {
    /** 用户ID */
    private String userId;
    /** 企业微信用户ID */
    private String wechatId;
    /** 授权状态：0-未授权，1-已授权 */
    private Integer authStatus;
    /** 授权时间 */
    private LocalDateTime authTime;
    /** 企业微信用户信息 */
    private WechatWorkUserInfo userInfo;
}

