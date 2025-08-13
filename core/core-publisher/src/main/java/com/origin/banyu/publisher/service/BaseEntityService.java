package com.origin.banyu.publisher.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

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


}
