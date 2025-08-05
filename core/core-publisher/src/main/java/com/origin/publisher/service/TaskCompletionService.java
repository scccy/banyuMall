package com.origin.publisher.service;

import com.origin.common.dto.PageResult;
import com.origin.publisher.dto.TaskCompletionRequest;
import com.origin.publisher.dto.TaskCompletionResponse;

/**
 * 任务完成管理服务接口
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
public interface TaskCompletionService {
    
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
    PageResult<TaskCompletionResponse> getTaskCompletionList(String taskId, Integer page, Integer size);
    
    /**
     * 审核任务完成
     * @param completionId 完成记录ID
     * @param reviewStatus 审核状态
     * @param comment 审核意见
     */
    void reviewTaskCompletion(String completionId, Integer reviewStatus, String comment);
    
    /**
     * 检查任务完成状态（自动判断）
     * @param taskId 任务ID
     * @param userId 用户ID
     */
    void checkTaskCompletion(String taskId, String userId);
    
    /**
     * 处理企业微信回调
     * @param taskId 任务ID
     * @param userId 用户ID
     * @param status 完成状态
     */
    void processWechatWorkCallback(String taskId, String userId, String status);
} 