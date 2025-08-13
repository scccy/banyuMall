package com.origin.banyu.publisher.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.banyu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务完成流水表实体
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("publisher_task_completion")
public class PublisherTaskCompletion extends BasePublisherEntity {
    
    /**
     * 完成记录ID
     */
    @TableId(value = "completion_id", type = IdType.ASSIGN_ID)
    private String completionId;
    
    /**
     * 任务ID
     */
    @TableField("task_id")
    private String taskId;
    
    /**
     * 完成用户ID
     */
    @TableField("user_id")
    private String userId;
    
    /**
     * 完成状态：1-进行中，2-已完成，3-已拒绝
     */
    @TableField("completion_status")
    private Integer completionStatus;
    
    /**
     * 完成时间
     */
    @TableField("completion_time")
    private LocalDateTime completionTime;
    
    /**
     * 获得奖励金额
     */
    @TableField("reward_amount")
    private BigDecimal rewardAmount;
    
    /**
     * 完成证据JSON（仅社群分享任务需要）
     */
    @TableField("completion_evidence")
    private String completionEvidence;
} 