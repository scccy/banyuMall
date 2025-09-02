package com.origin.banyu.coreflink.source;

import com.origin.banyu.coreflink.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.springframework.stereotype.Component;

/**
 * 通用Kafka数据源构建器
 * 支持多种数据类型的反序列化
 *
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSourceBuilder {

    private final KafkaConfig kafkaConfig;

    /**
     * 构建通用Kafka数据源（推荐使用）
     * 自动创建GenericJsonDeserializer
     * 
     * @param pipelineName 管道名称，用于选择配置
     * @param dataType 数据类型Class
     * @param <T> 数据类型泛型
     * @return KafkaSource实例
     */
    public <T> KafkaSource<T> buildKafkaSource(String pipelineName, Class<T> dataType) {
        return buildKafkaSource(pipelineName, dataType, GenericJsonDeserializer.of(dataType));
    }

    /**
     * 构建通用Kafka数据源（使用默认配置）
     * 自动创建GenericJsonDeserializer
     * 
     * @param dataType 数据类型Class
     * @param <T> 数据类型泛型
     * @return KafkaSource实例
     */
    public <T> KafkaSource<T> buildKafkaSource(Class<T> dataType) {
        return buildKafkaSource(null, dataType, GenericJsonDeserializer.of(dataType));
    }

    /**
     * 构建通用Kafka数据源（高级用法）
     * 允许自定义反序列化器
     * 
     * @param pipelineName 管道名称，用于选择配置
     * @param dataType 数据类型Class
     * @param deserializer 自定义反序列化器
     * @param <T> 数据类型泛型
     * @return KafkaSource实例
     */
    public <T> KafkaSource<T> buildKafkaSource(String pipelineName, Class<T> dataType, DeserializationSchema<T> deserializer) {
        final String bootstrap = kafkaConfig.resolveBootstrapServers(pipelineName);
        final String topic = kafkaConfig.resolveInputTopic(pipelineName);
        final String groupId = kafkaConfig.resolveGroupId(pipelineName);

        log.info("Building Kafka source for pipeline: {}, topic: {}, groupId: {}", pipelineName, topic, groupId);

        return KafkaSource.<T>builder()
                .setBootstrapServers(bootstrap)
                .setTopics(topic)
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(deserializer)
                .build();
    }
}
