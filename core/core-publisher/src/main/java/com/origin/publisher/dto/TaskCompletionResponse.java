package com.origin.publisher.dto;

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
    private String completionId;              // 完成记录ID
    private String taskId;                    // 任务ID
    private String userId;                    // 完成用户ID
    private Integer completionStatus;         // 完成状态
    private LocalDateTime completionTime;     // 完成时间
    private BigDecimal rewardAmount;          // 获得奖励金额
    private Map<String, Object> completionEvidence; // 完成证据
    private LocalDateTime createdTime;        // 创建时间
} 