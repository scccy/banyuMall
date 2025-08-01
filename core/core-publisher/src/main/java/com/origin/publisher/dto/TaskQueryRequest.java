package com.origin.publisher.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;


import java.time.LocalDateTime;

/**
 * 任务查询请求DTO
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Data
public class TaskQueryRequest {
    
    private String taskName;
    private String taskType;
    private String publisherId;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;
    
    @Min(value = 1, message = "页大小必须大于0")
    @Max(value = 100, message = "页大小不能超过100")
    private Integer pageSize = 10;
} 