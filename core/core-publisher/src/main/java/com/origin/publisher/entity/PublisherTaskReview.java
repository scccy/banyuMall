package com.origin.publisher.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 任务审核实体
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("publisher_task_review")
public class PublisherTaskReview extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("reviewer_id")
    private String reviewerId;
    
    @TableField("review_status")
    private String reviewStatus;
    
    @TableField("review_comment")
    private String reviewComment;
    
    @TableField("review_time")
    private LocalDateTime reviewTime;
} 