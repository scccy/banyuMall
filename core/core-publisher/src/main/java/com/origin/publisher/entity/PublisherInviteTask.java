package com.origin.publisher.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 邀请任务实体
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("publisher_invite_task")
public class PublisherInviteTask extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("invite_platform")
    private String invitePlatform;
    
    @TableField("invite_link")
    private String inviteLink;
    
    @TableField("invite_code")
    private String inviteCode;
    
    @TableField("invite_count")
    private Integer inviteCount;
    
    @TableField("invite_reward")
    private BigDecimal inviteReward;
} 