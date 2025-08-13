package com.origin.banyu.publisher.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基础实体服务测试类
 * 验证重构后的功能
 * 作者: AI小张
 * 创建时间: 2025-08-09
 */
@ExtendWith(MockitoExtension.class)
class BaseEntityServiceTest {
    
    // 测试用的具体实现类
    private static class TestEntityService extends BaseEntityService<String, Integer, String> {
        // 测试实现
    }
    
    @Test
    void testBuildPageResponse() {
        // 准备测试数据
        TestEntityService service = new TestEntityService();
        
        // 创建模拟的分页数据
        Page<String> sourcePage = new Page<>(1, 10, 25);
        sourcePage.setRecords(Arrays.asList("item1", "item2", "item3"));
        
        // 测试转换函数
        IPage<Integer> result = service.buildPageResponse(sourcePage, String::length);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getCurrent());
        assertEquals(10, result.getSize());
        assertEquals(25, result.getTotal());
        assertEquals(3, result.getRecords().size());
        assertEquals(5, result.getRecords().get(0)); // "item1" 长度为5
        assertEquals(5, result.getRecords().get(1)); // "item2" 长度为5
        assertEquals(5, result.getRecords().get(2)); // "item3" 长度为5
    }
    
    @Test
    void testBuildPageResponseWithEmptyList() {
        // 准备测试数据
        TestEntityService service = new TestEntityService();
        
        // 创建空的分页数据
        Page<String> sourcePage = new Page<>(1, 10, 0);
        sourcePage.setRecords(List.of());
        
        // 测试转换函数
        IPage<Integer> result = service.buildPageResponse(sourcePage, String::length);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getCurrent());
        assertEquals(10, result.getSize());
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getRecords().size());
    }
}
