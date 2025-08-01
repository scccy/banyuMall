# core-publisher 发布者 模块设计文档

> **文档位置**: `infra/moudleDocs/core-publisher/design.md`
> **作者**: scccy
> **创建时间**: 2025-08-01

## 1. 模块概述

### 1.1 微服务模块级别信息
- **当前微服务模块级别**: core-publisher（发布者服务）
- **父模块**: core（核心层）
- **模块类型**: 任务发布管理服务

### 1.2 模块定位
core-publisher 模块是 banyuMall 系统的核心发布者模块，负责管理任务发布、审核、参与等核心业务流程。

### 1.3 主要功能
- **任务发布管理**: 创建、更新、删除、查询任务
- **任务审核流程**: 任务审核、审核历史管理
- **社群分享审核**: 社群分享内容的审核管理
- **任务参与管理**: 用户参与任务、完成任务、参与记录查询

### 1.4 技术栈
- **框架**: Spring Boot 3.x
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus
- **缓存**: Redis
- **文档**: Knife4j (OpenAPI 3.0)
- **认证**: JWT
- **服务发现**: Nacos

## 2. 接口设计

### 2.1 接口概览

| 序号 | 接口名称 | 请求方法 | 接口路径 | 功能描述 | 职责对应 | 是否需要Feign客户端 | 详细说明 |
|------|----------|----------|----------|----------|----------|-------------------|----------|
| 1 | 创建任务 | POST | `/core/publisher/tasks` | 创建新的任务 | 任务发布管理 | 否 | [查看详情](#51-任务管理接口) |
| 2 | 更新任务 | PUT | `/core/publisher/tasks/{id}` | 更新任务信息 | 任务发布管理 | 否 | [查看详情](#51-任务管理接口) |
| 3 | 删除任务 | DELETE | `/core/publisher/tasks/{id}` | 删除任务 | 任务发布管理 | 否 | [查看详情](#51-任务管理接口) |
| 4 | 获取任务详情 | GET | `/core/publisher/tasks/{id}` | 获取任务详细信息 | 任务发布管理 | 否 | [查看详情](#51-任务管理接口) |
| 5 | 分页查询任务列表 | GET | `/core/publisher/tasks` | 分页查询任务列表 | 任务发布管理 | 否 | [查看详情](#51-任务管理接口) |
| 6 | 提交任务审核 | POST | `/core/publisher/tasks/{id}/submit` | 提交任务进行审核 | 任务审核流程 | 否 | [查看详情](#52-任务审核接口) |
| 7 | 审核任务 | POST | `/core/publisher/tasks/{id}/review` | 审核任务 | 任务审核流程 | 否 | [查看详情](#52-任务审核接口) |
| 8 | 获取审核历史 | GET | `/core/publisher/tasks/{id}/review-history` | 获取任务审核历史 | 任务审核流程 | 否 | [查看详情](#52-任务审核接口) |
| 9 | 提交社群分享审核 | POST | `/core/publisher/share-reviews` | 提交社群分享审核申请 | 任务审核流程 | 否 | [查看详情](#53-社群分享审核接口) |
| 10 | 审核社群分享 | PUT | `/core/publisher/share-reviews/{id}/review` | 审核社群分享申请 | 任务审核流程 | 否 | [查看详情](#53-社群分享审核接口) |
| 11 | 查询社群分享审核列表 | GET | `/core/publisher/share-reviews` | 查询社群分享审核列表 | 任务审核流程 | 否 | [查看详情](#53-社群分享审核接口) |


### 2.2 接口分类

#### 2.2.1 任务发布管理
- 任务的增删改查操作
- 支持多种任务类型：点赞、评论、讨论、分享、邀请、反馈、排行榜

#### 2.2.2 任务审核流程
- 任务审核状态管理
- 审核历史记录
- 社群分享内容审核



## 3. 数据模型设计

### 3.1 数据库表结构
参考: [publisher-schema-optimized.sql](../../database/data/publisher/publisher-schema-optimized.sql)

> **注意**: 数据模型设计直接指向 `infra/database/data/` 目录下的SQL文件，不重复定义表结构。

### 3.2 核心表结构

#### 3.2.1 任务相关表
- **publisher_task**: 任务基础表 - 存储所有任务的基本信息
- **publisher_like_task**: 点赞任务表 - 存储点赞任务特有信息
- **publisher_comment_task**: 评论任务表 - 存储评论任务特有信息
- **publisher_discuss_task**: 讨论任务表 - 存储讨论任务特有信息
- **publisher_share_task**: 社群分享任务表 - 存储社群分享任务特有信息
- **publisher_invite_task**: 邀请任务表 - 存储邀请任务特有信息
- **publisher_feedback_task**: 反馈任务表 - 存储反馈任务特有信息
- **publisher_ranking_task**: 排行榜任务表 - 存储排行榜任务特有信息

#### 3.2.2 审核相关表
- **publisher_task_review**: 任务审核记录表 - 存储任务审核历史
- **publisher_share_review**: 社群分享审核表 - 存储社群分享任务的具体审核信息



### 3.3 表关系设计

#### 3.3.1 主从关系
- **publisher_task** (主表) ← **publisher_*_task** (从表)
  - 通过 `task_id` 字段关联
  - 一对多关系

#### 3.3.2 审核关系
- **publisher_task** ← **publisher_task_review**
  - 通过 `task_id` 字段关联
  - 一对多关系

- **publisher_share_task** ← **publisher_share_review**
  - 通过 `task_id` 字段关联
  - 一对一关系



## 4. 实体类设计

### 4.1 核心实体类

#### 4.1.1 任务基础实体
```java
@Data
@TableName("publisher_task")
public class PublisherTask extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_name")
    private String taskName;
    
    @TableField("task_type")
    private String taskType;
    
    @TableField("task_description")
    private String taskDescription;
    
    @TableField("reward_amount")
    private BigDecimal rewardAmount;
    
    @TableField("publisher_id")
    private String publisherId;
    
    @TableField("status")
    private String status;
    
    @TableField("start_time")
    private LocalDateTime startTime;
    
    @TableField("end_time")
    private LocalDateTime endTime;
    
    @TableField("max_participants")
    private Integer maxParticipants;
    
    @TableField("current_participants")
    private Integer currentParticipants;
}
```

#### 4.1.2 点赞任务实体
```java
@Data
@TableName("publisher_like_task")
public class PublisherLikeTask extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("target_url")
    private String targetUrl;
    
    @TableField("like_count")
    private Integer likeCount;
    
    @TableField("comment_required")
    private Boolean commentRequired;
}
```

#### 4.1.3 评论任务实体
```java
@Data
@TableName("publisher_comment_task")
public class PublisherCommentTask extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("target_url")
    private String targetUrl;
    
    @TableField("comment_template")
    private String commentTemplate;
    
    @TableField("min_comment_length")
    private Integer minCommentLength;
    
    @TableField("max_comment_length")
    private Integer maxCommentLength;
    
    @TableField("comment_count")
    private Integer commentCount;
}
```

#### 4.1.4 社群分享任务实体
```java
@Data
@TableName("publisher_share_task")
public class PublisherShareTask extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("share_content")
    private String shareContent;
    
    @TableField("share_platform")
    private String sharePlatform;
    
    @TableField("share_url")
    private String shareUrl;
    
    @TableField("screenshot_required")
    private Boolean screenshotRequired;
    
    @TableField("share_count")
    private Integer shareCount;
}
```

#### 4.1.5 任务审核实体
```java
@Data
@TableName("publisher_task_review")
public class PublisherTaskReview extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("reviewer_id")
    private String reviewerId;
    
    @TableField("review_status")
    private String reviewStatus;
    
    @TableField("review_comment")
    private String reviewComment;
    
    @TableField("review_time")
    private LocalDateTime reviewTime;
}
```

#### 4.1.6 社群分享审核实体
```java
@Data
@TableName("publisher_share_review")
public class PublisherShareReview extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("task_id")
    private String taskId;
    
    @TableField("share_content")
    private String shareContent;
    
    @TableField("share_platform")
    private String sharePlatform;
    
    @TableField("share_url")
    private String shareUrl;
    
    @TableField("screenshot_url")
    private String screenshotUrl;
    
    @TableField("reviewer_id")
    private String reviewerId;
    
    @TableField("review_status")
    private String reviewStatus;
    
    @TableField("review_comment")
    private String reviewComment;
}
```

### 4.2 DTO类设计

#### 4.2.1 任务创建请求DTO
```java
@Data
public class TaskCreateRequest {
    
    @NotBlank(message = "任务名称不能为空")
    private String taskName;
    
    @NotBlank(message = "任务类型不能为空")
    private String taskType;
    
    private String taskDescription;
    
    @NotNull(message = "奖励金额不能为空")
    @DecimalMin(value = "0.01", message = "奖励金额必须大于0")
    private BigDecimal rewardAmount;
    
    @NotBlank(message = "发布者ID不能为空")
    private String publisherId;
    
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    
    @Min(value = 1, message = "最大参与人数必须大于0")
    private Integer maxParticipants;
    
    // 任务类型特定字段
    private String targetUrl;
    private String commentTemplate;
    private String shareContent;
    private String sharePlatform;
    private String shareUrl;
    private Boolean screenshotRequired;
    private Boolean commentRequired;
    private Integer minCommentLength;
    private Integer maxCommentLength;
}
```

#### 4.2.2 任务查询请求DTO
```java
@Data
public class TaskQueryRequest {
    
    private String taskName;
    private String taskType;
    private String publisherId;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;
    
    @Min(value = 1, message = "页大小必须大于0")
    @Max(value = 100, message = "页大小不能超过100")
    private Integer pageSize = 10;
}
```

#### 4.2.3 任务审核请求DTO
```java
@Data
public class TaskReviewRequest {
    
    @NotBlank(message = "审核状态不能为空")
    private String reviewStatus;
    
    private String reviewComment;
    
    @NotBlank(message = "审核人ID不能为空")
    private String reviewerId;
}
```

## 5. 接口详细设计

### 5.1 任务管理接口

#### 5.1.1 创建任务
- **接口路径**: `POST /core-publisher/tasks`
- **功能描述**: 创建新的任务
- **请求参数**: TaskCreateRequest
- **响应结果**: ApiResponse<PublisherTask>
- **权限要求**: 需要登录，发布者权限

#### 5.1.2 更新任务
- **接口路径**: `PUT /core-publisher/tasks/{id}`
- **功能描述**: 更新任务信息
- **请求参数**: TaskCreateRequest
- **响应结果**: ApiResponse<PublisherTask>
- **权限要求**: 需要登录，任务发布者权限

#### 5.1.3 删除任务
- **接口路径**: `DELETE /core-publisher/tasks/{id}`
- **功能描述**: 删除任务
- **请求参数**: 路径参数id
- **响应结果**: ApiResponse<Boolean>
- **权限要求**: 需要登录，任务发布者权限

#### 5.1.4 获取任务详情
- **接口路径**: `GET /core-publisher/tasks/{id}`
- **功能描述**: 获取任务详细信息
- **请求参数**: 路径参数id
- **响应结果**: ApiResponse<PublisherTask>
- **权限要求**: 需要登录

#### 5.1.5 分页查询任务列表
- **接口路径**: `GET /core-publisher/tasks`
- **功能描述**: 分页查询任务列表
- **请求参数**: TaskQueryRequest
- **响应结果**: ApiResponse<PageResult<PublisherTask>>
- **权限要求**: 需要登录

### 5.2 任务审核接口

#### 5.2.1 提交任务审核
- **接口路径**: `POST /core-publisher/tasks/{id}/submit`
- **功能描述**: 提交任务进行审核
- **请求参数**: 路径参数id
- **响应结果**: ApiResponse<Boolean>
- **权限要求**: 需要登录，任务发布者权限

#### 5.2.2 审核任务
- **接口路径**: `POST /core-publisher/tasks/{id}/review`
- **功能描述**: 审核任务
- **请求参数**: TaskReviewRequest
- **响应结果**: ApiResponse<Boolean>
- **权限要求**: 需要登录，审核员权限

#### 5.2.3 获取审核历史
- **接口路径**: `GET /core-publisher/tasks/{id}/review-history`
- **功能描述**: 获取任务审核历史
- **请求参数**: 路径参数id
- **响应结果**: ApiResponse<List<PublisherTaskReview>>
- **权限要求**: 需要登录

### 5.3 社群分享审核接口

#### 5.3.1 提交社群分享审核
- **接口路径**: `POST /core-publisher/share-reviews`
- **功能描述**: 提交社群分享审核申请
- **请求参数**: PublisherShareReview
- **响应结果**: ApiResponse<PublisherShareReview>
- **权限要求**: 需要登录

#### 5.3.2 审核社群分享
- **接口路径**: `PUT /core-publisher/share-reviews/{id}/review`
- **功能描述**: 审核社群分享申请
- **请求参数**: TaskReviewRequest
- **响应结果**: ApiResponse<Boolean>
- **权限要求**: 需要登录，审核员权限

#### 5.3.3 查询社群分享审核列表
- **接口路径**: `GET /core-publisher/share-reviews`
- **功能描述**: 查询社群分享审核列表
- **请求参数**: 查询参数
- **响应结果**: ApiResponse<PageResult<PublisherShareReview>>
- **权限要求**: 需要登录



## 6. 服务层设计

### 6.1 核心服务接口

#### 6.1.1 任务服务
```java
public interface PublisherTaskService {
    
    PublisherTask createTask(TaskCreateRequest request);
    
    PublisherTask updateTask(String id, TaskCreateRequest request);
    
    boolean deleteTask(String id);
    
    PublisherTask getTaskById(String id);
    
    PageResult<PublisherTask> queryTasks(TaskQueryRequest request);
    
    boolean submitForReview(String id);
    
    boolean reviewTask(String id, TaskReviewRequest request);
    
    List<PublisherTaskReview> getReviewHistory(String id);
}
```

#### 6.1.2 社群分享审核服务
```java
public interface PublisherShareReviewService {
    
    PublisherShareReview submitShareReview(PublisherShareReview review);
    
    boolean reviewShareReview(String id, TaskReviewRequest request);
    
    PageResult<PublisherShareReview> queryShareReviews(ShareReviewQueryRequest request);
}
```



### 6.2 服务实现要点

#### 6.2.1 事务管理
- 任务创建、更新、删除操作需要事务保护
- 审核流程需要事务保护
- 任务参与需要事务保护

#### 6.2.2 缓存策略
- 任务详情缓存
- 任务列表缓存
- 审核状态缓存

#### 6.2.3 异常处理
- 业务异常处理
- 数据验证异常处理
- 权限异常处理

## 7. 控制器设计

### 7.1 任务控制器
```java
@RestController
@RequestMapping("/core/publisher/tasks")
@Api(tags = "任务管理")
public class PublisherTaskController {
    
    @PostMapping
    @ApiOperation("创建任务")
    public ApiResponse<PublisherTask> createTask(@RequestBody @Valid TaskCreateRequest request) {
        // 实现逻辑
    }
    
    @PutMapping("/{id}")
    @ApiOperation("更新任务")
    public ApiResponse<PublisherTask> updateTask(@PathVariable String id, 
                                                @RequestBody @Valid TaskCreateRequest request) {
        // 实现逻辑
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation("删除任务")
    public ApiResponse<Boolean> deleteTask(@PathVariable String id) {
        // 实现逻辑
    }
    
    @GetMapping("/{id}")
    @ApiOperation("获取任务详情")
    public ApiResponse<PublisherTask> getTaskById(@PathVariable String id) {
        // 实现逻辑
    }
    
    @GetMapping
    @ApiOperation("分页查询任务列表")
    public ApiResponse<PageResult<PublisherTask>> queryTasks(TaskQueryRequest request) {
        // 实现逻辑
    }
    
    @PostMapping("/{id}/submit")
    @ApiOperation("提交任务审核")
    public ApiResponse<Boolean> submitForReview(@PathVariable String id) {
        // 实现逻辑
    }
    
    @PostMapping("/{id}/review")
    @ApiOperation("审核任务")
    public ApiResponse<Boolean> reviewTask(@PathVariable String id, 
                                          @RequestBody @Valid TaskReviewRequest request) {
        // 实现逻辑
    }
    
    @GetMapping("/{id}/review-history")
    @ApiOperation("获取审核历史")
    public ApiResponse<List<PublisherTaskReview>> getReviewHistory(@PathVariable String id) {
        // 实现逻辑
    }
    

}
```

### 7.2 社群分享审核控制器
```java
@RestController
@RequestMapping("/core/publisher/share-reviews")
@Api(tags = "社群分享审核")
public class PublisherShareReviewController {
    
    @PostMapping
    @ApiOperation("提交社群分享审核")
    public ApiResponse<PublisherShareReview> submitShareReview(@RequestBody @Valid PublisherShareReview review) {
        // 实现逻辑
    }
    
    @PutMapping("/{id}/review")
    @ApiOperation("审核社群分享")
    public ApiResponse<Boolean> reviewShareReview(@PathVariable String id, 
                                                 @RequestBody @Valid TaskReviewRequest request) {
        // 实现逻辑
    }
    
    @GetMapping
    @ApiOperation("查询社群分享审核列表")
    public ApiResponse<PageResult<PublisherShareReview>> queryShareReviews(ShareReviewQueryRequest request) {
        // 实现逻辑
    }
}
```

## 8. 配置管理

### 8.1 应用配置
```yaml
spring:
  application:
    name: core-publisher
  datasource:
    url: jdbc:mysql://localhost:3306/banyu_mall?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: 0

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

knife4j:
  enable: true
  setting:
    language: zh-CN
    enable-swagger-models: true
    enable-document-manage: true
    swagger-model-name: 实体类列表
    enable-version: false
    enable-reload-cache-parameter: false
    enable-after-script: true
    enable-filter-multipart-api-method-type: POST
    enable-filter-multipart-apis: false
    enable-request-cache: true
    enable-host: false
    enable-host-text: ""
    enable-home-custom: false
    home-custom-path: ""
    enable-search: true
    enable-footer: false
    enable-footer-custom: true
    footer-custom-content: Apache License 2.0 | Copyright  2024-[banyuMall](https://github.com/scccy/banyuMall)
    enable-dynamic-parameter: false
    enable-debug: true
    enable-open-api: false
    enable-group: true
```

### 8.2 日志配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Properties>
        <Property name="PID">????</Property>
        <Property name="LOG_PATTERN">%clr{%d{yyyy-MM-dd HH:mm:ss.SSS}}{faint} %clr{%5p} %clr{%t} %clr{---} %clr{[%15.15t]} %clr{%-40.40c{1.}} %clr{:} %m%n%xwEx</Property>
    </Properties>
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT" follow="true">
            <PatternLayout pattern="${LOG_PATTERN}"/>
        </Console>
        <RollingFile name="RollingFile" fileName="logs/core-publisher.log"
                     filePattern="logs/core-publisher-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <TimeBasedTriggeringPolicy />
                <SizeBasedTriggeringPolicy size="100 MB"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
    </Appenders>
    <Loggers>
        <Logger name="com.origin.publisher" level="DEBUG" additivity="false">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="RollingFile"/>
        </Logger>
        <Root level="INFO">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="RollingFile"/>
        </Root>
    </Loggers>
</Configuration>
```

## 9. 部署配置

### 9.1 Docker配置
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/core-publisher-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 9.2 启动脚本
```bash
#!/bin/bash

# 设置JVM参数
JVM_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# 设置应用参数
APP_OPTS="--spring.profiles.active=prod"

# 启动应用
java $JVM_OPTS -jar core-publisher.jar $APP_OPTS
```

## 10. 测试策略

### 10.1 单元测试
- 服务层业务逻辑测试
- 数据访问层测试
- 工具类测试

### 10.2 集成测试
- 接口功能测试
- 数据库操作测试
- 缓存操作测试

### 10.3 性能测试
- 接口响应时间测试
- 并发处理能力测试
- 数据库性能测试

## 11. 监控与运维

### 11.1 健康检查
- 应用健康状态检查
- 数据库连接检查
- Redis连接检查

### 11.2 指标监控
- 接口调用次数
- 接口响应时间
- 错误率统计

### 11.3 日志监控
- 错误日志监控
- 业务日志监控
- 性能日志监控

## 12. 安全考虑

### 12.1 认证授权
- JWT Token验证
- 角色权限控制
- 接口权限控制

### 12.2 数据安全
- 敏感数据加密
- SQL注入防护
- XSS攻击防护

### 12.3 接口安全
- 请求频率限制
- 参数验证
- 异常信息脱敏

## 13. 版本规划

### 13.1 v1.0.0 (当前版本)
- 基础任务管理功能
- 任务审核流程
- 社群分享审核
- 任务参与管理

### 13.2 v1.1.0 (计划中)
- 任务模板功能
- 批量操作功能
- 高级搜索功能
- 数据统计功能

### 13.3 v1.2.0 (计划中)
- 任务推荐算法
- 智能审核功能
- 移动端适配
- 国际化支持

## 14. 总结

core-publisher 模块作为 banyuMall 系统的核心发布者模块，承担着任务发布、审核、参与等关键业务功能。通过合理的架构设计、完善的接口定义、规范的数据库设计，为系统提供了稳定可靠的任务管理能力。

该模块采用微服务架构，支持水平扩展，具备良好的可维护性和可扩展性。通过完善的测试策略、监控体系和安全措施，确保系统的稳定运行和数据安全。

