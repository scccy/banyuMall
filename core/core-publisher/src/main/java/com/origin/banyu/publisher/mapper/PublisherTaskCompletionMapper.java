package com.origin.banyu.publisher.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.banyu.publisher.entity.PublisherTaskCompletion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 任务完成流水表Mapper接口
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Mapper
public interface PublisherTaskCompletionMapper extends BaseMapper<PublisherTaskCompletion> {
    
    /**
     * 根据任务ID列表查询完成人数统计
     * @param taskIds 任务ID列表
     * @return 任务ID -> 完成人数的映射
     */

    
    /**
     * 根据任务ID查询完成记录列表
     * @param taskId 任务ID
     * @return 完成记录列表
     */
    List<PublisherTaskCompletion> selectByTaskId(@Param("taskId") String taskId);
} 