package com.origin.banyu.publisher.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务完成响应DTO
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Data
public class TaskCompletionResponse {
    
    /**
     * 完成记录ID
     */
    private String completionId;
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 完成用户ID
     */
    private String userId;
    
    /**
     * 完成状态
     */
    private Integer completionStatus;
    
    /**
     * 完成时间
     */
    private LocalDateTime completionTime;
    
    /**
     * 获得奖励金额
     */
    private BigDecimal rewardAmount;
    
    /**
     * 完成证据
     */
    private Map<String, Object> completionEvidence;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
} 