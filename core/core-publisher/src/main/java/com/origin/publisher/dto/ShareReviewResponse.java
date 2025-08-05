package com.origin.publisher.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分享审核响应DTO
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Data
public class ShareReviewResponse {
    private String shareReviewId;             // 分享审核ID
    private String taskId;                    // 任务ID
    private String shareContent;              // 分享内容
    private String sharePlatform;             // 分享平台
    private String shareUrl;                  // 分享链接
    private String screenshotUrl;             // 截图URL
    private Integer reviewStatusId;           // 审核状态ID
    private String reviewComment;             // 审核意见
    private LocalDateTime createdTime;        // 创建时间
} 