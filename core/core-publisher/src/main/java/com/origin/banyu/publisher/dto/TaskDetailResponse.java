package com.origin.banyu.publisher.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * 任务详情响应DTO
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Data
public class TaskDetailResponse {
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 任务类型ID
     */
    private Integer taskTypeId;
    
    /**
     * 任务描述
     */
    private String taskDescription;
    
    /**
     * 任务积分
     */
    private BigDecimal taskReward;
    
    /**
     * 任务图标URL
     */
    private String taskIconUrl;
    
    /**
     * 任务状态ID
     */
    private Integer statusId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 完成人数统计
     */
    private Integer completionCount;
    
    /**
     * 任务特定配置（前端自定义字段）
     */
    private String taskConfig;
} 