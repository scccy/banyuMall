package com.origin.publisher.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 任务列表查询请求DTO
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Data
public class TaskListRequest {
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;                 // 页码，默认1
    
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 1000, message = "每页大小不能超过1000")
    private Integer size = 10;                // 每页大小，默认10，最大1000
    
    private Integer taskTypeId;               // 任务类型ID过滤
    private Integer statusId;                 // 任务状态ID过滤
    private String keyword;                   // 任务名称关键词搜索
} 