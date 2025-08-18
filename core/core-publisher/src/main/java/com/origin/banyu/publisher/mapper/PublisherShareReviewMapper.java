package com.origin.banyu.publisher.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.origin.banyu.publisher.dto.request.ShareReviewListRequest;
import com.origin.banyu.publisher.entity.PublisherShareReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 社群分享审核Mapper接口
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Mapper
public interface PublisherShareReviewMapper extends BaseMapper<PublisherShareReview> {
    
    /**
     * 根据条件查询分享审核列表
     * @param reviewStatus 审核状态
     * @return 分享审核列表
     */
    List<PublisherShareReview> selectByReviewStatus(@Param("reviewStatus") Integer reviewStatus);

    /**
     * 多条件分页查询
     */
    IPage<PublisherShareReview> selectByConditions(
            IPage<PublisherShareReview> page,
            @Param("req") ShareReviewListRequest request);
    
    /**
     * 多条件查询所有数据（不分页，用于导出）
     * @param request 查询条件
     * @return 分享审核列表
     */
    List<PublisherShareReview> selectAllByConditions(@Param("req") ShareReviewListRequest request);
    
    /**
     * 根据任务ID列表批量查询
     * @param taskIds 任务ID列表
     * @return 分享审核列表
     */
    List<PublisherShareReview> selectByTaskIds(@Param("taskIds") List<String> taskIds);
    
    /**
     * 根据用户ID列表批量查询
     * @param userIds 用户ID列表
     * @return 分享审核列表
     */
    List<PublisherShareReview> selectByUserIds(@Param("userIds") List<String> userIds);
    
    /**
     * 统计指定条件下的记录数量
     * @param request 查询条件
     * @return 记录数量
     */
    Long countByConditions(@Param("req") ShareReviewListRequest request);
} 