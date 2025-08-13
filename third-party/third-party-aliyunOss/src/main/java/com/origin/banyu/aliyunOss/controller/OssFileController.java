package com.origin.banyu.aliyunOss.controller;

import com.origin.banyu.aliyunOss.service.OssFileService;
import com.origin.banyu.common.dto.ResultData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * OSS文件上传控制器
 * 
 * @author scccy
 * @since 2025-07-31
 */
@Slf4j
@RestController
@RequestMapping("/tp/oss")
@Tag(name = "OSS文件上传", description = "阿里云OSS文件上传接口")
@Validated
public class OssFileController {

    @Autowired
    private OssFileService ossFileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传文件到阿里云OSS并返回访问地址")
    public ResultData<String> uploadFile(
            @Parameter(description = "上传的文件") @RequestPart("file") MultipartFile file) {
        // 移除try-catch，让异常传播到全局异常处理器
        String accessUrl = ossFileService.uploadFile(file);
        return ResultData.success("文件上传成功", accessUrl);
    }
} 