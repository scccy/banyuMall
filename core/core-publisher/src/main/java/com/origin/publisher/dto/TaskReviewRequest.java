package com.origin.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;



/**
 * 任务审核请求DTO
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Data
public class TaskReviewRequest {
    
    @NotBlank(message = "审核状态不能为空")
    private String reviewStatus;
    
    private String reviewComment;
    
    @NotBlank(message = "审核人ID不能为空")
    private String reviewerId;
} 