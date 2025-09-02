package com.origin.banyu.coreflink.sink;

import com.alibaba.fastjson2.JSON;
import com.origin.banyu.coreflink.config.KafkaConfig;
import com.origin.banyu.coreflink.entity.RegistrationSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.springframework.stereotype.Component;

/**
 * Kafka注册汇总结果输出
 *
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaRegistrationSummarySink {

    private final KafkaConfig kafkaConfig;

    /**
     * 构建Kafka输出（默认基础配置）
     */
    public KafkaSink<RegistrationSummary> buildKafkaSink() {
        return buildKafkaSink(null);
    }

    /**
     * 构建Kafka输出（指定管道名）
     */
    public KafkaSink<RegistrationSummary> buildKafkaSink(String pipelineName) {
        final String bootstrap = kafkaConfig.resolveBootstrapServers(pipelineName);
        final String topic = kafkaConfig.resolveOutputTopic(pipelineName);

        return KafkaSink.<RegistrationSummary>builder()
                .setBootstrapServers(bootstrap)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(topic)
                        .setValueSerializationSchema(new RegistrationSummarySerializer())
                        .build())
                .build();
    }

    /**
     * 注册汇总结果序列化器
     */
    public static class RegistrationSummarySerializer implements SerializationSchema<RegistrationSummary> {

        @Override
        public byte[] serialize(RegistrationSummary summary) {
            try {
                String json = JSON.toJSONString(summary);
                log.debug("Serializing summary: {}", json);
                return json.getBytes();
            } catch (Exception e) {
                log.error("Failed to serialize summary: {}", summary, e);
                return "{}".getBytes();
            }
        }
    }
}
