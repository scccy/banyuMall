package com.origin.banyu.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 任务完成请求DTO
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Data
public class TaskCompletionRequest {
    
    /**
     * 完成用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    /**
     * 完成证据（仅社群分享任务需要）
     */
    private Map<String, Object> evidence;
} 