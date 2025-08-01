package com.origin.oss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.oss.entity.OssFileAccessLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * OSS文件访问日志Mapper接口
 * 
 * @author scccy
 * @since 2025-01-27
 */
@Mapper
public interface OssFileAccessLogMapper extends BaseMapper<OssFileAccessLog> {
} 