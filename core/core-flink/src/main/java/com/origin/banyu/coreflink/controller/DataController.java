package com.origin.banyu.coreflink.controller;

import com.alibaba.fastjson2.JSONObject;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.coreflink.service.RedisDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通用数据查询控制器
 * 支持查询任意主题的实时数据
 *
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {

    private final RedisDataService redisDataService;



    /**
     * 获取最新数据（默认user主题）
     */
    @GetMapping("/latest")
    public ResultData<JSONObject> getLatestData() {
        return getLatestData("user");
    }

    /**
     * 获取指定主题的最新数据
     */
    @GetMapping("/latest/{topicName}")
    public ResultData<JSONObject> getLatestData(@PathVariable String topicName) {
        JSONObject data = redisDataService.getLatestData(topicName);
        if (data != null) {
            return ResultData.success("获取最新数据成功", data);
        }
        return ResultData.fail("暂无数据");
    }

    /**
     * 获取历史数据
     */
    @GetMapping("/history")
    public ResultData<List<JSONObject>> getHistoryData() {
        return getHistoryData("user");
    }

    /**
     * 获取指定主题的历史数据
     */
    @GetMapping("/history/{topicName}")
    public ResultData<List<JSONObject>> getHistoryData(@PathVariable String topicName) {
        List<JSONObject> data = redisDataService.getHistoryData(topicName);
        if (data != null && !data.isEmpty()) {
            return ResultData.success("获取历史数据成功", data);
        }
        return ResultData.fail("暂无历史数据");
    }



    /**
     * 获取Redis键信息（用于调试）
     */
    @GetMapping("/debug/keys")
    public ResultData<List<String>> getRedisKeys() {
        List<String> keys = redisDataService.getRedisKeys();
        if (keys != null && !keys.isEmpty()) {
            return ResultData.success("获取Redis键信息成功", keys);
        }
        return ResultData.fail("暂无Redis键信息");
    }
}
