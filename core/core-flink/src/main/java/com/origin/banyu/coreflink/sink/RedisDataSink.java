package com.origin.banyu.coreflink.sink;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.time.Duration;

/**
 * 通用Redis数据输出Sink
 * 支持任意JSONObject数据的存储
 *
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Slf4j
public class RedisDataSink implements Serializable {

    private final RedisTemplate<String, Object> redisTemplate;
    /**
     * -- GETTER --
     *  获取主题名称
     */
    @Getter
    private final String topicName;
    private final String keyPrefix;
    /**
     * -- GETTER --
     *  获取最新数据的key
     */
    @Getter
    private final String latestKey;
    /**
     * -- GETTER --
     *  获取历史数据的key
     */
    @Getter
    private final String historyKey;
    private final Duration ttl;
    
    // 默认构造函数 - 使用user主题
    public RedisDataSink(RedisTemplate<String, Object> redisTemplate) {
        this(redisTemplate, "user");
    }
    
    // 支持多主题的构造函数
    public RedisDataSink(RedisTemplate<String, Object> redisTemplate, String topicName) {
        this.redisTemplate = redisTemplate;
        this.topicName = topicName;
        this.keyPrefix = "flink:" + topicName + ":";
        this.latestKey = "flink:" + topicName + ":latest";
        this.historyKey = "flink:" + topicName + ":history";
        this.ttl = Duration.ofHours(24); // 24小时过期
    }
    
    public void accept(JSONObject data) {
        try {
            // 添加通用元数据
            JSONObject enrichedData = enrichWithMetadata(data);
            String jsonData = JSON.toJSONString(enrichedData);
            
            // 存储最新数据，供实时监控使用
            redisTemplate.opsForValue().set(latestKey, jsonData, ttl);
            
            // 存储到历史记录（保留最近100条）
            redisTemplate.opsForList().leftPush(historyKey, jsonData);
            redisTemplate.opsForList().trim(historyKey, 0, 99); // 保留最近100条
            redisTemplate.expire(historyKey, ttl);
            
            log.debug("Stored data to Redis: latestKey={}, data={}", latestKey, jsonData);
            
        } catch (Exception e) {
            log.error("Failed to store data to Redis: {}", data, e);
            // 不抛出异常，避免影响Flink作业
        }
    }





    /**
     * 添加通用元数据到JSON数据中
     */
    private JSONObject enrichWithMetadata(JSONObject data) {
        // 创建新的JSONObject，避免修改原始数据
        JSONObject enriched = new JSONObject(data);
        
        // 添加通用元数据
        enriched.put("topic", topicName);
        enriched.put("timestamp", System.currentTimeMillis());
        enriched.put("dataSource", "flink-realtime");
        enriched.put("sinkTime", System.currentTimeMillis());
        
        // 如果原始数据没有时间戳，添加当前时间
        if (!enriched.containsKey("processTime")) {
            enriched.put("processTime", System.currentTimeMillis());
        }
        
        return enriched;
    }



}
