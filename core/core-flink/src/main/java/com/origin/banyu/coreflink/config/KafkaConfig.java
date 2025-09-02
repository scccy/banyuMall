package com.origin.banyu.coreflink.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 配置类
 *
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfig {

    /**
     * Kafka服务器地址
     */
    private String bootstrapServers;

    /**
     * 消费者配置
     */
    private Consumer consumer = new Consumer();

    /**
     * 生产者配置
     */
    private Producer producer = new Producer();

    /**
     * 主题配置
     */
    private Topic topic = new Topic();

    /**
     * 多管道配置（例如 user、order 等），可覆盖基础配置
     */
    private Map<String, Pipeline> pipelines = new HashMap<>();

    /**
     * 消费者配置
     */
    @Data
    public static class Consumer {
        /**
         * 消费者组ID
         */
        private String groupId;

        /**
         * 自动偏移量重置策略
         */
        private String autoOffsetReset = "earliest";

        /**
         * 是否启用自动提交
         */
        private boolean enableAutoCommit = false;

        /**
         * 自动提交间隔（毫秒）
         */
        private int autoCommitInterval = 1000;

        /**
         * 会话超时时间（毫秒）
         */
        private int sessionTimeout = 30000;

        /**
         * 心跳间隔（毫秒）
         */
        private int heartbeatInterval = 3000;

        /**
         * 最大拉取记录数
         */
        private int maxPollRecords = 500;

        /**
         * 拉取超时时间（毫秒）
         */
        private int fetchMaxWait = 500;

        /**
         * 最大拉取字节数
         */
        private int maxPartitionFetchBytes = 1048576;
    }

    /**
     * 生产者配置
     */
    @Data
    public static class Producer {
        /**
         * 确认机制
         */
        private String acks = "all";

        /**
         * 重试次数
         */
        private int retries = 3;

        /**
         * 批次大小（字节）
         */
        private int batchSize = 16384;

        /**
         * 延迟时间（毫秒）
         */
        private int lingerMs = 1;

        /**
         * 缓冲区大小（字节）
         */
        private int bufferMemory = 33554432;

        /**
         * 压缩类型
         */
        private String compressionType = "snappy";

        /**
         * 最大请求大小（字节）
         */
        private int maxRequestSize = 1048576;

        /**
         * 请求超时时间（毫秒）
         */
        private int requestTimeout = 30000;

        /**
         * 元数据获取超时时间（毫秒）
         */
        private int metadataMaxAge = 300000;
    }

    /**
     * 主题配置
     */
    @Data
    public static class Topic {
        /**
         * 输入主题
         */
        private String input;

        /**
         * 输出主题
         */
        private String output;

        /**
         * 错误主题
         */
        private String error;

        /**
         * 分区数
         */
        private int partitions;

        /**
         * 副本因子
         */
        private int replicationFactor;
    }

    /**
     * 单个管道配置，可选择性覆盖基础配置
     */
    @Data
    public static class Pipeline {
        /**
         * 覆盖级别的 bootstrapServers（可选）
         */
        private String bootstrapServers;

        /**
         * 管道级别 Topic 配置
         */
        private Topic topic = new Topic();

        /**
         * 管道级别 Consumer 配置
         */
        private Consumer consumer = new Consumer();

        /**
         * 管道级别 Producer 配置
         */
        private Producer producer = new Producer();
    }

    /**
     * 工具方法：根据管道名解析 bootstrapServers（带回退）
     */
    public String resolveBootstrapServers(String pipelineName) {
        Pipeline p = pipelines != null ? pipelines.get(pipelineName) : null;
        return (p != null && p.getBootstrapServers() != null && !p.getBootstrapServers().isEmpty())
                ? p.getBootstrapServers() : this.bootstrapServers;
    }

    /**
     * 工具方法：根据管道名解析 input topic（带回退）
     */
    public String resolveInputTopic(String pipelineName) {
        Pipeline p = pipelines != null ? pipelines.get(pipelineName) : null;
        return (p != null && p.getTopic() != null && p.getTopic().getInput() != null && !p.getTopic().getInput().isEmpty())
                ? p.getTopic().getInput() : this.topic.getInput();
    }

    /**
     * 工具方法：根据管道名解析 output topic（带回退）
     */
    public String resolveOutputTopic(String pipelineName) {
        Pipeline p = pipelines != null ? pipelines.get(pipelineName) : null;
        return (p != null && p.getTopic() != null && p.getTopic().getOutput() != null && !p.getTopic().getOutput().isEmpty())
                ? p.getTopic().getOutput() : this.topic.getOutput();
    }

    /**
     * 工具方法：根据管道名解析 groupId（带回退）
     */
    public String resolveGroupId(String pipelineName) {
        Pipeline p = pipelines != null ? pipelines.get(pipelineName) : null;
        return (p != null && p.getConsumer() != null && p.getConsumer().getGroupId() != null && !p.getConsumer().getGroupId().isEmpty())
                ? p.getConsumer().getGroupId() : this.consumer.getGroupId();
    }
}
