package com.origin.oss.controller;

import com.origin.common.dto.ResultData;
import com.origin.oss.dto.FileUploadRequest;
import com.origin.oss.dto.FileUploadResponse;
import com.origin.oss.service.OssFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

/**
 * OSS文件上传控制器
 * 
 * @author scccy
 * @since 2025-01-27
 */
@Slf4j
@RestController
@RequestMapping("/third-party/oss")
@Tag(name = "OSS文件上传", description = "阿里云OSS文件上传接口")
@Validated
public class OssFileController {

    @Autowired
    private OssFileService ossFileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传文件到阿里云OSS并返回访问地址")
    public ResultData<FileUploadResponse> uploadFile(
            @Parameter(description = "上传的文件") @RequestPart("file") MultipartFile file,
            @Parameter(description = "来源服务") @RequestParam("sourceService") @NotBlank String sourceService,
            @Parameter(description = "业务类型") @RequestParam("businessType") @NotBlank String businessType,
            @Parameter(description = "文件路径") @RequestParam("filePath") @NotBlank String filePath,
            @Parameter(description = "上传用户ID") @RequestParam(value = "uploadUserId", required = false) Long uploadUserId,
            @Parameter(description = "上传用户名") @RequestParam(value = "uploadUserName", required = false) String uploadUserName) {
        
        try {
            FileUploadRequest request = new FileUploadRequest();
            request.setFile(file);
            request.setSourceService(sourceService);
            request.setBusinessType(businessType);
            request.setFilePath(filePath);
            request.setUploadUserId(uploadUserId);
            request.setUploadUserName(uploadUserName);
            
            FileUploadResponse response = ossFileService.uploadFile(request);
            return ResultData.ok("文件上传成功", response);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return ResultData.fail("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/url/{fileId}")
    @Operation(summary = "获取文件访问URL", description = "获取文件的访问URL")
    public ResultData<String> getFileAccessUrl(
            @Parameter(description = "文件ID") @PathVariable @NotNull Long fileId) {
        
        try {
            String accessUrl = ossFileService.getFileAccessUrl(fileId);
            return ResultData.ok("获取文件访问URL成功", accessUrl);
        } catch (Exception e) {
            log.error("获取文件访问URL失败", e);
            return ResultData.fail("获取文件访问URL失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/file/{fileId}")
    @Operation(summary = "删除文件", description = "删除指定的文件")
    public ResultData<Boolean> deleteFile(
            @Parameter(description = "文件ID") @PathVariable @NotNull Long fileId,
            @Parameter(description = "用户ID") @RequestParam @NotNull Long userId) {
        
        try {
            boolean result = ossFileService.deleteFile(fileId, userId);
            return ResultData.ok("文件删除成功", result);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return ResultData.fail("文件删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch-upload")
    @Operation(summary = "批量上传文件", description = "批量上传多个文件到阿里云OSS")
    public ResultData<List<FileUploadResponse>> batchUploadFiles(
            @Parameter(description = "上传的文件列表") @RequestPart("files") List<MultipartFile> files,
            @Parameter(description = "来源服务") @RequestParam("sourceService") @NotBlank String sourceService,
            @Parameter(description = "业务类型") @RequestParam("businessType") @NotBlank String businessType,
            @Parameter(description = "用户ID") @RequestParam @NotNull Long userId) {
        
        try {
            List<FileUploadResponse> responses = ossFileService.batchUploadFiles(files, sourceService, businessType, userId);
            return ResultData.ok("批量文件上传成功", responses);
        } catch (Exception e) {
            log.error("批量文件上传失败", e);
            return ResultData.fail("批量文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/generate-path")
    @Operation(summary = "生成文件路径", description = "根据来源服务和业务类型生成文件路径")
    public ResultData<String> generateFilePath(
            @Parameter(description = "来源服务") @RequestParam("sourceService") @NotBlank String sourceService,
            @Parameter(description = "业务类型") @RequestParam("businessType") @NotBlank String businessType) {
        
        try {
            String filePath = ossFileService.generateFilePath(sourceService, businessType);
            return ResultData.ok("生成文件路径成功", filePath);
        } catch (Exception e) {
            log.error("生成文件路径失败", e);
            return ResultData.fail("生成文件路径失败: " + e.getMessage());
        }
    }
} 