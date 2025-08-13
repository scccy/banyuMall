package com.origin.banyu.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 健康检查响应DTO
 * 
 * @author origin
 * @since 2025-08-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "健康检查响应")
public class HealthResponse {

    @Schema(description = "服务状态", example = "UP")
    private String status;

    @Schema(description = "服务名称", example = "service-auth")
    private String service;

    @Schema(description = "服务版本", example = "0.0.1-SNAPSHOT")
    private String version;

    @Schema(description = "检查时间", example = "2025-08-07T23:26:55")
    private LocalDateTime timestamp;

    @Schema(description = "服务描述", example = "认证服务运行正常")
    private String description;

    @Schema(description = "额外信息")
    private Object details;
} 