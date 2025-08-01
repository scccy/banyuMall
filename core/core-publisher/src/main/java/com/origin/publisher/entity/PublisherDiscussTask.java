package com.origin.publisher.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 讨论任务实体
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("publisher_discuss_task")
public class PublisherDiscussTask extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("discuss_topic")
    private String discussTopic;
    
    @TableField("discuss_content")
    private String discussContent;
    
    @TableField("discuss_platform")
    private String discussPlatform;
    
    @TableField("discuss_url")
    private String discussUrl;
    
    @TableField("min_discuss_length")
    private Integer minDiscussLength;
    
    @TableField("discuss_count")
    private Integer discussCount;
} 