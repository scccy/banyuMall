package com.origin.publisher.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论任务实体
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("publisher_comment_task")
public class PublisherCommentTask extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("target_url")
    private String targetUrl;
    
    @TableField("comment_template")
    private String commentTemplate;
    
    @TableField("min_comment_length")
    private Integer minCommentLength;
    
    @TableField("max_comment_length")
    private Integer maxCommentLength;
    
    @TableField("comment_count")
    private Integer commentCount;
} 