package com.origin.banyu.coreflink.util;

import com.alibaba.fastjson2.JSON;
import com.origin.banyu.coreflink.entity.UserRegistrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

/**
 * 测试数据生成器
 *
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Slf4j
@Component
public class TestDataGenerator {

    private static final String[] NICKNAMES = {
        "张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十",
        "Alice", "Bob", "Charlie", "David", "Eva", "Frank", "Grace", "Henry",
        "小明", "小红", "小刚", "小丽", "小强", "小美", "小华", "小芳"
    };

    private static final String[] GENDERS = {"MALE", "FEMALE", "UNKNOWN"};

    private final Random random = new Random();

    /**
     * 生成随机用户注册事件
     */
    public UserRegistrationEvent generateRandomEvent() {
        String userId = UUID.randomUUID().toString();
        LocalDateTime registerTime = LocalDateTime.now().minusSeconds(random.nextInt(3600)); // 随机时间
        String nickname = NICKNAMES[random.nextInt(NICKNAMES.length)];
        String gender = GENDERS[random.nextInt(GENDERS.length)];

        return UserRegistrationEvent.builder()
                .userId(userId)
                .registerTime(registerTime)
                .nickname(nickname)
                .gender(gender)
                .timestamp(registerTime.toInstant(ZoneOffset.of("+8")).toEpochMilli())
                .source("test-generator")
                .build();
    }

    /**
     * 发送测试数据到Kafka
     */
    public void sendTestDataToKafka(String bootstrapServers, String topic, int count) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            log.info("Sending {} test events to topic: {}", count, topic);

            for (int i = 0; i < count; i++) {
                UserRegistrationEvent event = generateRandomEvent();
                String json = JSON.toJSONString(event);

                ProducerRecord<String, String> record = new ProducerRecord<>(topic, event.getUserId(), json);
                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        log.error("Failed to send message: {}", exception.getMessage());
                    } else {
                        log.debug("Message sent to topic: {}, partition: {}, offset: {}",
                                metadata.topic(), metadata.partition(), metadata.offset());
                    }
                });

                // 随机延迟，模拟真实场景
                try {
                    Thread.sleep(random.nextInt(1000) + 100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            log.info("Successfully sent {} test events", count);
        } catch (Exception e) {
            log.error("Failed to send test data to Kafka", e);
        }
    }

    /**
     * 生成指定数量的测试事件
     */
    public void generateTestEvents(int count) {
        log.info("Generating {} test events...", count);

        for (int i = 0; i < count; i++) {
            UserRegistrationEvent event = generateRandomEvent();
            log.info("Generated event {}: {}", i + 1, JSON.toJSONString(event));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        log.info("Generated {} test events successfully", count);
    }
}
