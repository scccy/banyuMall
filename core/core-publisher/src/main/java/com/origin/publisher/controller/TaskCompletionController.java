package com.origin.publisher.controller;

import com.origin.common.dto.PageResult;
import com.origin.common.dto.ResultData;
import com.origin.publisher.dto.TaskCompletionRequest;
import com.origin.publisher.dto.TaskCompletionResponse;
import com.origin.publisher.service.TaskCompletionService;
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
 * 任务完成管理控制器
 * 作者: scccy
 * 创建时间: 2025-01-27
 */
@Slf4j
@RestController
@RequestMapping("/core/publisher")
@RequiredArgsConstructor
@Tag(name = "任务完成管理", description = "任务完成提交、查询、审核等接口")
@Validated
public class TaskCompletionController {
    
    private final TaskCompletionService taskCompletionService;
    
    @PostMapping("/tasks/{taskId}/complete")
    @Operation(summary = "提交任务完成", description = "提交任务完成申请")
    public ResultData<String> submitTaskCompletion(@PathVariable String taskId,
                                                   @RequestBody @Valid TaskCompletionRequest request) {
        log.info("提交任务完成请求，任务ID：{}，参数：{}", taskId, request);
        String completionId = taskCompletionService.submitTaskCompletion(taskId, request);
        return ResultData.ok("任务完成提交成功", completionId);
    }
    
    @GetMapping("/tasks/{taskId}/completions")
    @Operation(summary = "获取任务完成列表", description = "获取指定任务的完成记录列表")
    public ResultData<PageResult<TaskCompletionResponse>> getTaskCompletionList(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小必须大于0") @Max(value = 1000, message = "每页大小不能超过1000") Integer size) {
        log.info("获取任务完成列表请求，任务ID：{}，页码：{}，大小：{}", taskId, page, size);
        PageResult<TaskCompletionResponse> result = taskCompletionService.getTaskCompletionList(taskId, page, size);
        return ResultData.ok("获取任务完成列表成功", result);
    }
    
    @PutMapping("/completions/{completionId}/review")
    @Operation(summary = "审核任务完成", description = "审核任务完成申请")
    public ResultData<Void> reviewTaskCompletion(@PathVariable String completionId,
                                                 @RequestParam Integer reviewStatus,
                                                 @RequestParam(required = false) String comment) {
        log.info("审核任务完成请求，完成记录ID：{}，审核状态：{}，审核意见：{}", completionId, reviewStatus, comment);
        taskCompletionService.reviewTaskCompletion(completionId, reviewStatus, comment);
        return ResultData.ok("任务完成审核成功");
    }
    
    @PostMapping("/tasks/{taskId}/check-completion")
    @Operation(summary = "检查任务完成状态", description = "检查指定用户的任务完成状态")
    public ResultData<Void> checkTaskCompletion(@PathVariable String taskId,
                                                @RequestParam String userId) {
        log.info("检查任务完成状态请求，任务ID：{}，用户ID：{}", taskId, userId);
        taskCompletionService.checkTaskCompletion(taskId, userId);
        return ResultData.ok("任务完成状态检查完成");
    }
    
    @PostMapping("/wechatwork/callback")
    @Operation(summary = "企业微信回调处理", description = "处理企业微信任务完成状态回调")
    public ResultData<Void> processWechatWorkCallback(@RequestParam String taskId,
                                                      @RequestParam String userId,
                                                      @RequestParam String status) {
        log.info("企业微信回调处理请求，任务ID：{}，用户ID：{}，状态：{}", taskId, userId, status);
        taskCompletionService.processWechatWorkCallback(taskId, userId, status);
        return ResultData.ok("企业微信回调处理成功");
    }
} 