package com.origin.publisher.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 任务主表实体
 * 作者: scccy
 * 创建时间: 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("publisher_task")
public class PublisherTask extends BaseEntity {
    
    @TableId(value = "task_id", type = IdType.ASSIGN_ID)
    private String taskId;                    // 任务ID
    
    @TableField("task_name")
    private String taskName;                  // 任务名称
    
    @TableField("task_type_id")
    private Integer taskTypeId;               // 任务类型ID：1-点赞，2-评论，3-讨论，4-分享，5-邀请，6-反馈，7-排行榜
    
    @TableField("task_description")
    private String taskDescription;           // 任务描述
    
    @TableField("task_reward")
    private BigDecimal taskReward;            // 任务积分
    
    @TableField("task_icon_url")
    private String taskIconUrl;               // 任务图标URL
    
    @TableField("detail_id")
    private String detailId;                  // 详情配置ID
    
    @TableField("status_id")
    private Integer statusId;                 // 任务状态ID：1-草稿，2-上架，3-下架，4-审核通过，5-审核不通过
} 