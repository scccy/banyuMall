package com.origin.banyu.coreflink.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.origin.banyu.coreflink.sink.RedisDataSink;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用Redis数据查询服务
 * 支持查询任意主题的JSON数据
 *
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisDataService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取最新数据（默认user主题）
     */
    public JSONObject getLatestData() {
        return getLatestData("user");
    }

    /**
     * 获取指定主题的最新数据
     */
    public JSONObject getLatestData(String topicName) {
        try {
            RedisDataSink sink = new RedisDataSink(redisTemplate, topicName);
            Object data = redisTemplate.opsForValue().get(sink.getLatestKey());
            if (data instanceof String) {
                return JSON.parseObject((String) data);
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to get latest data from Redis for topic: {}", topicName, e);
            return null;
        }
    }

    /**
     * 获取历史数据（最近100条，默认user主题）
     */
    public List<JSONObject> getHistoryData() {
        return getHistoryData("user");
    }

    /**
     * 获取指定主题的历史数据（最近100条）
     */
    public List<JSONObject> getHistoryData(String topicName) {
        try {
            RedisDataSink sink = new RedisDataSink(redisTemplate, topicName);
            List<Object> dataList = redisTemplate.opsForList().range(sink.getHistoryKey(), 0, -1);
            
            List<JSONObject> jsonDataList = new ArrayList<>();
            if (dataList != null) {
                for (Object data : dataList) {
                    if (data instanceof String) {
                        jsonDataList.add(JSON.parseObject((String) data));
                    }
                }
            }
            return jsonDataList;
        } catch (Exception e) {
            log.error("Failed to get history data from Redis for topic: {}", topicName, e);
            return new ArrayList<>();
        }
    }





    /**
     * 获取Redis中的键信息（用于调试）
     */
    public List<String> getRedisKeys() {
        try {
            return new ArrayList<>(redisTemplate.keys("flink:*"));
        } catch (Exception e) {
            log.error("Failed to get Redis keys", e);
            return new ArrayList<>();
        }
    }
}
