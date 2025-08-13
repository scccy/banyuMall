package com.origin.banyu.publisher.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 基础实体服务抽象类
 * 提供通用的CRUD操作和分页查询方法
 * 作者: AI小张
 * 创建时间: 2025-08-09
 */
@Slf4j
public abstract class BaseEntityService<T, R, ID> {
    
    /**
     * 构建分页响应
     * @param page 原始分页结果
     * @param converter 转换函数
     * @return 转换后的分页响应
     */
    protected IPage<R> buildPageResponse(IPage<T> page, Function<T, R> converter) {
        List<R> responses = page.getRecords().stream()
            .map(converter)
            .collect(Collectors.toList());
        
        Page<R> responsePage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        responsePage.setRecords(responses);
        return responsePage;
    }
    
    /**
     * 验证实体是否存在
     * @param entity 实体对象
     * @param entityName 实体名称
     * @param errorCode 错误码
     */
    protected void validateEntityExists(T entity, String entityName, ErrorCode errorCode) {
        if (entity == null) {
            throw new BusinessException(errorCode, entityName + "不存在");
        }
    }
    
    /**
     * 验证实体是否存在
     * @param entity 实体对象
     * @param entityName 实体名称
     */
    protected void validateEntityExists(T entity, String entityName) {
        validateEntityExists(entity, entityName, ErrorCode.PARAMS_ERROR);
    }
    
    /**
     * 验证状态是否有效
     * @param currentStatus 当前状态
     * @param expectedStatus 期望状态
     * @param errorMessage 错误消息
     */
    protected void validateStatus(Integer currentStatus, Integer expectedStatus, String errorMessage) {
        if (!expectedStatus.equals(currentStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, errorMessage);
        }
    }
    
    /**
     * 记录操作日志
     * @param operation 操作名称
     * @param entityId 实体ID
     * @param details 详细信息
     */
    protected void logOperation(String operation, ID entityId, String details) {
        log.info("{} - 实体ID: {}, 详情: {}", operation, entityId, details);
    }
    
    /**
     * 记录操作日志
     * @param operation 操作名称
     * @param entityId 实体ID
     */
    protected void logOperation(String operation, ID entityId) {
        log.info("{} - 实体ID: {}", operation, entityId);
    }
}
