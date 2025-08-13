package com.origin.banyu.wechatWork.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.banyu.wechatWork.entity.WechatWorkMoments;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业微信朋友圈动态Mapper接口
 * 
 * @author scccy
 */
@Mapper
public interface WechatWorkMomentsMapper extends BaseMapper<WechatWorkMoments> {
    
    /**
     * 根据wechatwork_moment_id查询动态
     * 
     * @param wechatworkMomentId 动态ID
     * @return 动态信息
     */
    WechatWorkMoments selectByWechatworkMomentId(@Param("wechatworkMomentId") String wechatworkMomentId);
    
    /**
     * 根据用户和时间范围统计动态数量
     * 
     * @param wechatworkUserId 企业微信用户ID
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 动态数量
     */
    long countByUserAndTimeRange(@Param("wechatworkUserId") String wechatworkUserId,
                                @Param("startTime") Long startTime,
                                @Param("endTime") Long endTime);
    
    /**
     * 根据用户和时间范围查询热门动态
     * 
     * @param wechatworkUserId 企业微信用户ID
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @param limit 限制数量
     * @return 热门动态列表
     */
    List<WechatWorkMoments> selectHotMomentsByUserAndTimeRange(@Param("wechatworkUserId") String wechatworkUserId,
                                                              @Param("startTime") Long startTime,
                                                              @Param("endTime") Long endTime,
                                                              @Param("limit") Integer limit);
} 