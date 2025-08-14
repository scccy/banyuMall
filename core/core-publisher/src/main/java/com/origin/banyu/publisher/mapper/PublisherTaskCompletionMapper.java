package com.origin.banyu.publisher.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.common.dto.CompletionDetailResponseDto;
import com.origin.banyu.publisher.entity.PublisherTaskCompletion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     *
     * @param taskIds 任务ID列表
     * @return 任务完成统计列表，包含taskId、completionCount
     */
    List<Map<String, Object>> selectCompletionCountByTaskIds(@Param("taskIds") List<String> taskIds);

    
    /**
     * 根据任务ID查询完成记录列表
     * @param taskId 任务ID
     * @return 完成记录列表
     */
    List<PublisherTaskCompletion> selectByTaskId(@Param("taskId") String taskId);


    

    
    /**
     * 查询排名任务详情（类型7）
     * @param page 分页参数
     * @param taskId 任务ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排名任务详情分页结果
     */
    IPage<CompletionDetailResponseDto.RankTaskDetail> selectRankTaskDetailsPage(
        IPage<CompletionDetailResponseDto.RankTaskDetail> page,
        @Param("taskId") String taskId,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("topCount") Integer topCount
    );
    

} 