package com.origin.publisher.mapper;

import com.origin.publisher.entity.PublisherCommentTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论任务Mapper接口
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Mapper
public interface PublisherCommentTaskMapper extends BaseMapper<PublisherCommentTask> {
    
    /**
     * 根据任务ID查询评论任务详情
     *
     * @param taskId 任务ID
     * @return 评论任务详情
     */
    PublisherCommentTask selectByTaskId(@Param("taskId") String taskId);
    
    /**
     * 根据目标URL查询评论任务列表
     *
     * @param targetUrl 目标URL
     * @return 评论任务列表
     */
    List<PublisherCommentTask> selectByTargetUrl(@Param("targetUrl") String targetUrl);
} 