package com.origin.publisher.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.publisher.entity.PublisherTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务主表Mapper接口
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Mapper
public interface PublisherTaskMapper extends BaseMapper<PublisherTask> {
    
    /**
     * 根据条件查询任务列表
     *
     * @param taskTypeId 任务类型ID
     * @param statusId 任务状态ID
     * @param keyword 关键词
     * @return 任务列表
     */
    List<PublisherTask> selectTasksByCondition(@Param("taskTypeId") Integer taskTypeId,
                                              @Param("statusId") Integer statusId,
                                              @Param("keyword") String keyword);
    
    /**
     * 根据任务类型和状态查询任务列表
     *
     * @param taskTypeId 任务类型ID
     * @param statusId 任务状态ID
     * @param limit 限制数量
     * @return 任务列表
     */
    List<PublisherTask> selectTasksByTypeAndStatus(@Param("taskTypeId") Integer taskTypeId,
                                                  @Param("statusId") Integer statusId,
                                                  @Param("limit") Integer limit);
} 