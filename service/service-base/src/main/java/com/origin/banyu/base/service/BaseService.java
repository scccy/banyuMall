package com.origin.banyu.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 基础服务抽象类
 * 提供通用的业务方法包装器和分页查询方法
 * 子类继承此类，在业务逻辑中直接抛出BusinessException，由AOP自动处理
 * 
 * @author AI Assistant
 * @since 2025-08-14
 */
@Transactional(rollbackFor = Exception.class)
public abstract class BaseService {
    
    /**
     * 通用业务方法包装器 - 有返回值
     * 
     * @param supplier 业务逻辑提供者
     * @param <T> 返回值类型
     * @return 业务执行结果
     */
    protected <T> T execute(Supplier<T> supplier) {
        return supplier.get();
    }
    
    /**
     * 通用业务方法包装器 - 无返回值
     * 
     * @param runnable 业务逻辑执行者
     */
    protected void execute(Runnable runnable) {
        runnable.run();
    }
    
    /**
     * 构建分页响应
     * @param page 原始分页结果
     * @param converter 转换函数
     * @return 转换后的分页响应
     */
    protected <T, R> IPage<R> buildPageResponse(IPage<T> page, Function<T, R> converter) {
        List<R> responses = page.getRecords().stream()
                .map(converter)
                .collect(Collectors.toList());

        Page<R> responsePage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        responsePage.setRecords(responses);
        return responsePage;
    }
}
