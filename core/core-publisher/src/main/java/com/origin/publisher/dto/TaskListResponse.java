package com.origin.publisher.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务列表响应DTO
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Data
public class TaskListResponse {
    private String taskId;                    // 任务ID
    private String taskName;                  // 任务名称
    private Integer taskTypeId;               // 任务类型ID
    private String taskDescription;           // 任务描述
    private BigDecimal taskReward;            // 任务积分
    private String taskIconUrl;               // 任务图标URL
    private Integer statusId;                 // 任务状态ID
    private LocalDateTime createdTime;        // 创建时间
    private Integer completionCount;          // 完成人数统计
    private Map<String, Object> taskConfig;   // 任务特定配置
} 