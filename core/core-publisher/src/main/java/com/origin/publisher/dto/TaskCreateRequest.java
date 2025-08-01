package com.origin.publisher.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务创建请求DTO
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Data
public class TaskCreateRequest {
    
    @NotBlank(message = "任务名称不能为空")
    private String taskName;
    
    @NotBlank(message = "任务类型不能为空")
    private String taskType;
    
    private String taskDescription;
    
    @NotNull(message = "奖励金额不能为空")
    @DecimalMin(value = "0.01", message = "奖励金额必须大于0")
    private BigDecimal rewardAmount;
    
    @NotBlank(message = "发布者ID不能为空")
    private String publisherId;
    
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    
    @Min(value = 1, message = "最大参与人数必须大于0")
    private Integer maxParticipants;
    
    // 任务类型特定字段
    private String targetUrl;
    private String commentTemplate;
    private String shareContent;
    private String sharePlatform;
    private String shareUrl;
    private Boolean screenshotRequired;
    private Boolean commentRequired;
    private Integer minCommentLength;
    private Integer maxCommentLength;
    
    // 讨论任务字段
    private String discussTopic;
    private String discussContent;
    private String discussPlatform;
    private String discussUrl;
    private Integer minDiscussLength;
    
    // 邀请任务字段
    private String invitePlatform;
    private String inviteLink;
    private String inviteCode;
    private BigDecimal inviteReward;
    
    // 反馈任务字段
    private String feedbackType;
    private String feedbackPlatform;
    private String feedbackContent;
    private String feedbackUrl;
    
    // 排行榜任务字段
    private String rankingPlatform;
    private String rankingType;
    private String rankingUrl;
    private Integer targetRanking;
    private BigDecimal rankingReward;
} 