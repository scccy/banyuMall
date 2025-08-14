package com.origin.banyu.publisher.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.banyu.base.service.BaseService;
import com.origin.banyu.common.dto.CompletionDetailResponseDto;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.exception.BusinessException;
import com.origin.banyu.publisher.dto.payload.NormalDetailPayload;
import com.origin.banyu.publisher.dto.request.CompletionsGetDetailsRequestDto;
import com.origin.banyu.publisher.dto.request.TaskCompletionRequest;
import com.origin.banyu.publisher.dto.response.TaskCompletionResponse;
import com.origin.banyu.publisher.entity.PublisherTask;
import com.origin.banyu.publisher.entity.PublisherTaskCompletion;
import com.origin.banyu.publisher.entity.PublisherTaskDetail;
import com.origin.banyu.publisher.feign.UserFeignClient;
import com.origin.banyu.publisher.mapper.PublisherTaskCompletionMapper;
import com.origin.banyu.publisher.mapper.PublisherTaskDetailMapper;
import com.origin.banyu.publisher.mapper.PublisherTaskMapper;
import com.origin.banyu.publisher.service.PublisherTaskCompletionService;
import com.origin.banyu.publisher.util.TaskValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 任务完成管理服务实现类
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PublisherTaskCompletionServiceImpl extends BaseService implements PublisherTaskCompletionService {
    
    private final PublisherTaskCompletionMapper taskCompletionMapper;
    private final PublisherTaskMapper taskMapper;
    private final PublisherTaskDetailMapper taskDetailMapper;
    private final TaskValidator taskValidator;
    private final UserFeignClient userFeignClient;
    @Override
    public PublisherTask getTaskById(String taskId){
        return taskMapper.selectById(taskId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitTaskCompletion(String taskId, TaskCompletionRequest request) {
        log.info("提交任务完成，任务ID：{}，请求参数：{}", taskId, request);
        
        // 验证任务是否存在
        PublisherTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_NOT_FOUND);
        }
        
        // 验证任务状态
        if (task.getStatusId() != 2) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_STATUS_INVALID, "只有上架状态的任务才能提交完成");
        }
        
        // 检查是否已经完成过
        LambdaQueryWrapper<PublisherTaskCompletion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PublisherTaskCompletion::getTaskId, taskId)
               .eq(PublisherTaskCompletion::getUserId, request.getUserId())
               .eq(PublisherTaskCompletion::getDeleted, false);
        
        if (taskCompletionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_ALREADY_COMPLETED, "该任务已经完成过");
        }
        
        // 创建完成记录
        PublisherTaskCompletion completion = new PublisherTaskCompletion();
        completion.setTaskId(taskId);
        completion.setUserId(request.getUserId());
        completion.setCompletionStatus(1); // 进行中状态
        completion.setRewardAmount(task.getTaskReward());
        
        // 如果是社群分享任务，需要保存完成证据（completion_evidence）
        if (task.getTaskTypeId() == 4 && request.getEvidence() != null) {
            completion.setCompletionEvidence(request.getEvidence().toString());
        }

        // 如果是邀请任务，需要保存结构化完成详情（completion_detail）
        if (task.getTaskTypeId() == 5 && request.getEvidence() != null) {
            // 期望字段：invitedUserId, invitedWechatId, invitedWechatNickname, inviteStartTime
            // 统一结构化为：
            // {
            //   "invitedUser": {"userId":"...","wechatId":"...","wechatNickname":"..."},
            //   "inviteStartTime":"..."
            // }
            java.util.Map<String, Object> evidence = request.getEvidence();
            String invitedUserId = evidence.getOrDefault("invitedUserId", "").toString();
            String invitedWechatId = evidence.getOrDefault("invitedWechatId", "").toString();
            String invitedWechatNickname = evidence.getOrDefault("invitedWechatNickname", "").toString();
            String inviteStartTime = evidence.getOrDefault("inviteStartTime", "").toString();

            String detailJson = String.format(
                "{\"invitedUser\":{\"userId\":\"%s\",\"wechatId\":\"%s\",\"wechatNickname\":\"%s\"},\"inviteStartTime\":\"%s\"}",
                invitedUserId, invitedWechatId, invitedWechatNickname, inviteStartTime
            );
            completion.setCompletionDetail(detailJson);
        }
        
        taskCompletionMapper.insert(completion);
        
        log.info("任务完成提交成功，完成记录ID：{}", completion.getCompletionId());
        return completion.getCompletionId();
    }
    
    @Override
    public IPage<TaskCompletionResponse> getTaskCompletionList(String taskId, Integer page, Integer size) {
        log.info("获取任务完成列表，任务ID：{}，页码：{}，大小：{}", taskId, page, size);
        
        // 验证任务是否存在
        PublisherTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_NOT_FOUND);
        }
        
        // 构建查询条件
        LambdaQueryWrapper<PublisherTaskCompletion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PublisherTaskCompletion::getTaskId, taskId)
               .orderByDesc(PublisherTaskCompletion::getCreatedTime);
        
        // 分页查询
        Page<PublisherTaskCompletion> pageParam = new Page<>(page, size);
        IPage<PublisherTaskCompletion> result = taskCompletionMapper.selectPage(pageParam, wrapper);
        
        // 使用基础类的方法构建分页响应
        return buildPageResponse(result, this::convertToResponse);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewTaskCompletion(String completionId, Integer reviewStatus, String comment) {
        log.info("审核任务完成，完成记录ID：{}，审核状态：{}，审核意见：{}", completionId, reviewStatus, comment);
        
        // 验证完成记录是否存在
        PublisherTaskCompletion completion = taskCompletionMapper.selectById(completionId);
        if (completion == null) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_COMPLETION_NOT_FOUND);
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
    public IPage<CompletionDetailResponseDto> completionsGetDetails(CompletionsGetDetailsRequestDto request) {
        log.info("获取任务完成详情，请求参数：{}", request);
        
        // 验证任务是否存在
        PublisherTask task = taskMapper.selectById(request.getTaskId());
        if (task == null) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_NOT_FOUND);
        }
        
        // 根据任务类型查询不同的数据
        switch (task.getTaskTypeId()) {
            case 1, 2, 3, 4, 6 -> {
                // 普通任务详情查询（服务层解析 completion_detail JSON）
                Page<PublisherTaskCompletion> pageParam = new Page<>(request.getPage(), request.getSize());
                LambdaQueryWrapper<PublisherTaskCompletion> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(PublisherTaskCompletion::getTaskId, request.getTaskId())
                       .eq(PublisherTaskCompletion::getDeleted, 0)
                       .orderByDesc(PublisherTaskCompletion::getCompletionTime);

                IPage<PublisherTaskCompletion> entityPage = taskCompletionMapper.selectPage(pageParam, wrapper);

                IPage<CompletionDetailResponseDto> page = new Page<>(request.getPage(), request.getSize());
                List<CompletionDetailResponseDto> responseList = entityPage.getRecords().stream()
                    .map(record -> {
                        CompletionDetailResponseDto response = new CompletionDetailResponseDto();
                        CompletionDetailResponseDto.NormalTaskDetail detail = new CompletionDetailResponseDto.NormalTaskDetail();

                        detail.setUserId(record.getUserId());
                        detail.setTaskReward(record.getRewardAmount());
                        detail.setCompletionStatus(record.getCompletionStatus());
                        detail.setCompletionTime(record.getCompletionTime());

                        String wechatNicknameVal = null;
                        try {
                            if (record.getCompletionDetail() != null && !record.getCompletionDetail().isEmpty()) {
                                NormalDetailPayload payload = JSON.parseObject(record.getCompletionDetail(), NormalDetailPayload.class);
                                if (payload != null) {
                                    wechatNicknameVal = payload.resolveWechatNickname();
                                }
                            }
                        } catch (Exception ignore) { }
                        detail.setWechatNickname(wechatNicknameVal);

                        response.setNormalTaskDetail(detail);
                        return response;
                    })
                    .toList();

                page.setRecords(responseList);
                page.setTotal(entityPage.getTotal());
                return page;
            }
            case 5 -> {
                // 邀请任务详情查询（解析 completion_detail JSON，避免复杂 SQL）
                Page<PublisherTaskCompletion> pageParam = new Page<>(request.getPage(), request.getSize());
                LambdaQueryWrapper<PublisherTaskCompletion> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(PublisherTaskCompletion::getTaskId, request.getTaskId())
                       .eq(PublisherTaskCompletion::getDeleted, 0)
                       .orderByDesc(PublisherTaskCompletion::getCompletionTime);

                IPage<PublisherTaskCompletion> entityPage = taskCompletionMapper.selectPage(pageParam, wrapper);

                IPage<CompletionDetailResponseDto> page = new Page<>(request.getPage(), request.getSize());
                List<CompletionDetailResponseDto> responseList = entityPage.getRecords().stream()
                    .map(record -> {
                        CompletionDetailResponseDto response = new CompletionDetailResponseDto();
                        CompletionDetailResponseDto.InviteTaskDetail detail;
                        try {
                            if (record.getCompletionDetail() != null && !record.getCompletionDetail().isEmpty()) {
                                detail = JSON.parseObject(record.getCompletionDetail(), CompletionDetailResponseDto.InviteTaskDetail.class);
                            } else {
                                detail = new CompletionDetailResponseDto.InviteTaskDetail();
                            }
                        } catch (Exception ignore) {
                            detail = new CompletionDetailResponseDto.InviteTaskDetail();
                        }


                        response.setInviteTaskDetail(detail);
                        return response;
                    })
                    .toList();

                page.setRecords(responseList);
                page.setTotal(entityPage.getTotal());
                return page;
            }
            case 7 -> {
                // 排名任务详情查询
                String startDate, endDate;
                
                // 如果请求中已经包含了startDate和endDate，直接使用自定义时间范围
                if (request.getStarDate() != null && request.getEndData() != null) {
                    startDate = request.getStarDate();
                    endDate = request.getEndData();
                } else {
                    // 根据rankType确定时间范围
                    if (request.getRankType() == null) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "排名任务必须指定rankType或自定义时间范围");
                    }

                    switch (request.getRankType()) {
                        case 1 -> {
                            // 本周
                            startDate = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).toString();
                            endDate = LocalDateTime.now().with(java.time.DayOfWeek.SUNDAY).withHour(23).withMinute(59).withSecond(59).toString();
                        }
                        case 2 -> {
                            // 本月
                            startDate = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).toString();
                            endDate = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59).toString();
                        }
                        case 3 -> {
                            // 当年
                            startDate = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).toString();
                            endDate = LocalDateTime.now().withDayOfYear(LocalDateTime.now().toLocalDate().lengthOfYear()).withHour(23).withMinute(59).withSecond(59).toString();
                        }
                        case 4 -> {
                            // 自定义时间范围
                            if (request.getStarDate() == null || request.getEndData() == null) {
                                throw new BusinessException(ErrorCode.PARAMS_ERROR, "自定义时间范围必须指定startDate和endDate");
                            }
                            startDate = request.getStarDate();
                            endDate = request.getEndData();
                        }
                        default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的rankType值");
                    }
                }
                
                // 读取任务详情配置中的topCount，若不存在则默认50
                Integer topCount = 50;
                PublisherTaskDetail taskDetail = taskDetailMapper.selectByTaskId(request.getTaskId());
                if (taskDetail != null && taskDetail.getTaskConfig() != null) {
                    try {
                        JSONObject root = JSON.parseObject(taskDetail.getTaskConfig());
                        if (root != null && root.containsKey("topCount")) {
                            topCount = root.getIntValue("topCount");
                        }
                    } catch (Exception ignore) {
                    }
                }

                Page<CompletionDetailResponseDto.RankTaskDetail> pageParam = new Page<>(request.getPage(), request.getSize());
                IPage<CompletionDetailResponseDto.RankTaskDetail> rankDetailsPage = 
                    taskCompletionMapper.selectRankTaskDetailsPage(pageParam, request.getTaskId(), startDate, endDate, topCount);
                
                IPage<CompletionDetailResponseDto> page = new Page<>(request.getPage(), request.getSize());
                List<CompletionDetailResponseDto> responseList = rankDetailsPage.getRecords().stream()
                    .map(detail -> {
                        CompletionDetailResponseDto response = new CompletionDetailResponseDto();
                        response.setRankTaskDetail(detail);
                        return response;
                    })
                    .toList();
                page.setRecords(responseList);
                page.setTotal(rankDetailsPage.getTotal());
                return page;
            }
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的任务类型");
        }
    }



    private TaskCompletionResponse convertToResponse(PublisherTaskCompletion completion) {
        TaskCompletionResponse response = new TaskCompletionResponse();
        BeanUtils.copyProperties(completion, response);
        
        // 这里应该将JSON字符串转换为Map，暂时使用简单处理
        if (completion.getCompletionEvidence() != null) {
            response.setCompletionEvidence(Map.of("evidence", completion.getCompletionEvidence()));
        }
        
        return response;
    }
    


} 
