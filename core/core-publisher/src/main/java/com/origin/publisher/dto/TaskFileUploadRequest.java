package com.origin.publisher.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

/**
 * 任务文件上传请求DTO
 * 
 * @author scccy
 * @since 2025-08-01
 */
@Data
@Accessors(chain = true)
public class TaskFileUploadRequest {
    
    /**
     * 任务ID
     */
    @NotNull(message = "任务ID不能为空")
    private String taskId;
    
    /**
     * 文件类型：qr_code-二维码，task_avatar-任务头像
     */
    @NotNull(message = "文件类型不能为空")
    private String fileType;
    
    /**
     * 上传的文件
     */
    @NotNull(message = "文件不能为空")
    private MultipartFile file;
} 