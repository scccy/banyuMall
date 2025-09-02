package com.origin.banyu.coreflink.job;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.origin.banyu.coreflink.config.FlinkConfig;
import com.origin.banyu.coreflink.config.RedisDataSinkFactory;
import com.origin.banyu.coreflink.entity.RegistrationSummary;
import com.origin.banyu.coreflink.entity.UserRegistrationEvent;
import com.origin.banyu.coreflink.source.KafkaSourceBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 用户注册分析作业
 *
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegistrationAnalysisJob {


    private final FlinkConfig flinkConfig;
    private final KafkaSourceBuilder kafkaSourceBuilder;
    private final RedisDataSinkFactory redisSinkFactory;

    /**
     * 执行用户注册分析作业
     * @param env 执行环境
     * @return 作业ID
     */
    public JobID execute(StreamExecutionEnvironment env) throws Exception {
        log.info("Starting User Registration Analysis Job...");

        // 创建表环境
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 设置并行度
        env.setParallelism(flinkConfig.getParallelism().getDefaultParallelism());

        // 启用检查点
        if (flinkConfig.getCheckpoint().isEnabled()) {
            env.enableCheckpointing(flinkConfig.getCheckpoint().getInterval());
            env.getCheckpointConfig().setCheckpointTimeout(flinkConfig.getCheckpoint().getTimeout());
            env.getCheckpointConfig().setMinPauseBetweenCheckpoints(flinkConfig.getCheckpoint().getMinPause());
            env.getCheckpointConfig().setMaxConcurrentCheckpoints(flinkConfig.getCheckpoint().getMaxConcurrent());
        }

        // 创建Kafka数据源 - 使用user管道配置
        KafkaSource<UserRegistrationEvent> source = kafkaSourceBuilder.buildKafkaSource(
                "user", 
                UserRegistrationEvent.class
        );

        // 创建数据流
        DataStream<UserRegistrationEvent> eventStream = env.fromSource(
                source,
                WatermarkStrategy
                        .<UserRegistrationEvent>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, timestamp) -> event.getTimestamp()),
                "User Registration Source"
        );

        // 转换为Table
        Table eventTable = tableEnv.fromDataStream(
                eventStream,
                Schema.newBuilder()
                        .column("userId", DataTypes.STRING())
                        .column("registerTime", DataTypes.TIMESTAMP_WITH_LOCAL_TIME_ZONE(3))
                        .column("nickname", DataTypes.STRING())
                        .column("gender", DataTypes.STRING())
                        .column("timestamp", DataTypes.BIGINT())
                        .column("source", DataTypes.STRING())
                        .columnByExpression("proc_time", "PROCTIME()")
                        .watermark("registerTime", "registerTime - INTERVAL '5' SECOND")
                        .build()
        );

        // 注册表
        tableEnv.createTemporaryView("user_registrations", eventTable);

        // 执行SQL查询 - 按时间窗口聚合
        String sql = """
                SELECT
                    TUMBLE_START(registerTime, INTERVAL '1' MINUTE) as window_start,
                    TUMBLE_END(registerTime, INTERVAL '1' MINUTE) as window_end,
                    COUNT(*) as total_registrations,
                    COUNT(CASE WHEN gender = 'MALE' THEN 1 END) as male_count,
                    COUNT(CASE WHEN gender = 'FEMALE' THEN 1 END) as female_count,
                    COUNT(CASE WHEN gender = 'UNKNOWN' THEN 1 END) as unknown_count,
                    PROCTIME() as process_time
                FROM user_registrations
                GROUP BY TUMBLE(registerTime, INTERVAL '1' MINUTE)
                """;

        Table resultTable = tableEnv.sqlQuery(sql);

        // 转换为DataStream
        DataStream<Row> resultStream = tableEnv.toDataStream(resultTable);

        // 转换为RegistrationSummary对象
        DataStream<RegistrationSummary> summaryStream = resultStream.map(
                (MapFunction<Row, RegistrationSummary>) row -> RegistrationSummary.builder()
                        .windowStart(((LocalDateTime) row.getField("window_start")))
                        .windowEnd(((LocalDateTime) row.getField("window_end")))
                        .totalRegistrations((Long) row.getField("total_registrations"))
                        .maleCount((Long) row.getField("male_count"))
                        .femaleCount((Long) row.getField("female_count"))
                        .unknownCount((Long) row.getField("unknown_count"))
                        .processTime(LocalDateTime.now())
                        .windowType("TUMBLING_1_MINUTE")
                        .build()
        );

        // 输出到Redis - 供前端轮询查看（使用user主题）
        summaryStream.map(summary -> {
            // 使用FastJson直接转换实体类为JSONObject
            JSONObject jsonData = (JSONObject) JSON.toJSON(summary);
            // 使用工厂获取指定主题的sink
            redisSinkFactory.getSink("user").accept(jsonData);
            return summary;
        }).name("Registration Summary Redis Sink");

        // 打印到控制台（用于调试）
        summaryStream.print("Registration Summary");

        // 执行作业
        log.info("User Registration Analysis Job started successfully");
        return env.execute(flinkConfig.getJob().getName()).getJobID();
    }


}
