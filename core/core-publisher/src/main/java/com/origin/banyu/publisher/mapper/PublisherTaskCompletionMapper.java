package com.origin.banyu.publisher.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.publisher.dto.response.CompletionDetailResponseDto;
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
     * 查询排行榜数据：按用户ID分组汇总奖励金额，按总金额降序排序，限制返回前N名
     * @param taskId 任务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param topCount 返回前N名
     * @return 排行榜数据列表
     */
    @Select("""
        SELECT 
            ptd.user_id,
            SUM(ptd.reward_amount) as total_reward,
            ROW_NUMBER() OVER (ORDER BY SUM(ptd.reward_amount) DESC) as `rank`
        FROM publisher_task_completion ptd
        WHERE ptd.task_id = #{taskId} 
            AND ptd.deleted = false 
            AND ptd.completion_status = 2
            AND ptd.completion_time BETWEEN #{startTime} AND #{endTime}
        GROUP BY ptd.user_id
        ORDER BY total_reward DESC
        LIMIT #{topCount}
        """)
    List<Map<String, Object>> selectTopRankings(
        @Param("taskId") String taskId,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime,
        @Param("topCount") Integer topCount
    );
    
    /**
     * 查询普通任务详情（类型1,2,3,4,6）
     * @param page 分页参数
     * @param taskId 任务ID
     * @return 普通任务详情分页结果
     */
    IPage<CompletionDetailResponseDto.NormalTaskDetail> selectNormalTaskDetailsPage(
        IPage<CompletionDetailResponseDto.NormalTaskDetail> page,
        @Param("taskId") String taskId
    );
    
    /**
     * 查询邀请任务详情（类型5）
     * @param page 分页参数
     * @param taskId 任务ID
     * @return 邀请任务详情分页结果
     */
    IPage<CompletionDetailResponseDto.InviteTaskDetail> selectInviteTaskDetailsPage(
        IPage<CompletionDetailResponseDto.InviteTaskDetail> page,
        @Param("taskId") String taskId
    );
    
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