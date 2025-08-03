package com.origin.publisher.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.publisher.entity.PublisherShareReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 社群分享审核Mapper接口
 * 作者: scccy
 * 创建时间: 2025-01-27
 */
@Mapper
public interface PublisherShareReviewMapper extends BaseMapper<PublisherShareReview> {
    
    /**
     * 根据条件查询分享审核列表
     * @param reviewStatus 审核状态
     * @return 分享审核列表
     */
    List<PublisherShareReview> selectByReviewStatus(@Param("reviewStatus") Integer reviewStatus);
} 