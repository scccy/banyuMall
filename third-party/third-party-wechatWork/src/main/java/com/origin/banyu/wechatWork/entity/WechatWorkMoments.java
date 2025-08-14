package com.origin.banyu.wechatWork.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.origin.banyu.common.entity.BaseEntity;
import lombok.*;

/**
 * 企业微信朋友圈信息实体
 * 
 * @author scccy
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("wechatwork_moments")
public class WechatWorkMoments extends BaseEntity {
    
    @TableId(value = "wechatwork_moment_id", type = IdType.INPUT)
    private String wechatworkMomentId;
    
    @TableField("wechatwork_user_id")
    private String wechatworkUserId;
    
    @TableField("content")
    private String content;
    
    @TableField("media_urls")
    private String mediaUrls; // JSON格式存储
} 