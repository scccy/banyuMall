package com.origin.publisher.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 反馈任务实体
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("publisher_feedback_task")
public class PublisherFeedbackTask extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("feedback_type")
    private String feedbackType;
    
    @TableField("feedback_platform")
    private String feedbackPlatform;
    
    @TableField("feedback_content")
    private String feedbackContent;
    
    @TableField("feedback_url")
    private String feedbackUrl;
    
    @TableField("feedback_count")
    private Integer feedbackCount;
} 