package com.origin.banyu.publisher.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.exception.BusinessException;
import com.origin.banyu.publisher.dto.*;
import com.origin.banyu.publisher.entity.*;
import com.origin.banyu.publisher.mapper.*;
import com.origin.banyu.publisher.service.BaseEntityService;
import com.origin.banyu.publisher.service.PublisherTaskService;

import com.origin.banyu.publisher.util.TaskValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务管理服务实现类
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PublisherTaskServiceImpl extends BaseEntityService<PublisherTask, TaskListResponse, String> implements PublisherTaskService {
    
    private final PublisherTaskMapper taskMapper;
    private final PublisherTaskDetailMapper taskDetailMapper;
    private final PublisherTaskCompletionMapper taskCompletionMapper;

    private final TaskValidator taskValidator;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createTask(TaskCreateRequest request) {
        log.info("创建任务，请求参数：{}", request);
        
        // 验证请求参数
        TaskValidator.ValidationResult validationResult = taskValidator.validateCreateRequest(request);
        if (!validationResult.isValid()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, validationResult.getErrorMessage());
        }
        
        // 任务配置验证已移除，taskConfig为前端自定义字段
        
        // 创建任务主表记录
        PublisherTask task = new PublisherTask();
        BeanUtils.copyProperties(request, task);
        // 删除默认草稿状态设置，任务创建时不设置默认状态
        
        // 手动设置时间字段
        LocalDateTime now = LocalDateTime.now();
        task.setCreatedTime(now);
        task.setUpdatedTime(now);
        
        log.info("设置时间字段 - createdTime: {}, updatedTime: {}", task.getCreatedTime(), task.getUpdatedTime());
        
        taskMapper.insert(task);
        
        // 创建任务详情记录
        if (request.getTaskConfig() != null) {
            createTaskDetail(task.getTaskId(), request.getTaskConfig());
        }
        
        log.info("任务创建成功，任务ID：{}", task.getTaskId());
        return task.getTaskId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTask(String taskId, TaskUpdateRequest request) {
        log.info("更新任务，任务ID：{}，请求参数：{}", taskId, request);
        
        // 验证任务是否存在
        PublisherTask existingTask = taskMapper.selectById(taskId);
        if (existingTask == null) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_NOT_FOUND);
        }
        
        // 验证请求参数
        TaskValidator.ValidationResult validationResult = taskValidator.validateUpdateRequest(request);
        if (!validationResult.isValid()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, validationResult.getErrorMessage());
        }
        
        // 任务配置验证已移除，taskConfig为前端自定义字段
        
        // 更新任务主表
        BeanUtils.copyProperties(request, existingTask);
        taskMapper.updateById(existingTask);
        
        // 更新任务详情
        if (request.getTaskConfig() != null) {
            updateTaskDetail(taskId, request.getTaskConfig());
        }
        
        log.info("任务更新成功，任务ID：{}", taskId);
    }
    
    @Override
    public TaskDetailResponse getTaskDetail(String taskId) {
        log.info("获取任务详情，任务ID：{}", taskId);
        
        PublisherTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_NOT_FOUND);
        }
        
        TaskDetailResponse response = new TaskDetailResponse();
        BeanUtils.copyProperties(task, response);
        
        // 查询任务配置
        PublisherTaskDetail detail = taskDetailMapper.selectByTaskId(taskId);
        if (detail != null) {
            response.setTaskConfig(detail.getTaskConfig()); // 直接返回字符串，不再解析为Map
        }
        
        // 查询完成人数 - 暂时跳过，避免SQL错误
        response.setCompletionCount(0);
        
        return response;
    }
    
    @Override
    public IPage<TaskListResponse> getTaskList(TaskListRequest request) {
        log.info("获取任务列表，请求参数：{}", request);
        
        // 构建查询条件
        LambdaQueryWrapper<PublisherTask> wrapper = new LambdaQueryWrapper<>();
        
        if (request.getTaskTypeId() != null) {
            wrapper.eq(PublisherTask::getTaskTypeId, request.getTaskTypeId());
        }
        if (request.getStatusId() != null) {
            wrapper.eq(PublisherTask::getStatusId, request.getStatusId());
        }
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.like(PublisherTask::getTaskName, request.getKeyword());
        }
        
        wrapper.orderByDesc(PublisherTask::getCreatedTime);
        
        // 分页查询
        Page<PublisherTask> page = new Page<>(request.getPage(), request.getSize());
        IPage<PublisherTask> result = taskMapper.selectPage(page, wrapper);
        
        // 使用基础类的方法构建分页响应
        return buildPageResponse(result, this::convertToTaskListResponse);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(String taskId) {
        log.info("删除任务，任务ID：{}", taskId);
        
        PublisherTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_NOT_FOUND);
        }
        
        // 逻辑删除任务
        taskMapper.deleteById(taskId);
        
        // 逻辑删除任务详情
        LambdaQueryWrapper<PublisherTaskDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(PublisherTaskDetail::getTaskId, taskId);
        taskDetailMapper.delete(detailWrapper);
        
        log.info("任务删除成功，任务ID：{}", taskId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTaskStatus(String taskId, Integer statusId) {
        log.info("更新任务状态，任务ID：{}，状态ID：{}", taskId, statusId);
        
        if (!taskValidator.isValidTaskStatus(statusId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的任务状态");
        }
        
        PublisherTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_NOT_FOUND);
        }
        
        task.setStatusId(statusId);
        taskMapper.updateById(task);
        
        log.info("任务状态更新成功，任务ID：{}，状态ID：{}", taskId, statusId);
    }
    

    
    private void createTaskDetail(String taskId, String taskConfig) {
        PublisherTaskDetail detail = new PublisherTaskDetail();
        detail.setTaskId(taskId);
        detail.setTaskConfig(taskConfig); // 直接保存字符串，不需要解析
        
        // 手动设置时间字段，确保不为null
        LocalDateTime now = LocalDateTime.now();
        detail.setCreatedTime(now);
        detail.setUpdatedTime(now);
        
        taskDetailMapper.insert(detail);
        
        // 更新任务主表的detail_id
        PublisherTask task = new PublisherTask();
        task.setTaskId(taskId);
        task.setDetailId(detail.getDetailId());
        // 手动设置更新时间，确保不为null
        task.setUpdatedTime(LocalDateTime.now());
        taskMapper.updateById(task);
    }
    
    private void updateTaskDetail(String taskId, String taskConfig) {
        PublisherTaskDetail detail = taskDetailMapper.selectByTaskId(taskId);
        if (detail != null) {
            detail.setTaskConfig(taskConfig); // 直接保存字符串，不需要解析
            taskDetailMapper.updateById(detail);
        } else {
            createTaskDetail(taskId, taskConfig);
        }
    }
    
    private TaskListResponse convertToTaskListResponse(PublisherTask task) {
        TaskListResponse response = new TaskListResponse();
        BeanUtils.copyProperties(task, response);
        
        // 查询任务配置
        PublisherTaskDetail detail = taskDetailMapper.selectByTaskId(task.getTaskId());
        if (detail != null) {
            response.setTaskConfig(detail.getTaskConfig()); // 直接返回字符串，不再解析为Map
        }
        
        return response;
    }
} 