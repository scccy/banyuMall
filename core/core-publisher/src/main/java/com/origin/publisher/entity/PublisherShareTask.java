package com.origin.publisher.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.origin.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 社群分享任务实体
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("publisher_share_task")
public class PublisherShareTask extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("share_content")
    private String shareContent;
    
    @TableField("share_platform")
    private String sharePlatform;
    
    @TableField("share_url")
    private String shareUrl;
    
    @TableField("screenshot_required")
    private Boolean screenshotRequired;
    
    @TableField("share_count")
    private Integer shareCount;
} 