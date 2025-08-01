package com.origin.publisher.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.publisher.entity.PublisherTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务基础Mapper接口
 * 作者: scccy
 * 创建时间: 2025-08-01
 */
@Mapper
public interface PublisherTaskMapper extends BaseMapper<PublisherTask> {
    
    /**
     * 根据发布者ID分页查询任务列表
     *
     * @param page 分页参数
     * @param publisherId 发布者ID
     * @param taskType 任务类型
     * @param status 任务状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<PublisherTask> selectTaskPageByPublisher(Page<PublisherTask> page,
                                                  @Param("publisherId") String publisherId,
                                                  @Param("taskType") String taskType,
                                                  @Param("status") String status,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据任务类型和状态查询任务列表
     *
     * @param taskType 任务类型
     * @param status 任务状态
     * @param limit 限制数量
     * @return 任务列表
     */
    List<PublisherTask> selectTasksByTypeAndStatus(@Param("taskType") String taskType,
                                                  @Param("status") String status,
                                                  @Param("limit") Integer limit);
    
    /**
     * 查询即将开始的任务
     *
     * @param currentTime 当前时间
     * @param limit 限制数量
     * @return 即将开始的任务列表
     */
    List<PublisherTask> selectUpcomingTasks(@Param("currentTime") LocalDateTime currentTime,
                                           @Param("limit") Integer limit);
    
    /**
     * 查询即将结束的任务
     *
     * @param currentTime 当前时间
     * @param limit 限制数量
     * @return 即将结束的任务列表
     */
    List<PublisherTask> selectEndingTasks(@Param("currentTime") LocalDateTime currentTime,
                                         @Param("limit") Integer limit);
} 