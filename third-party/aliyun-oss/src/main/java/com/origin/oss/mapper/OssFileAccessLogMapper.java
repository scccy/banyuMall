package com.origin.oss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.oss.entity.OssFileAccessLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OSS文件访问日志Mapper接口
 * 
 * @author scccy
 * @since 2025-01-27
 */
@Mapper
public interface OssFileAccessLogMapper extends BaseMapper<OssFileAccessLog> {
    
    /**
     * 根据文件ID查询访问日志
     *
     * @param fileId 文件ID
     * @return 访问日志列表
     */
    List<OssFileAccessLog> selectByFileId(@Param("fileId") String fileId);
    
    /**
     * 根据访问用户ID查询访问日志
     *
     * @param accessUserId 访问用户ID
     * @return 访问日志列表
     */
    List<OssFileAccessLog> selectByAccessUserId(@Param("accessUserId") String accessUserId);
    
    /**
     * 根据访问类型查询访问日志
     *
     * @param accessType 访问类型
     * @return 访问日志列表
     */
    List<OssFileAccessLog> selectByAccessType(@Param("accessType") String accessType);
    
    /**
     * 分页查询文件访问日志
     *
     * @param page 分页参数
     * @param fileId 文件ID
     * @param accessType 访问类型
     * @param accessUserId 访问用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<OssFileAccessLog> selectAccessLogPage(Page<OssFileAccessLog> page,
                                             @Param("fileId") String fileId,
                                             @Param("accessType") String accessType,
                                             @Param("accessUserId") String accessUserId,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计文件访问次数
     *
     * @param fileId 文件ID
     * @return 访问次数
     */
    Long countAccessByFileId(@Param("fileId") String fileId);
} 