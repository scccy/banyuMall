package com.origin.publisher.service;

import com.origin.common.dto.PageResult;
import com.origin.publisher.dto.*;

/**
 * 任务管理服务接口
 * 作者: scccy
 * 创建时间: 2025-01-27
 */
public interface PublisherTaskService {
    
    /**
     * 创建任务
     * @param request 任务创建请求
     * @return 任务ID
     */
    String createTask(TaskCreateRequest request);
    
    /**
     * 更新任务
     * @param taskId 任务ID
     * @param request 任务更新请求
     */
    void updateTask(String taskId, TaskUpdateRequest request);
    
    /**
     * 获取任务详情
     * @param taskId 任务ID
     * @return 任务详情
     */
    TaskDetailResponse getTaskDetail(String taskId);
    
    /**
     * 获取任务列表
     * @param request 查询请求
     * @return 任务列表
     */
    PageResult<TaskListResponse> getTaskList(TaskListRequest request);
    
    /**
     * 删除任务
     * @param taskId 任务ID
     */
    void deleteTask(String taskId);
    
    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param statusId 状态ID
     */
    void updateTaskStatus(String taskId, Integer statusId);
    
    /**
     * 发布任务
     * @param taskId 任务ID
     */
    void publishTask(String taskId);
    
    /**
     * 下架任务
     * @param taskId 任务ID
     */
    void unpublishTask(String taskId);
} 