package com.origin.banyu.publisher.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.publisher.dto.request.CompletionsGetDetailsRequestDto;
import com.origin.banyu.publisher.dto.response.CompletionDetailResponseDto;
import com.origin.banyu.publisher.dto.response.TaskCompletionResponse;
import com.origin.banyu.publisher.service.PublisherTaskCompletionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 任务完成管理控制器
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Slf4j
@RestController
@RequestMapping("/core/publisher")
@RequiredArgsConstructor
@Tag(name = "任务完成管理", description = "任务完成提交、查询、审核等接口")
@Validated
public class PiblisherTaskCompletionController {
    
    private final PublisherTaskCompletionService taskCompletionService;

    
    @GetMapping("/tasks/{taskId}/completions")
    @Operation(summary = "获取任务完成列表", description = "获取指定任务的完成记录列表")
    public ResultData<IPage<TaskCompletionResponse>> getTaskCompletionList(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小必须大于0") @Max(value = 1000, message = "每页大小不能超过1000") Integer size) {
        IPage<TaskCompletionResponse> result = taskCompletionService.getTaskCompletionList(taskId, page, size);
        return ResultData.success("获取任务完成列表成功", result);
    }
    
    @PutMapping("/completions/{completionId}/review")
    @Operation(summary = "审核任务完成", description = "审核任务完成申请")
    public ResultData<Void> reviewTaskCompletion(@PathVariable String completionId,
                                                 @RequestParam Integer reviewStatus,
                                                 @RequestParam(required = false) String comment) {
        taskCompletionService.reviewTaskCompletion(completionId, reviewStatus, comment);
        return ResultData.success("任务完成审核成功", null);
    }

    @PostMapping("/tasks/completions/get/details")
    @Operation(summary = "查询任务完成情况", description = "查询任务完成情况")
    public ResultData<Map<String, Object>> completionsGetDetails(@RequestBody CompletionsGetDetailsRequestDto request ) {
        IPage<CompletionDetailResponseDto> result= taskCompletionService.completionsGetDetails(request);
        java.util.Map<String, Object> wrapped = new java.util.HashMap<>();
        // 外层携带任务元信息
        var task = taskCompletionService.getTaskById(request.getTaskId());
        wrapped.put("taskName", task.getTaskName());
        wrapped.put("taskTypeId", task.getTaskTypeId());
        // 分页元信息
        wrapped.put("current", result.getCurrent());
        wrapped.put("size", result.getSize());
        wrapped.put("total", result.getTotal());
        wrapped.put("pages", result.getPages());
        // 仅返回具体数据记录
        wrapped.put("records", result.getRecords());
        return ResultData.success("任务完成提交成功", wrapped);
    }

} 