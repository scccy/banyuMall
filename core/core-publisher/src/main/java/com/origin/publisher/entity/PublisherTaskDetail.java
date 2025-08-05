package com.origin.publisher.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务详情表实体
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("publisher_task_detail")
public class PublisherTaskDetail extends BaseEntity {
    
    @TableId(value = "detail_id", type = IdType.ASSIGN_ID)
    private String detailId;                  // 详情ID
    
    @TableField("task_id")
    private String taskId;                    // 任务ID
    
    @TableField("task_config")
    private String taskConfig;                // 任务配置JSON
} 