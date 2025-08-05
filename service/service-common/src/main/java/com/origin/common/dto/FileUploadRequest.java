package com.origin.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传请求DTO
 * 
 * @author scccy
 * @since 2025-07-31
 */
@Data
public class FileUploadRequest {

    /**
     * 上传的文件
     */
    @NotNull(message = "文件不能为空")
    private MultipartFile file;

    /**
     * 来源服务(如:core-publisher)
     */
    @NotBlank(message = "来源服务不能为空")
    private String sourceService;

    /**
     * 业务类型(如:task-image)
     */
    @NotBlank(message = "业务类型不能为空")
    private String businessType;

    /**
     * 文件路径(如:core-publisher/task-image/2025/08/01/)
     */
    @NotBlank(message = "文件路径不能为空")
    private String filePath;

    /**
     * 上传用户ID
     */
    private Long uploadUserId;

    /**
     * 上传用户名
     */
    private String uploadUserName;
} 