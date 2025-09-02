# 实时SQL开发指南

## 概述

本文档介绍如何在BanyuMall Flink项目中开发新的实时SQL作业。当前架构采用**模板方法模式**，让新功能开发变得简单高效。

## 当前数据流架构

### 🏗️ 整体架构图

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Kafka Source  │───▶│  Flink Job      │───▶│  Redis Sink     │
│   (多管道支持)    │    │  (Table API)    │    │  (多主题支持)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   application   │    │  BaseFlinkJob   │    │  RedisDataSink   │
│   .yml配置       │    │  Service基类    │    │  Factory工厂     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 📊 数据流详解

#### 1. 数据输入层 (Kafka)
- **多管道支持**：支持 `user`、`order`、`payment` 等不同业务管道
- **配置灵活**：每个管道可独立配置 `group-id`、`topic`、`consumer` 参数
- **自动反序列化**：使用 `GenericJsonDeserializer` 自动处理JSON数据

#### 2. 数据处理层 (Flink)
- **Table API**：使用Flink Table API进行SQL化处理
- **窗口聚合**：支持滚动窗口、滑动窗口等时间窗口聚合
- **检查点机制**：自动启用检查点，保证数据一致性

#### 3. 数据输出层 (Redis)
- **多主题支持**：支持不同主题的数据存储
- **缓存机制**：使用 `RedisDataSinkFactory` 缓存Sink实例
- **数据格式**：统一JSON格式，包含元数据信息

## 开发新实时SQL的步骤

### 第一步：定义数据实体

创建输入和输出的数据实体类：

```java
// 输入实体 - 例如：订单事件
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private LocalDateTime createTime;
    private String status;
}

// 输出实体 - 例如：订单汇总
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummary {
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
    private Long totalOrders;
    private BigDecimal totalAmount;
    private BigDecimal avgAmount;
    private LocalDateTime processTime;
    private String windowType;
}
```

### 第二步：创建Job逻辑类

继承现有模式，创建具体的Job实现：

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAnalysisJob {
    
    private final FlinkConfig flinkConfig;
    private final KafkaSourceBuilder kafkaSourceBuilder;
    private final RedisDataSinkFactory redisSinkFactory;

    public JobID execute(StreamExecutionEnvironment env) throws Exception {
        log.info("Starting Order Analysis Job...");

        // 1. 创建表环境
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 2. 设置并行度
        env.setParallelism(flinkConfig.getParallelism().getDefaultParallelism());

        // 3. 启用检查点
        if (flinkConfig.getCheckpoint().isEnabled()) {
            env.enableCheckpointing(flinkConfig.getCheckpoint().getInterval());
            env.getCheckpointConfig().setCheckpointTimeout(flinkConfig.getCheckpoint().getTimeout());
            env.getCheckpointConfig().setMinPauseBetweenCheckpoints(flinkConfig.getCheckpoint().getMinPause());
            env.getCheckpointConfig().setMaxConcurrentCheckpoints(flinkConfig.getCheckpoint().getMaxConcurrent());
        }

        // 4. 创建Kafka数据源
        KafkaSource<OrderEvent> source = kafkaSourceBuilder.buildKafkaSource(
                "order",  // 使用order管道配置
                OrderEvent.class
        );

        // 5. 创建数据流
        DataStream<OrderEvent> eventStream = env.fromSource(
                source,
                WatermarkStrategy
                        .<OrderEvent>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, timestamp) -> event.getCreateTime().toInstant(ZoneOffset.UTC).toEpochMilli()),
                "Order Event Source"
        );

        // 6. 转换为Table
        Table orderTable = tableEnv.fromDataStream(
                eventStream,
                Schema.newBuilder()
                        .column("orderId", DataTypes.STRING())
                        .column("userId", DataTypes.STRING())
                        .column("amount", DataTypes.DECIMAL(10, 2))
                        .column("createTime", DataTypes.TIMESTAMP(3))
                        .column("status", DataTypes.STRING())
                        .columnByExpression("proc_time", "PROCTIME()")
                        .build()
        );

        // 7. 注册临时视图
        tableEnv.createTemporaryView("orders", orderTable);

        // 8. 执行SQL查询
        String sql = """
                SELECT 
                    TUMBLE_START(createTime, INTERVAL '1' MINUTE) as window_start,
                    TUMBLE_END(createTime, INTERVAL '1' MINUTE) as window_end,
                    COUNT(*) as total_orders,
                    SUM(amount) as total_amount,
                    AVG(amount) as avg_amount
                FROM orders
                GROUP BY TUMBLE(createTime, INTERVAL '1' MINUTE)
                """;

        Table resultTable = tableEnv.sqlQuery(sql);

        // 9. 转换为DataStream
        DataStream<Row> resultStream = tableEnv.toDataStream(resultTable);

        // 10. 转换为OrderSummary对象
        DataStream<OrderSummary> summaryStream = resultStream.map(
                (MapFunction<Row, OrderSummary>) row -> OrderSummary.builder()
                        .windowStart(((LocalDateTime) row.getField("window_start")))
                        .windowEnd(((LocalDateTime) row.getField("window_end")))
                        .totalOrders((Long) row.getField("total_orders"))
                        .totalAmount((BigDecimal) row.getField("total_amount"))
                        .avgAmount((BigDecimal) row.getField("avg_amount"))
                        .processTime(LocalDateTime.now())
                        .windowType("TUMBLING_1_MINUTE")
                        .build()
        );

        // 11. 输出到Redis
        summaryStream.map(summary -> {
            JSONObject jsonData = (JSONObject) JSON.toJSON(summary);
            redisSinkFactory.getSink("order").accept(jsonData);
            return summary;
        }).name("Order Summary Redis Sink");

        // 12. 打印到控制台（调试用）
        summaryStream.print("Order Summary");

        // 13. 执行作业
        log.info("Order Analysis Job started successfully");
        return env.execute(flinkConfig.getJob().getName()).getJobID();
    }
}
```

### 第三步：创建Service类

继承 `BaseFlinkJobService`，实现模板方法：

```java
public class OrderAnalysisService extends BaseFlinkJobService {

    private final OrderAnalysisJob analysisJob;

    public OrderAnalysisService(OrderAnalysisJob analysisJob, FlinkConfig flinkConfig) {
        super(flinkConfig);
        this.analysisJob = analysisJob;
    }

    @Override
    public String getJobName() {
        return "order-analysis";
    }

    @Override
    public String getDescription() {
        return """
                订单实时分析服务

                功能：
                1. 从Kafka消费订单事件
                2. 使用Flink Table API进行实时分析
                3. 按1分钟时间窗口聚合订单数据
                4. 统计订单数量、总金额、平均金额
                5. 将结果输出到Redis

                数据格式：
                - 输入：订单事件（orderId, userId, amount, createTime, status）
                - 输出：订单汇总（windowStart, windowEnd, totalOrders, totalAmount, avgAmount）
                """;
    }

    @Override
    public int getPriority() {
        return 20; // 中等优先级
    }

    @Override
    public boolean isAutoStart() {
        return true; // 自动启动
    }

    @Override
    protected Function<StreamExecutionEnvironment, JobID> getJobLogic() {
        return env -> {
            try {
                return analysisJob.execute(env);
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute order analysis job", e);
            }
        };
    }
}
```

### 第四步：添加@Service注解

在Service类上添加 `@Service` 注解，让Spring自动扫描并创建Bean：

```java
@Service
public class OrderAnalysisService extends BaseFlinkJobService {
    // ... 现有代码 ...
}
```
```

### 第五步：配置Kafka管道

在 `application.yml` 中添加新的管道配置：

```yaml
kafka:
  # ... 现有配置 ...
  pipelines:
    user:
      consumer:
        group-id: banyu-user-consumer
      topic:
        input: user-input-topic
        output: user-output-topic
        error: user-error-topic
    order:  # 新增order管道
      consumer:
        group-id: banyu-order-consumer
      topic:
        input: order-input-topic
        output: order-output-topic
        error: order-error-topic
```

## 配置说明

### Kafka配置

```yaml
kafka:
  bootstrap-servers: localhost:9092
  pipelines:
    # 业务管道配置
    user:
      consumer:
        group-id: banyu-user-consumer
      topic:
        input: user-input-topic
        output: user-output-topic
        error: user-error-topic
    order:
      consumer:
        group-id: banyu-order-consumer
      topic:
        input: order-input-topic
        output: order-output-topic
        error: order-error-topic
```

### Flink配置

```yaml
flink:
  job:
    name: banyu-realtime-processor
    description: Banyu实时数据处理作业
  checkpoint:
    interval: 60000  # 60秒
    timeout: 30000   # 30秒
    min-pause: 5000  # 5秒
    max-concurrent: 1
    storage-path: file:///tmp/flink-checkpoints
    enabled: true
  parallelism:
    default: 2
    source: 2
    sink: 2
    processor: 2
```

### Redis配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
    timeout: 5000ms
    lettuce:
      pool:
        max-active: 8
        max-wait: -1ms
        max-idle: 8
        min-idle: 0
```

## 数据存储格式

### Redis存储结构

```
# 最新数据
flink:{topic}:latest = {
  "windowStart": "2024-01-01T10:00:00",
  "windowEnd": "2024-01-01T10:01:00",
  "totalOrders": 100,
  "totalAmount": 10000.00,
  "avgAmount": 100.00,
  "processTime": "2024-01-01T10:01:00",
  "windowType": "TUMBLING_1_MINUTE",
  "topic": "order",
  "timestamp": 1704096000000,
  "dataSource": "flink-realtime",
  "sinkTime": 1704096000000
}

# 历史数据（最近100条）
flink:{topic}:history = [最新数据1, 最新数据2, ...]
```

## 开发最佳实践

### 1. 命名规范

- **Job类**：`{Business}AnalysisJob`（如：`OrderAnalysisJob`）
- **Service类**：`{Business}AnalysisService`（如：`OrderAnalysisService`）
- **实体类**：`{Business}Event`、`{Business}Summary`（如：`OrderEvent`、`OrderSummary`）
- **管道名**：小写业务名（如：`order`、`user`、`payment`）

### 2. 配置管理

- **管道配置**：在 `application.yml` 中配置 `kafka.pipelines.{pipelineName}`
- **Bean配置**：使用 `@Service` 注解自动创建Bean
- **优先级配置**：在Service类中设置 `getPriority()` 返回值

### 3. 错误处理

- **Job执行**：使用try-catch包装，抛出RuntimeException
- **Redis存储**：在 `RedisDataSink` 中静默处理异常，避免影响Flink作业
- **日志记录**：使用 `@Slf4j` 记录关键操作和错误信息

### 4. 性能优化

- **并行度设置**：根据数据量调整 `flink.parallelism` 配置
- **检查点间隔**：根据业务需求调整 `flink.checkpoint.interval`
- **缓存机制**：使用 `RedisDataSinkFactory` 缓存Sink实例

## 监控和调试

### 1. 日志监控

```java
// 在Job类中添加关键日志
log.info("Starting Order Analysis Job...");
log.info("Processing {} records in window", recordCount);
log.info("Order Analysis Job completed successfully");
```

### 2. Flink Web UI

- **访问地址**：`http://localhost:8081`
- **功能**：查看Job状态、监控指标、查看日志
- **操作**：启动、停止、重启Job

### 3. Redis数据查看

```bash
# 查看最新数据
redis-cli get flink:order:latest

# 查看历史数据
redis-cli lrange flink:order:history 0 -1

# 查看所有相关key
redis-cli keys flink:order:*
```

## 常见问题

### Q1: 如何修改SQL查询？

A: 在Job类的 `execute` 方法中修改SQL字符串：

```java
String sql = """
    SELECT 
        TUMBLE_START(createTime, INTERVAL '5' MINUTE) as window_start,
        TUMBLE_END(createTime, INTERVAL '5' MINUTE) as window_end,
        COUNT(*) as total_orders,
        SUM(amount) as total_amount
    FROM orders
    WHERE status = 'COMPLETED'
    GROUP BY TUMBLE(createTime, INTERVAL '5' MINUTE)
    """;
```

### Q2: 如何添加新的数据源？

A: 在 `application.yml` 中添加新的管道配置：

```yaml
kafka:
  pipelines:
    payment:
      consumer:
        group-id: banyu-payment-consumer
      topic:
        input: payment-input-topic
        output: payment-output-topic
        error: payment-error-topic
```

### Q3: 如何修改输出格式？

A: 修改输出实体类和转换逻辑：

```java
// 修改输出实体
@Data
@Builder
public class OrderSummary {
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
    private Long totalOrders;
    private BigDecimal totalAmount;
    private String businessType; // 新增字段
}

// 修改转换逻辑
DataStream<OrderSummary> summaryStream = resultStream.map(
    (MapFunction<Row, OrderSummary>) row -> OrderSummary.builder()
        .windowStart(((LocalDateTime) row.getField("window_start")))
        .windowEnd(((LocalDateTime) row.getField("window_end")))
        .totalOrders((Long) row.getField("total_orders"))
        .totalAmount((BigDecimal) row.getField("total_amount"))
        .businessType("order") // 设置业务类型
        .build()
);
```

## 数据查询API

### 通用数据查询接口

当前系统提供通用的数据查询API，支持查询任意主题的实时数据：

#### 1. 获取最新数据
```http
GET /api/data/latest/{topicName}
```

**示例**：
- `GET /api/data/latest/user` - 获取用户主题最新数据
- `GET /api/data/latest/order` - 获取订单主题最新数据
- `GET /api/data/latest` - 获取默认主题（user）最新数据

**响应格式**：
```json
{
  "code": 200,
  "message": "获取最新数据成功",
  "data": {
    "topic": "user",
    "timestamp": 1704067200000,
    "dataSource": "flink-realtime",
    "sinkTime": 1704067200000,
    "processTime": 1704067200000,
    "windowStart": "2024-01-01T10:00:00",
    "windowEnd": "2024-01-01T10:01:00",
    "totalRegistrations": 150,
    "maleCount": 80,
    "femaleCount": 60,
    "unknownCount": 10
  }
}
```

#### 2. 获取历史数据
```http
GET /api/data/history/{topicName}
```

**示例**：
- `GET /api/data/history/user` - 获取用户主题历史数据
- `GET /api/data/history/order` - 获取订单主题历史数据
- `GET /api/data/history` - 获取默认主题（user）历史数据

**响应格式**：
```json
{
  "code": 200,
  "message": "获取历史数据成功",
  "data": [
    {
      "topic": "user",
      "timestamp": 1704067200000,
      "dataSource": "flink-realtime",
      "sinkTime": 1704067200000,
      "processTime": 1704067200000,
      "windowStart": "2024-01-01T10:00:00",
      "windowEnd": "2024-01-01T10:01:00",
      "totalRegistrations": 150,
      "maleCount": 80,
      "femaleCount": 60,
      "unknownCount": 10
    }
  ]
}
```

#### 3. 调试接口
```http
GET /api/data/debug/keys
```

**响应格式**：
```json
{
  "code": 200,
  "message": "获取Redis键信息成功",
  "data": [
    "flink:user:latest",
    "flink:user:history",
    "flink:order:latest",
    "flink:order:history"
  ]
}
```

### API特点

- **通用性**：支持任意主题的数据查询
- **标准化**：统一的JSON响应格式
- **元数据丰富**：包含时间戳、数据源等信息
- **多主题支持**：可查询不同业务主题的数据

## 总结

通过遵循本指南，你可以快速开发新的实时SQL作业：

1. **定义数据实体**：输入和输出数据结构
2. **实现Job逻辑**：使用Flink Table API处理数据
3. **创建Service类**：继承BaseFlinkJobService并添加@Service注解
4. **配置管道**：在application.yml中配置Kafka管道
5. **测试和监控**：使用Flink Web UI和Redis工具监控

当前架构的优势：
- **模板方法模式**：减少重复代码
- **多管道支持**：灵活的数据源配置
- **统一输出格式**：标准化的Redis存储
- **自动启动机制**：应用启动时自动执行Job
- **通用API**：支持任意主题的数据查询

**"简单就是美，复杂就是垃圾"** - 让新功能开发变得简单高效！
