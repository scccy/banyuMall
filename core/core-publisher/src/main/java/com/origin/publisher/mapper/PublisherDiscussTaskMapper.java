package com.origin.publisher.mapper;

import com.origin.publisher.entity.PublisherDiscussTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 讨论任务Mapper接口
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Mapper
public interface PublisherDiscussTaskMapper extends BaseMapper<PublisherDiscussTask> {
    
    /**
     * 根据任务ID查询讨论任务详情
     *
     * @param taskId 任务ID
     * @return 讨论任务详情
     */
    PublisherDiscussTask selectByTaskId(@Param("taskId") String taskId);
    
    /**
     * 根据讨论主题查询讨论任务列表
     *
     * @param discussTopic 讨论主题
     * @return 讨论任务列表
     */
    List<PublisherDiscussTask> selectByDiscussTopic(@Param("discussTopic") String discussTopic);
    
    /**
     * 根据讨论平台查询讨论任务列表
     *
     * @param discussPlatform 讨论平台
     * @return 讨论任务列表
     */
    List<PublisherDiscussTask> selectByDiscussPlatform(@Param("discussPlatform") String discussPlatform);
} 