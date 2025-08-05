package com.origin.publisher.controller;

import com.origin.common.dto.ResultData;
import com.origin.common.dto.PageResult;
import com.origin.publisher.dto.*;
import com.origin.publisher.service.PublisherTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 任务管理控制器
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Slf4j
@RestController
@RequestMapping("/core/publisher/tasks")
@RequiredArgsConstructor
@Tag(name = "任务管理", description = "任务发布、查询、状态管理等接口")
@Validated
public class PublisherTaskController {
    
    private final PublisherTaskService taskService;
    
    @PostMapping
    @Operation(summary = "创建任务", description = "创建新任务")
    public ResultData<String> createTask(@RequestBody @Valid TaskCreateRequest request) {
        log.info("创建任务请求，参数：{}", request);
        String taskId = taskService.createTask(request);
        return ResultData.ok("任务创建成功", taskId);
    }
    
    @PutMapping("/{taskId}")
    @Operation(summary = "更新任务", description = "更新任务信息")
    public ResultData<Void> updateTask(@PathVariable String taskId,
                                       @RequestBody @Valid TaskUpdateRequest request) {
        log.info("更新任务请求，任务ID：{}，参数：{}", taskId, request);
        taskService.updateTask(taskId, request);
        return ResultData.ok("任务更新成功");
    }
    
    @GetMapping("/{taskId}")
    @Operation(summary = "获取任务详情", description = "获取任务详细信息")
    public ResultData<TaskDetailResponse> getTaskDetail(@PathVariable String taskId) {
        log.info("获取任务详情请求，任务ID：{}", taskId);
        TaskDetailResponse task = taskService.getTaskDetail(taskId);
        return ResultData.ok("获取任务详情成功", task);
    }
    
    @GetMapping
    @Operation(summary = "获取任务列表", description = "获取任务列表（包含完成人数统计）")
    public ResultData<PageResult<TaskListResponse>> getTaskList(@Valid TaskListRequest request) {
        log.info("获取任务列表请求，参数：{}", request);
        PageResult<TaskListResponse> result = taskService.getTaskList(request);
        return ResultData.ok("获取任务列表成功", result);
    }
    
    @DeleteMapping("/{taskId}")
    @Operation(summary = "删除任务", description = "删除任务")
    public ResultData<Boolean> deleteTask(@PathVariable String taskId) {
        log.info("删除任务请求，任务ID：{}", taskId);
        taskService.deleteTask(taskId);
        return ResultData.ok("任务删除成功", true);
    }
    
    @PutMapping("/{taskId}/status")
    @Operation(summary = "更新任务状态", description = "更新任务状态")
    public ResultData<Void> updateTaskStatus(@PathVariable String taskId,
                                            @RequestParam Integer statusId) {
        log.info("更新任务状态请求，任务ID：{}，状态ID：{}", taskId, statusId);
        taskService.updateTaskStatus(taskId, statusId);
        return ResultData.ok("任务状态更新成功");
    }
    
    @PostMapping("/{taskId}/publish")
    @Operation(summary = "发布任务", description = "发布任务")
    public ResultData<Void> publishTask(@PathVariable String taskId) {
        log.info("发布任务请求，任务ID：{}", taskId);
        taskService.publishTask(taskId);
        return ResultData.ok("任务发布成功");
    }
    
    @PostMapping("/{taskId}/unpublish")
    @Operation(summary = "下架任务", description = "下架任务")
    public ResultData<Void> unpublishTask(@PathVariable String taskId) {
        log.info("下架任务请求，任务ID：{}", taskId);
        taskService.unpublishTask(taskId);
        return ResultData.ok("任务下架成功");
    }
} 