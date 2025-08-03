package com.origin.publisher.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.common.dto.PageResult;
import com.origin.common.entity.ErrorCode;
import com.origin.common.exception.BusinessException;
import com.origin.publisher.dto.TaskCompletionRequest;
import com.origin.publisher.dto.TaskCompletionResponse;
import com.origin.publisher.entity.PublisherTask;
import com.origin.publisher.entity.PublisherTaskCompletion;
import com.origin.publisher.mapper.PublisherTaskCompletionMapper;
import com.origin.publisher.mapper.PublisherTaskMapper;
import com.origin.publisher.service.TaskCompletionService;
import com.origin.publisher.util.TaskValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务完成管理服务实现类
 * 作者: scccy
 * 创建时间: 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskCompletionServiceImpl implements TaskCompletionService {
    
    private final PublisherTaskCompletionMapper taskCompletionMapper;
    private final PublisherTaskMapper taskMapper;
    private final TaskValidator taskValidator;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitTaskCompletion(String taskId, TaskCompletionRequest request) {
        log.info("提交任务完成，任务ID：{}，请求参数：{}", taskId, request);
        
        // 验证任务是否存在
        PublisherTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        
        // 验证任务状态
        if (task.getStatusId() != 2) {
            throw new BusinessException(ErrorCode.TASK_STATUS_INVALID, "只有上架状态的任务才能提交完成");
        }
        
        // 检查是否已经完成过
        LambdaQueryWrapper<PublisherTaskCompletion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PublisherTaskCompletion::getTaskId, taskId)
               .eq(PublisherTaskCompletion::getUserId, request.getUserId())
               .eq(PublisherTaskCompletion::getDeleted, false);
        
        if (taskCompletionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.TASK_ALREADY_COMPLETED, "该任务已经完成过");
        }
        
        // 创建完成记录
        PublisherTaskCompletion completion = new PublisherTaskCompletion();
        completion.setTaskId(taskId);
        completion.setUserId(request.getUserId());
        completion.setCompletionStatus(1); // 进行中状态
        completion.setRewardAmount(task.getTaskReward());
        
        // 如果是社群分享任务，需要保存完成证据
        if (task.getTaskTypeId() == 4 && request.getEvidence() != null) {
            // 这里应该将Map转换为JSON字符串，暂时使用简单处理
            completion.setCompletionEvidence(request.getEvidence().toString());
        }
        
        taskCompletionMapper.insert(completion);
        
        log.info("任务完成提交成功，完成记录ID：{}", completion.getCompletionId());
        return completion.getCompletionId();
    }
    
    @Override
    public PageResult<TaskCompletionResponse> getTaskCompletionList(String taskId, Integer page, Integer size) {
        log.info("获取任务完成列表，任务ID：{}，页码：{}，大小：{}", taskId, page, size);
        
        // 验证任务是否存在
        PublisherTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        
        // 构建查询条件
        LambdaQueryWrapper<PublisherTaskCompletion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PublisherTaskCompletion::getTaskId, taskId)
               .orderByDesc(PublisherTaskCompletion::getCreatedTime);
        
        // 分页查询
        Page<PublisherTaskCompletion> pageParam = new Page<>(page, size);
        Page<PublisherTaskCompletion> result = taskCompletionMapper.selectPage(pageParam, wrapper);
        
        // 转换为响应对象
        List<TaskCompletionResponse> responses = result.getRecords().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PageResult<>(responses, result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewTaskCompletion(String completionId, Integer reviewStatus, String comment) {
        log.info("审核任务完成，完成记录ID：{}，审核状态：{}，审核意见：{}", completionId, reviewStatus, comment);
        
        // 验证完成记录是否存在
        PublisherTaskCompletion completion = taskCompletionMapper.selectById(completionId);
        if (completion == null) {
            throw new BusinessException(ErrorCode.TASK_COMPLETION_NOT_FOUND);
        }
        
        // 验证审核状态
        if (!taskValidator.isValidCompletionStatus(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的完成状态");
        }
        
        // 更新完成状态
        completion.setCompletionStatus(reviewStatus);
        if (reviewStatus == 2) { // 已完成
            completion.setCompletionTime(LocalDateTime.now());
        }
        
        taskCompletionMapper.updateById(completion);
        
        log.info("任务完成审核成功，完成记录ID：{}，审核结果：{}", completionId, reviewStatus);
    }
    
    @Override
    public void checkTaskCompletion(String taskId, String userId) {
        log.info("检查任务完成状态，任务ID：{}，用户ID：{}", taskId, userId);
        
        // 这里应该调用企业微信API检查任务完成状态
        // 暂时使用简单的逻辑判断
        
        // 查询完成记录
        LambdaQueryWrapper<PublisherTaskCompletion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PublisherTaskCompletion::getTaskId, taskId)
               .eq(PublisherTaskCompletion::getUserId, userId)
               .eq(PublisherTaskCompletion::getDeleted, false);
        
        PublisherTaskCompletion completion = taskCompletionMapper.selectOne(wrapper);
        if (completion != null && completion.getCompletionStatus() == 1) {
            // 如果存在进行中的记录，可以在这里调用企业微信API进行状态检查
            log.info("发现进行中的任务完成记录，需要调用企业微信API检查状态");
        }
    }
    
    @Override
    public void processWechatWorkCallback(String taskId, String userId, String status) {
        log.info("处理企业微信回调，任务ID：{}，用户ID：{}，状态：{}", taskId, userId, status);
        
        // 查询完成记录
        LambdaQueryWrapper<PublisherTaskCompletion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PublisherTaskCompletion::getTaskId, taskId)
               .eq(PublisherTaskCompletion::getUserId, userId)
               .eq(PublisherTaskCompletion::getDeleted, false);
        
        PublisherTaskCompletion completion = taskCompletionMapper.selectOne(wrapper);
        if (completion == null) {
            log.warn("未找到对应的任务完成记录，任务ID：{}，用户ID：{}", taskId, userId);
            return;
        }
        
        // 根据企业微信回调状态更新完成状态
        Integer completionStatus;
        switch (status) {
            case "completed":
                completionStatus = 2; // 已完成
                break;
            case "failed":
                completionStatus = 3; // 已拒绝
                break;
            default:
                log.warn("未知的企业微信回调状态：{}", status);
                return;
        }
        
        completion.setCompletionStatus(completionStatus);
        if (completionStatus == 2) {
            completion.setCompletionTime(LocalDateTime.now());
        }
        
        taskCompletionMapper.updateById(completion);
        
        log.info("企业微信回调处理成功，完成记录ID：{}，状态：{}", completion.getCompletionId(), completionStatus);
    }
    
    private TaskCompletionResponse convertToResponse(PublisherTaskCompletion completion) {
        TaskCompletionResponse response = new TaskCompletionResponse();
        BeanUtils.copyProperties(completion, response);
        
        // 这里应该将JSON字符串转换为Map，暂时使用简单处理
        if (completion.getCompletionEvidence() != null) {
            // 简单的字符串处理，实际应该使用JSON解析
            response.setCompletionEvidence(Map.of("evidence", completion.getCompletionEvidence()));
        }
        
        return response;
    }
} 