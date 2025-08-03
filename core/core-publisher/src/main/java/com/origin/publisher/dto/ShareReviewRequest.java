package com.origin.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分享审核请求DTO
 * 作者: scccy
 * 创建时间: 2025-01-27
 */
@Data
public class ShareReviewRequest {
    
    @NotBlank(message = "任务ID不能为空")
    private String taskId;                    // 任务ID
    
    @NotBlank(message = "分享内容不能为空")
    private String shareContent;              // 分享内容
    
    @NotBlank(message = "分享平台不能为空")
    private String sharePlatform;             // 分享平台
    
    private String shareUrl;                  // 分享链接
    
    private String screenshotUrl;             // 截图URL
} 