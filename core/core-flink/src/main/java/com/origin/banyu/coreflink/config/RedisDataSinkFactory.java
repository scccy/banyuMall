package com.origin.banyu.coreflink.config;

import com.origin.banyu.coreflink.sink.RedisDataSink;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * RedisDataSink工厂类
 * 提供按需创建RedisDataSink实例的方法
 *
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Component
@RequiredArgsConstructor
public class RedisDataSinkFactory {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 缓存已创建的实例，避免重复创建
    private final ConcurrentHashMap<String, RedisDataSink> sinkCache = new ConcurrentHashMap<>();
    
    /**
     * 获取指定主题的RedisDataSink实例
     * 
     * @param topicName 主题名称
     * @return RedisDataSink实例
     */
    public RedisDataSink getSink(String topicName) {
        return sinkCache.computeIfAbsent(topicName, 
            name -> new RedisDataSink(redisTemplate, name));
    }
    
        /**
     * 获取默认主题的RedisDataSink实例（用于向后兼容）
     */
    public RedisDataSink getDefaultSink() {
        return getSink("user");
    }
    
    /**
     * 清除缓存（用于测试或重新加载配置）
     */
    public void clearCache() {
        sinkCache.clear();
    }
}
