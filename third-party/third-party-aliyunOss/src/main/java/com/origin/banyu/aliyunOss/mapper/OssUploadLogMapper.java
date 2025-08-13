package com.origin.banyu.aliyunOss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.origin.banyu.aliyunOss.entity.OssUploadLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OssUploadLogMapper extends BaseMapper<OssUploadLog> {
    int insertLog(OssUploadLog log);
}