package com.origin.banyu.publisher.controller;

import com.origin.banyu.common.dto.ResultData;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.publisher.dto.*;
import com.origin.banyu.publisher.service.PublisherTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@RequiredArgsConstructor
@Tag(name = "任务管理", description = "任务发布、查询、状态管理等接口")
@Validated
public class PublisherTaskController {
    
    private final PublisherTaskService  publisherTaskService;
    
    @PostMapping("/core/publisher/create/task")
    @Operation(summary = "创建任务", description = "创建新任务")
    public ResultData<String> createTask(@RequestBody @Valid TaskCreateRequest request) {
        log.info("创建任务请求，参数：{}", request);
        String taskId = publisherTaskService.createTask(request);
        return ResultData.success("任务创建成功", taskId);
    }
    
    @PutMapping("/core/publisher/tasks/{taskId}")
    @Operation(summary = "更新任务", description = "更新任务信息")
    public ResultData<Void> updateTask(@PathVariable String taskId,
                                       @RequestBody @Valid TaskUpdateRequest request) {
        log.info("更新任务请求，任务ID：{}，参数：{}", taskId, request);
        publisherTaskService.updateTask(taskId, request);
        return ResultData.success("任务更新成功", null);
    }
    
    @GetMapping("/core/publisher/get/tasks/{taskId}")
    @Operation(summary = "获取任务详情", description = "获取任务详细信息")
    public ResultData<TaskDetailResponse> getTaskDetail(@PathVariable String taskId) {
        log.info("获取任务详情请求，任务ID：{}", taskId);
        TaskDetailResponse task = publisherTaskService.getTaskDetail(taskId);
        return ResultData.success("获取任务详情成功", task);
    }
    
    @GetMapping("/core/publisher/tasksList")
    @Operation(summary = "获取任务列表", description = "获取任务列表（包含完成人数统计）")
    public ResultData<IPage<TaskListResponse>> getTaskList(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(1000) Integer size,
            @RequestParam(required = false) Integer taskTypeId,
            @RequestParam(required = false) Integer statusId,
            @RequestParam(required = false) String keyword) {
        
        TaskListRequest request = new TaskListRequest();
        request.setPage(page);
        request.setSize(size);
        request.setTaskTypeId(taskTypeId);
        request.setStatusId(statusId);
        request.setKeyword(keyword);
        
        log.info("获取任务列表请求，参数：{}", request);
        IPage<TaskListResponse> result = publisherTaskService.getTaskList(request);
        return ResultData.success("获取任务列表成功", result);
    }
    
    @DeleteMapping("/core/publisher/tasks/{taskId}")
    @Operation(summary = "删除任务", description = "删除任务")
    public ResultData<Boolean> deleteTask(@PathVariable String taskId) {
        log.info("删除任务请求，任务ID：{}", taskId);
        publisherTaskService.deleteTask(taskId);
        return ResultData.success("任务删除成功", true);
    }
    
    @PutMapping("/core/publisher/tasks/{taskId}/status")
    @Operation(summary = "更新任务状态", description = "更新任务状态")
    public ResultData<Void> updateTaskStatus(@PathVariable String taskId,
                                            @RequestParam Integer statusId) {
        log.info("更新任务状态请求，任务ID：{}，状态ID：{}", taskId, statusId);
        publisherTaskService.updateTaskStatus(taskId, statusId);
        return ResultData.success("任务状态更新成功", null);
    }
    

} 