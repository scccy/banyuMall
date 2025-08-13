package com.origin.banyu.publisher.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分享审核响应DTO
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Data
public class ShareReviewResponse {
    
    /**
     * 分享审核ID
     */
    private String shareReviewId;
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 分享内容
     */
    private String shareContent;
    
    /**
     * 分享平台
     */
    private String sharePlatform;
    
    /**
     * 分享链接
     */
    private String shareUrl;
    
    /**
     * 截图URL
     */
    private String screenshotUrl;
    
    /**
     * 审核状态ID
     */
    private Integer reviewStatusId;
    
    /**
     * 审核意见
     */
    private String reviewComment;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
} 