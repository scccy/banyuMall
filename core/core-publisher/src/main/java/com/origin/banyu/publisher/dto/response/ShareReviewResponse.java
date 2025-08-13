package com.origin.banyu.publisher.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
     * 提交用户ID
     */
    private String userId;
    
    /**
     * 微信昵称
     */
    private String wechatNickname;
    
    /**
     * 分享内容
     */
    private String shareContent;
    
    /**
     * 分享平台
     */
    private String sharePlatform;
    
    /**
     * 分享链接（多个）
     */
    private List<String> links;
    
    /**
     * 截图URL（多个）
     */
    private List<String> images;
    
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