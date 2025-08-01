package com.origin.publisher.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.publisher.dto.TaskCreateRequest;
import com.origin.publisher.dto.TaskFileUploadResponse;
import com.origin.publisher.dto.TaskQueryRequest;
import com.origin.publisher.dto.TaskReviewRequest;
import com.origin.publisher.entity.PublisherTask;
import com.origin.publisher.entity.PublisherTaskReview;

import java.util.List;

/**
 * 任务服务接口
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
public interface PublisherTaskService {
    
    /**
     * 创建任务
     */
    PublisherTask createTask(TaskCreateRequest request);
    
    /**
     * 更新任务
     */
    PublisherTask updateTask(String id, TaskCreateRequest request);
    
    /**
     * 删除任务
     */
    boolean deleteTask(String id);
    
    /**
     * 根据ID获取任务
     */
    PublisherTask getTaskById(String id);
    
    /**
     * 分页查询任务列表
     */
    IPage<PublisherTask> queryTasks(TaskQueryRequest request);
    
    /**
     * 提交任务审核
     */
    boolean submitForReview(String id);
    
    /**
     * 审核任务
     */
    boolean reviewTask(String id, TaskReviewRequest request);
    
    /**
     * 获取审核历史
     */
    List<PublisherTaskReview> getReviewHistory(String id);
    
    /**
     * 上传任务文件（二维码或任务头像）
     *
     * @param taskId 任务ID
     * @param fileType 文件类型：qr_code-二维码，task_avatar-任务头像
     * @param file 上传的文件
     * @return 上传结果
     */
    TaskFileUploadResponse uploadTaskFile(String taskId, String fileType, org.springframework.web.multipart.MultipartFile file);
} 