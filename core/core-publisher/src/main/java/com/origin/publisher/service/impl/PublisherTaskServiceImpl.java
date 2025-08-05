package com.origin.publisher.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.common.entity.ErrorCode;
import com.origin.common.exception.BusinessException;
import com.origin.publisher.dto.*;
import com.origin.publisher.entity.*;
import com.origin.publisher.mapper.*;
import com.origin.publisher.service.PublisherTaskService;
import com.origin.publisher.util.TaskConfigParser;
import com.origin.publisher.util.TaskValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
public class PublisherTaskServiceImpl implements PublisherTaskService {
    
    private final PublisherTaskMapper taskMapper;
    private final PublisherTaskDetailMapper taskDetailMapper;
    private final PublisherTaskCompletionMapper taskCompletionMapper;
    private final TaskConfigParser taskConfigParser;
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
        
        // 验证任务配置
        if (request.getTaskConfig() != null && !taskConfigParser.validateTaskConfig(request.getTaskTypeId(), request.getTaskConfig())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务配置格式错误");
        }
        
        // 创建任务主表记录
        PublisherTask task = new PublisherTask();
        BeanUtils.copyProperties(request, task);
        task.setStatusId(1); // 草稿状态
        
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
        
        // 验证任务配置
        if (request.getTaskConfig() != null && !taskConfigParser.validateTaskConfig(existingTask.getTaskTypeId(), request.getTaskConfig())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务配置格式错误");
        }
        
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
            response.setTaskConfig(taskConfigParser.parseFromJson(detail.getTaskConfig()));
        }
        
        // 查询完成人数
        Map<String, Integer> completionCountMap = taskCompletionMapper.selectCompletionCountByTaskIds(List.of(taskId));
        response.setCompletionCount(completionCountMap.getOrDefault(taskId, 0));
        
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
        
        // 转换为响应对象
        List<TaskListResponse> responses = result.getRecords().stream()
            .map(this::convertToTaskListResponse)
            .collect(Collectors.toList());
        
        // 批量查询完成人数统计
        List<String> taskIds = responses.stream()
            .map(TaskListResponse::getTaskId)
            .collect(Collectors.toList());
        
        if (!taskIds.isEmpty()) {
            Map<String, Integer> completionCountMap = taskCompletionMapper.selectCompletionCountByTaskIds(taskIds);
            responses.forEach(response -> 
                response.setCompletionCount(completionCountMap.getOrDefault(response.getTaskId(), 0))
            );
        }
        
        // 创建新的IPage对象返回
        Page<TaskListResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(responses);
        return responsePage;
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
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishTask(String taskId) {
        log.info("发布任务，任务ID：{}", taskId);
        
        PublisherTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_NOT_FOUND);
        }
        
        if (task.getStatusId() != 1) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_STATUS_INVALID, "只有草稿状态的任务才能发布");
        }
        
        task.setStatusId(2); // 上架状态
        taskMapper.updateById(task);
        
        log.info("任务发布成功，任务ID：{}", taskId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unpublishTask(String taskId) {
        log.info("下架任务，任务ID：{}", taskId);
        
        PublisherTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_NOT_FOUND);
        }
        
        if (task.getStatusId() != 2) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_STATUS_INVALID, "只有上架状态的任务才能下架");
        }
        
        task.setStatusId(3); // 下架状态
        taskMapper.updateById(task);
        
        log.info("任务下架成功，任务ID：{}", taskId);
    }
    
    private void createTaskDetail(String taskId, Map<String, Object> taskConfig) {
        PublisherTaskDetail detail = new PublisherTaskDetail();
        detail.setTaskId(taskId);
        detail.setTaskConfig(taskConfigParser.parseToJson(taskConfig));
        
        taskDetailMapper.insert(detail);
        
        // 更新任务主表的detail_id
        PublisherTask task = new PublisherTask();
        task.setTaskId(taskId);
        task.setDetailId(detail.getDetailId());
        taskMapper.updateById(task);
    }
    
    private void updateTaskDetail(String taskId, Map<String, Object> taskConfig) {
        PublisherTaskDetail detail = taskDetailMapper.selectByTaskId(taskId);
        if (detail != null) {
            detail.setTaskConfig(taskConfigParser.parseToJson(taskConfig));
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
            response.setTaskConfig(taskConfigParser.parseFromJson(detail.getTaskConfig()));
        }
        
        return response;
    }
} 