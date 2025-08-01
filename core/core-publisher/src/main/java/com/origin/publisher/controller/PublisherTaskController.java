package com.origin.publisher.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import com.origin.common.dto.ResultData;
import com.origin.publisher.dto.TaskCreateRequest;
import com.origin.publisher.dto.TaskFileUploadResponse;
import com.origin.publisher.dto.TaskQueryRequest;
import com.origin.publisher.dto.TaskReviewRequest;
import com.origin.publisher.entity.PublisherTask;
import com.origin.publisher.entity.PublisherTaskReview;
import com.origin.publisher.service.PublisherTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * 任务管理控制器
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Slf4j
@RestController
@RequestMapping("/core/publisher/tasks")
@RequiredArgsConstructor
@Tag(name = "任务管理", description = "任务发布、审核、查询等接口")
@Validated
public class PublisherTaskController {
    
    private final PublisherTaskService taskService;
    
    @PostMapping
    @Operation(summary = "创建任务", description = "创建新的任务")
    public ResultData<PublisherTask> createTask(@RequestBody @Valid TaskCreateRequest request) {
        log.info("创建任务请求，参数：{}", request);
        PublisherTask task = taskService.createTask(request);
        return ResultData.ok("任务创建成功", task);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新任务", description = "更新任务信息")
    public ResultData<PublisherTask> updateTask(@PathVariable String id,
                                                @RequestBody @Valid TaskCreateRequest request) {
        log.info("更新任务请求，任务ID：{}，参数：{}", id, request);
        PublisherTask task = taskService.updateTask(id, request);
        return ResultData.ok("任务更新成功", task);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除任务", description = "删除任务")
    public ResultData<Boolean> deleteTask(@PathVariable String id) {
        log.info("删除任务请求，任务ID：{}", id);
        boolean result = taskService.deleteTask(id);
        return ResultData.ok("任务删除成功", result);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取任务详情", description = "获取任务详细信息")
    public ResultData<PublisherTask> getTaskById(@PathVariable String id) {
        log.info("获取任务详情请求，任务ID：{}", id);
        PublisherTask task = taskService.getTaskById(id);
        return ResultData.ok("获取任务详情成功", task);
    }
    
    @GetMapping
    @Operation(summary = "分页查询任务列表", description = "分页查询任务列表")
    public ResultData<IPage<PublisherTask>> queryTasks(TaskQueryRequest request) {
        log.info("查询任务列表请求，参数：{}", request);
        IPage<PublisherTask> result = taskService.queryTasks(request);
        return ResultData.ok("查询任务列表成功", result);
    }
    
    @PostMapping("/{id}/submit")
    @Operation(summary = "提交任务审核", description = "提交任务进行审核")
    public ResultData<Boolean> submitForReview(@PathVariable String id) {
        log.info("提交任务审核请求，任务ID：{}", id);
        boolean result = taskService.submitForReview(id);
        return ResultData.ok("任务提交审核成功", result);
    }
    
    @PostMapping("/{id}/review")
    @Operation(summary = "审核任务", description = "审核任务")
    public ResultData<Boolean> reviewTask(@PathVariable String id,
                                          @RequestBody @Valid TaskReviewRequest request) {
        log.info("审核任务请求，任务ID：{}，审核参数：{}", id, request);
        boolean result = taskService.reviewTask(id, request);
        return ResultData.ok("任务审核成功", result);
    }
    
    @GetMapping("/{id}/review-history")
    @Operation(summary = "获取审核历史", description = "获取任务审核历史")
    public ResultData<List<PublisherTaskReview>> getReviewHistory(@PathVariable String id) {
        log.info("获取审核历史请求，任务ID：{}", id);
        List<PublisherTaskReview> history = taskService.getReviewHistory(id);
        return ResultData.ok("获取审核历史成功", history);
    }
    
    @PostMapping("/{taskId}/upload-file")
    @Operation(summary = "上传任务文件", description = "上传任务二维码或任务头像")
    public ResultData<TaskFileUploadResponse> uploadTaskFile(
            @PathVariable String taskId,
            @RequestParam("fileType") String fileType,
            @RequestPart("file") org.springframework.web.multipart.MultipartFile file) {
        log.info("上传任务文件请求，任务ID：{}，文件类型：{}，文件名：{}", taskId, fileType, file.getOriginalFilename());
        
        try {
                    TaskFileUploadResponse response = taskService.uploadTaskFile(taskId, fileType, file);
        return ResultData.ok("文件上传成功", response);
        } catch (Exception e) {
            log.error("上传任务文件失败，任务ID：{}，文件类型：{}，错误：{}", taskId, fileType, e.getMessage(), e);
            return ResultData.fail("文件上传失败：" + e.getMessage());
        }
    }
} 