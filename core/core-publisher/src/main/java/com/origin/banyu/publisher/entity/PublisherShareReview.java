package com.origin.banyu.publisher.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 社群分享审核表实体
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("publisher_share_review")
public class PublisherShareReview extends BasePublisherEntity {
    
    /**
     * 分享审核ID
     */
    @TableId(value = "share_review_id", type = IdType.ASSIGN_ID)
    private String shareReviewId;
    
    /**
     * 任务ID
     */
    @TableField("task_id")
    private String taskId;
    
    /**
     * 分享内容
     */
    @TableField("share_content")
    private String shareContent;
    
    /**
     * 分享平台
     */
    @TableField("share_platform")
    private String sharePlatform;
    
    /**
     * 分享链接
     */
    @TableField("share_url")
    private String shareUrl;
    
    /**
     * 截图URL
     */
    @TableField("screenshot_url")
    private String screenshotUrl;
    
    /**
     * 审核人ID
     */
    @TableField("reviewer_id")
    private String reviewerId;
    
    /**
     * 审核状态：1-待审核,2-通过,3-拒绝
     */
    @TableField("review_status_id")
    private Integer reviewStatusId;
    
    /**
     * 审核意见
     */
    @TableField("review_comment")
    private String reviewComment;
} 