package com.origin.publisher.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 任务更新请求DTO
 * 作者: scccy
 * 创建时间: 2025-01-27
 */
@Data
public class TaskUpdateRequest {
    
    @NotBlank(message = "任务名称不能为空")
    private String taskName;                  // 任务名称
    
    private String taskDescription;           // 任务描述
    
    @DecimalMin(value = "0.01", message = "任务积分必须大于0")
    private BigDecimal taskReward;            // 任务积分
    
    private String taskIconUrl;               // 任务图标URL
    
    // 特定任务类型的配置字段
    private Map<String, Object> taskConfig;   // 任务特定配置
} 