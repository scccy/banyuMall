package com.origin.publisher.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.publisher.entity.PublisherTaskCompletion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 任务完成流水表Mapper接口
 * 作者: scccy
 * 创建时间: 2025-01-27
 */
@Mapper
public interface PublisherTaskCompletionMapper extends BaseMapper<PublisherTaskCompletion> {
    
    /**
     * 根据任务ID列表查询完成人数统计
     * @param taskIds 任务ID列表
     * @return 任务ID -> 完成人数的映射
     */
    @Select("SELECT task_id, COUNT(*) as completion_count " +
            "FROM publisher_task_completion " +
            "WHERE task_id IN (${taskIds}) " +
            "AND completion_status = 2 " +  // 已完成状态
            "AND deleted = 0 " +
            "GROUP BY task_id")
    Map<String, Integer> selectCompletionCountByTaskIds(@Param("taskIds") List<String> taskIds);
    
    /**
     * 根据任务ID查询完成记录列表
     * @param taskId 任务ID
     * @return 完成记录列表
     */
    List<PublisherTaskCompletion> selectByTaskId(@Param("taskId") String taskId);
} 