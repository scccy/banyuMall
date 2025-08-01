package com.origin.publisher.mapper;

import com.origin.publisher.entity.PublisherLikeTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 点赞任务Mapper接口
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Mapper
public interface PublisherLikeTaskMapper extends BaseMapper<PublisherLikeTask> {
    
    /**
     * 根据任务ID查询点赞任务详情
     *
     * @param taskId 任务ID
     * @return 点赞任务详情
     */
    PublisherLikeTask selectByTaskId(@Param("taskId") String taskId);
    
    /**
     * 根据目标URL查询点赞任务列表
     *
     * @param targetUrl 目标URL
     * @return 点赞任务列表
     */
    List<PublisherLikeTask> selectByTargetUrl(@Param("targetUrl") String targetUrl);
} 