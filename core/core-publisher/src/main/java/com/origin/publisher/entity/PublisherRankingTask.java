package com.origin.publisher.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 排行榜任务实体
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("publisher_ranking_task")
public class PublisherRankingTask extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("ranking_platform")
    private String rankingPlatform;
    
    @TableField("ranking_type")
    private String rankingType;
    
    @TableField("ranking_url")
    private String rankingUrl;
    
    @TableField("target_ranking")
    private Integer targetRanking;
    
    @TableField("ranking_reward")
    private BigDecimal rankingReward;
} 