package com.origin.banyu.publisher.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.common.dto.CompletionDetailResponseDto;
import com.origin.banyu.publisher.dto.request.CompletionsGetDetailsRequestDto;
import com.origin.banyu.publisher.dto.request.TaskCompletionRequest;
import com.origin.banyu.publisher.dto.response.TaskCompletionResponse;

/**
 * 任务完成管理服务接口
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
public interface PublisherTaskCompletionService {
    
    /**
     * 提交任务完成
     * @param taskId 任务ID
     * @param request 完成请求
     * @return 完成记录ID
     */
    String submitTaskCompletion(String taskId, TaskCompletionRequest request);
    
    /**
     * 获取任务完成列表
     * @param taskId 任务ID
     * @param page 页码
     * @param size 每页大小
     * @return 完成列表
     */
    IPage<TaskCompletionResponse> getTaskCompletionList(String taskId, Integer page, Integer size);
    
    /**
     * 审核任务完成
     * @param completionId 完成记录ID
     * @param reviewStatus 审核状态
     * @param comment 审核意见
     */
    void reviewTaskCompletion(String completionId, Integer reviewStatus, String comment);


    IPage<CompletionDetailResponseDto> completionsGetDetails(CompletionsGetDetailsRequestDto request);

    // 为控制器封装外层元信息提供任务基本信息
    com.origin.banyu.publisher.entity.PublisherTask getTaskById(String taskId);
}