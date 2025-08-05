package com.origin.aliyunOss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.aliyunOss.entity.OssFileStorage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OSS文件存储记录Mapper接口
 * 
 * @author scccy
 * @since 2025-07-31
 */
@Mapper
public interface OssFileStorageMapper extends BaseMapper<OssFileStorage> {
    
    /**
     * 根据来源服务和业务类型查询文件列表
     *
     * @param sourceService 来源服务
     * @param businessType 业务类型
     * @return 文件列表
     */
    List<OssFileStorage> selectBySourceAndBusiness(@Param("sourceService") String sourceService,
                                                 @Param("businessType") String businessType);
    
    /**
     * 根据上传用户ID查询文件列表
     *
     * @param uploadUserId 上传用户ID
     * @return 文件列表
     */
    List<OssFileStorage> selectByUploadUserId(@Param("uploadUserId") String uploadUserId);
    
    /**
     * 根据文件路径查询文件列表
     *
     * @param filePath 文件路径
     * @return 文件列表
     */
    List<OssFileStorage> selectByFilePath(@Param("filePath") String filePath);
    
    /**
     * 分页查询文件存储记录
     *
     * @param page 分页参数
     * @param sourceService 来源服务
     * @param businessType 业务类型
     * @param uploadUserId 上传用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<OssFileStorage> selectFileStoragePage(Page<OssFileStorage> page,
                                              @Param("sourceService") String sourceService,
                                              @Param("businessType") String businessType,
                                              @Param("uploadUserId") String uploadUserId,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据对象键查询文件
     *
     * @param objectKey 对象键
     * @return 文件信息
     */
    OssFileStorage selectByObjectKey(@Param("objectKey") String objectKey);
} 