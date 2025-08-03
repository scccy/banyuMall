package com.origin.publisher.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.publisher.entity.PublisherTaskDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 任务详情表Mapper接口
 * 作者: scccy
 * 创建时间: 2025-01-27
 */
@Mapper
public interface PublisherTaskDetailMapper extends BaseMapper<PublisherTaskDetail> {
    
    /**
     * 根据任务ID查询任务详情
     * @param taskId 任务ID
     * @return 任务详情
     */
    PublisherTaskDetail selectByTaskId(@Param("taskId") String taskId);
} 