package com.origin.publisher.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务基础实体
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("publisher_task")
public class PublisherTask extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_name")
    private String taskName;
    
    @TableField("task_type")
    private String taskType;
    
    @TableField("task_description")
    private String taskDescription;
    
    @TableField("reward_amount")
    private BigDecimal rewardAmount;
    
    @TableField("publisher_id")
    private String publisherId;
    
    @TableField("status")
    private String status;
    
    @TableField("start_time")
    private LocalDateTime startTime;
    
    @TableField("end_time")
    private LocalDateTime endTime;
    
    @TableField("max_participants")
    private Integer maxParticipants;
    
    @TableField("current_participants")
    private Integer currentParticipants;
    
    @TableField("qr_code_url")
    private String qrCodeUrl;
    
    @TableField("task_avatar_url")
    private String taskAvatarUrl;
} 