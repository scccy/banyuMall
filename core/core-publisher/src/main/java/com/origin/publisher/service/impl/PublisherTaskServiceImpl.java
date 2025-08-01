package com.origin.publisher.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.origin.common.dto.ResultData;
import com.origin.common.entity.ErrorCode;
import com.origin.common.exception.BusinessException;
import com.origin.oss.dto.FileUploadRequest;
import com.origin.oss.dto.FileUploadResponse;
import com.origin.publisher.dto.TaskCreateRequest;
import com.origin.publisher.dto.TaskFileUploadResponse;
import com.origin.publisher.dto.TaskQueryRequest;
import com.origin.publisher.dto.TaskReviewRequest;
import com.origin.publisher.entity.*;
import com.origin.publisher.feign.OssFileFeignClient;
import com.origin.publisher.mapper.*;
import com.origin.publisher.service.PublisherTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务服务实现类
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PublisherTaskServiceImpl implements PublisherTaskService {
    
    private final PublisherTaskMapper taskMapper;
    private final PublisherLikeTaskMapper likeTaskMapper;
    private final PublisherCommentTaskMapper commentTaskMapper;
    private final PublisherShareTaskMapper shareTaskMapper;
    private final PublisherDiscussTaskMapper discussTaskMapper;
    private final PublisherInviteTaskMapper inviteTaskMapper;
    private final PublisherFeedbackTaskMapper feedbackTaskMapper;
    private final PublisherRankingTaskMapper rankingTaskMapper;
    private final PublisherTaskReviewMapper taskReviewMapper;
    private final OssFileFeignClient ossFileFeignClient;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PublisherTask createTask(TaskCreateRequest request) {
        log.info("创建任务，请求参数：{}", request);
        
        // 创建基础任务
        PublisherTask task = new PublisherTask();
        BeanUtils.copyProperties(request, task);
        task.setStatus("DRAFT");
        task.setCurrentParticipants(0);
        
        taskMapper.insert(task);
        
        // 根据任务类型创建对应的任务详情
        createTaskDetail(task.getId(), request);
        
        log.info("任务创建成功，任务ID：{}", task.getId());
        return task;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PublisherTask updateTask(String id, TaskCreateRequest request) {
        log.info("更新任务，任务ID：{}，请求参数：{}", id, request);
        
        PublisherTask existingTask = taskMapper.selectById(id);
        if (existingTask == null) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        
        // 更新基础任务信息
        BeanUtils.copyProperties(request, existingTask);
        taskMapper.updateById(existingTask);
        
        // 更新任务详情
        updateTaskDetail(id, request);
        
        log.info("任务更新成功，任务ID：{}", id);
        return existingTask;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTask(String id) {
        log.info("删除任务，任务ID：{}", id);
        
        PublisherTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        
        // 删除任务详情
        deleteTaskDetail(id);
        
        // 删除基础任务
        int result = taskMapper.deleteById(id);
        
        log.info("任务删除成功，任务ID：{}", id);
        return result > 0;
    }
    
    @Override
    public PublisherTask getTaskById(String id) {
        log.info("获取任务详情，任务ID：{}", id);
        
        PublisherTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        
        return task;
    }
    
    @Override
    public IPage<PublisherTask> queryTasks(TaskQueryRequest request) {
        log.info("查询任务列表，请求参数：{}", request);
        
        LambdaQueryWrapper<PublisherTask> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(request.getTaskName())) {
            wrapper.like(PublisherTask::getTaskName, request.getTaskName());
        }
        if (StringUtils.hasText(request.getTaskType())) {
            wrapper.eq(PublisherTask::getTaskType, request.getTaskType());
        }
        if (StringUtils.hasText(request.getPublisherId())) {
            wrapper.eq(PublisherTask::getPublisherId, request.getPublisherId());
        }
        if (StringUtils.hasText(request.getStatus())) {
            wrapper.eq(PublisherTask::getStatus, request.getStatus());
        }
        if (request.getStartTime() != null) {
            wrapper.ge(PublisherTask::getStartTime, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            wrapper.le(PublisherTask::getEndTime, request.getEndTime());
        }
        
        wrapper.orderByDesc(PublisherTask::getCreatedTime);
        
        Page<PublisherTask> page = new Page<>(request.getPageNum(), request.getPageSize());
        return taskMapper.selectPage(page, wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitForReview(String id) {
        log.info("提交任务审核，任务ID：{}", id);
        
        PublisherTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        
        if (!"DRAFT".equals(task.getStatus())) {
            throw new BusinessException(ErrorCode.TASK_STATUS_INVALID, "只有草稿状态的任务才能提交审核");
        }
        
        task.setStatus("SUBMITTED");
        taskMapper.updateById(task);
        
        log.info("任务提交审核成功，任务ID：{}", id);
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewTask(String id, TaskReviewRequest request) {
        log.info("审核任务，任务ID：{}，审核请求：{}", id, request);
        
        PublisherTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        
        if (!"SUBMITTED".equals(task.getStatus()) && !"REVIEWING".equals(task.getStatus())) {
            throw new BusinessException(ErrorCode.TASK_STATUS_INVALID, "只有已提交或审核中的任务才能审核");
        }
        
        // 创建审核记录
        PublisherTaskReview review = new PublisherTaskReview();
        review.setTaskId(id);
        review.setReviewerId(request.getReviewerId());
        review.setReviewStatus(request.getReviewStatus());
        review.setReviewComment(request.getReviewComment());
        review.setReviewTime(LocalDateTime.now());
        
        taskReviewMapper.insert(review);
        
        // 更新任务状态
        task.setStatus("APPROVED".equals(request.getReviewStatus()) ? "APPROVED" : "REJECTED");
        taskMapper.updateById(task);
        
        log.info("任务审核完成，任务ID：{}，审核结果：{}", id, request.getReviewStatus());
        return true;
    }
    
    @Override
    public List<PublisherTaskReview> getReviewHistory(String id) {
        log.info("获取任务审核历史，任务ID：{}", id);
        
        LambdaQueryWrapper<PublisherTaskReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PublisherTaskReview::getTaskId, id);
        wrapper.orderByDesc(PublisherTaskReview::getReviewTime);
        
        return taskReviewMapper.selectList(wrapper);
    }
    
    /**
     * 创建任务详情
     */
    private void createTaskDetail(String taskId, TaskCreateRequest request) {
        switch (request.getTaskType()) {
            case "LIKE":
                createLikeTask(taskId, request);
                break;
            case "COMMENT":
                createCommentTask(taskId, request);
                break;
            case "SHARE":
                createShareTask(taskId, request);
                break;
            case "DISCUSS":
                createDiscussTask(taskId, request);
                break;
            case "INVITE":
                createInviteTask(taskId, request);
                break;
            case "FEEDBACK":
                createFeedbackTask(taskId, request);
                break;
            case "RANKING":
                createRankingTask(taskId, request);
                break;
            default:
                throw new BusinessException(ErrorCode.PARAM_ERROR, "不支持的任务类型：" + request.getTaskType());
        }
    }
    
    /**
     * 更新任务详情
     */
    private void updateTaskDetail(String taskId, TaskCreateRequest request) {
        // 先删除原有详情
        deleteTaskDetail(taskId);
        // 重新创建详情
        createTaskDetail(taskId, request);
    }
    
    /**
     * 删除任务详情
     */
    private void deleteTaskDetail(String taskId) {
        likeTaskMapper.delete(new LambdaQueryWrapper<PublisherLikeTask>().eq(PublisherLikeTask::getTaskId, taskId));
        commentTaskMapper.delete(new LambdaQueryWrapper<PublisherCommentTask>().eq(PublisherCommentTask::getTaskId, taskId));
        shareTaskMapper.delete(new LambdaQueryWrapper<PublisherShareTask>().eq(PublisherShareTask::getTaskId, taskId));
        discussTaskMapper.delete(new LambdaQueryWrapper<PublisherDiscussTask>().eq(PublisherDiscussTask::getTaskId, taskId));
        inviteTaskMapper.delete(new LambdaQueryWrapper<PublisherInviteTask>().eq(PublisherInviteTask::getTaskId, taskId));
        feedbackTaskMapper.delete(new LambdaQueryWrapper<PublisherFeedbackTask>().eq(PublisherFeedbackTask::getTaskId, taskId));
        rankingTaskMapper.delete(new LambdaQueryWrapper<PublisherRankingTask>().eq(PublisherRankingTask::getTaskId, taskId));
    }
    
    private void createLikeTask(String taskId, TaskCreateRequest request) {
        PublisherLikeTask likeTask = new PublisherLikeTask();
        likeTask.setTaskId(taskId);
        likeTask.setTargetUrl(request.getTargetUrl());
        likeTask.setLikeCount(1);
        likeTask.setCommentRequired(request.getCommentRequired());
        likeTaskMapper.insert(likeTask);
    }
    
    private void createCommentTask(String taskId, TaskCreateRequest request) {
        PublisherCommentTask commentTask = new PublisherCommentTask();
        commentTask.setTaskId(taskId);
        commentTask.setTargetUrl(request.getTargetUrl());
        commentTask.setCommentTemplate(request.getCommentTemplate());
        commentTask.setMinCommentLength(request.getMinCommentLength());
        commentTask.setMaxCommentLength(request.getMaxCommentLength());
        commentTask.setCommentCount(1);
        commentTaskMapper.insert(commentTask);
    }
    
    private void createShareTask(String taskId, TaskCreateRequest request) {
        PublisherShareTask shareTask = new PublisherShareTask();
        shareTask.setTaskId(taskId);
        shareTask.setShareContent(request.getShareContent());
        shareTask.setSharePlatform(request.getSharePlatform());
        shareTask.setShareUrl(request.getShareUrl());
        shareTask.setScreenshotRequired(request.getScreenshotRequired());
        shareTask.setShareCount(1);
        shareTaskMapper.insert(shareTask);
    }
    
    private void createDiscussTask(String taskId, TaskCreateRequest request) {
        PublisherDiscussTask discussTask = new PublisherDiscussTask();
        discussTask.setTaskId(taskId);
        discussTask.setDiscussTopic(request.getDiscussTopic());
        discussTask.setDiscussContent(request.getDiscussContent());
        discussTask.setDiscussPlatform(request.getDiscussPlatform());
        discussTask.setDiscussUrl(request.getDiscussUrl());
        discussTask.setMinDiscussLength(request.getMinDiscussLength());
        discussTask.setDiscussCount(1);
        discussTaskMapper.insert(discussTask);
    }
    
    private void createInviteTask(String taskId, TaskCreateRequest request) {
        PublisherInviteTask inviteTask = new PublisherInviteTask();
        inviteTask.setTaskId(taskId);
        inviteTask.setInvitePlatform(request.getInvitePlatform());
        inviteTask.setInviteLink(request.getInviteLink());
        inviteTask.setInviteCode(request.getInviteCode());
        inviteTask.setInviteCount(1);
        inviteTask.setInviteReward(request.getInviteReward());
        inviteTaskMapper.insert(inviteTask);
    }
    
    private void createFeedbackTask(String taskId, TaskCreateRequest request) {
        PublisherFeedbackTask feedbackTask = new PublisherFeedbackTask();
        feedbackTask.setTaskId(taskId);
        feedbackTask.setFeedbackType(request.getFeedbackType());
        feedbackTask.setFeedbackPlatform(request.getFeedbackPlatform());
        feedbackTask.setFeedbackContent(request.getFeedbackContent());
        feedbackTask.setFeedbackUrl(request.getFeedbackUrl());
        feedbackTask.setFeedbackCount(1);
        feedbackTaskMapper.insert(feedbackTask);
    }
    
    private void createRankingTask(String taskId, TaskCreateRequest request) {
        PublisherRankingTask rankingTask = new PublisherRankingTask();
        rankingTask.setTaskId(taskId);
        rankingTask.setRankingPlatform(request.getRankingPlatform());
        rankingTask.setRankingType(request.getRankingType());
        rankingTask.setRankingUrl(request.getRankingUrl());
        rankingTask.setTargetRanking(request.getTargetRanking());
        rankingTask.setRankingReward(request.getRankingReward());
        rankingTaskMapper.insert(rankingTask);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskFileUploadResponse uploadTaskFile(String taskId, String fileType, org.springframework.web.multipart.MultipartFile file) {
        log.info("上传任务文件 - 任务ID: {}, 文件类型: {}, 文件名: {}", taskId, fileType, file.getOriginalFilename());
        
        // 检查任务是否存在
        PublisherTask task = getTaskById(taskId);
        if (task == null) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }
        
        // 验证文件类型
        if (!isValidFileType(fileType)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不支持的文件类型，仅支持qr_code和task_avatar");
        }
        
        // 检查文件格式
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImageFile(originalFilename)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不支持的文件格式，仅支持JPG、PNG、GIF格式");
        }
        
        // 检查文件大小（5MB限制）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件大小不能超过5MB");
        }
        
        try {
            // 构建OSS上传请求
            FileUploadRequest uploadRequest = new FileUploadRequest();
            uploadRequest.setFile(file);
            uploadRequest.setSourceService("core-publisher");
            uploadRequest.setBusinessType(fileType);
            uploadRequest.setFilePath(fileType + "/" + java.time.LocalDate.now());
            uploadRequest.setUploadUserId(Long.valueOf(task.getPublisherId()));
            uploadRequest.setUploadUserName("publisher_" + task.getPublisherId());
            
            // 调用OSS服务上传文件
            ResultData<FileUploadResponse> uploadResult = ossFileFeignClient.uploadFile(uploadRequest);
            
            if (!uploadResult.isSuccess()) {
                throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, "文件上传失败: " + uploadResult.getMessage());
            }
            
            FileUploadResponse uploadResponse = uploadResult.getData();
            
            // 更新任务文件信息
            PublisherTask updateTask = new PublisherTask();
            updateTask.setId(taskId);
            if ("qr_code".equals(fileType)) {
                updateTask.setQrCodeUrl(uploadResponse.getAccessUrl());
            } else if ("task_avatar".equals(fileType)) {
                updateTask.setTaskAvatarUrl(uploadResponse.getAccessUrl());
            }
            taskMapper.updateById(updateTask);
            
            // 构建响应
            TaskFileUploadResponse response = new TaskFileUploadResponse();
            response.setTaskId(taskId);
            response.setFileType(fileType);
            response.setFileUrl(uploadResponse.getAccessUrl());
            response.setFileSize(file.getSize());
            response.setMimeType(file.getContentType());
            response.setOriginalFileName(originalFilename);
            
            log.info("任务文件上传成功 - 任务ID: {}, 文件类型: {}, 文件URL: {}", taskId, fileType, uploadResponse.getAccessUrl());
            return response;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("任务文件上传异常 - 任务ID: {}, 文件类型: {}, 错误: {}", taskId, fileType, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "文件上传失败，请稍后重试");
        }
    }
    
    /**
     * 检查是否为有效的文件类型
     *
     * @param fileType 文件类型
     * @return 是否为有效文件类型
     */
    private boolean isValidFileType(String fileType) {
        return "qr_code".equals(fileType) || "task_avatar".equals(fileType);
    }
    
    /**
     * 检查是否为有效的图片文件
     *
     * @param filename 文件名
     * @return 是否为有效图片
     */
    private boolean isValidImageFile(String filename) {
        if (filename == null) {
            return false;
        }
        
        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith(".jpg") || 
               lowerFilename.endsWith(".jpeg") || 
               lowerFilename.endsWith(".png") || 
               lowerFilename.endsWith(".gif");
    }
} 