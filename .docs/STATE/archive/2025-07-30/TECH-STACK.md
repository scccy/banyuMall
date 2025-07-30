# 技术栈基线

## 核心技术栈

### 开发语言
- **Java**: 21 (LTS版本)
- **构建工具**: Maven 3.8+

### 核心框架
- **Spring Boot**: 3.x
- **Spring Cloud**: 2023.x
- **Spring Security**: 6.x
- **Spring Data JPA**: 3.x

### ORM框架
- **主数据库**: MySQL 8.0+
- **缓存数据库**: Redis 7.x
- **ORM框架**: MyBatis-Plus 3.5.12+

### 服务治理
- **服务注册发现**: Nacos 2.x
- **配置中心**: Nacos 2.x
- **API网关**: Spring Cloud Gateway
- **负载均衡**: Spring Cloud LoadBalancer

### 安全认证
- **JWT**: jjwt 0.12.x
- **加密**: BCrypt
- **黑名单管理**: Redis

### 文件存储
- **对象存储**: 阿里云OSS
- **文件处理**: Apache Commons IO
- **OSS SDK**: aliyun-sdk-oss 3.17.4

### 消息队列
- **消息中间件**: Kafka (可选)
- **消息处理**: Spring Cloud Stream

### 监控日志
- **日志框架**: Logback
- **链路追踪**: Sleuth + Zipkin
- **监控**: Micrometer + Prometheus

### 第三方集成
- **微信小程序**: 微信开放平台SDK
- **有赞商城**: 有赞开放平台SDK
- **阿里云OSS**: 阿里云OSS Java SDK
- **HTTP客户端**: OpenFeign + OkHttp

### 开发工具
- **IDE**: IntelliJ IDEA
- **API文档**: Swagger 3.x / OpenAPI 3.x
- **代码规范**: Checkstyle
- **单元测试**: JUnit 5 + Mockito

## 版本兼容性矩阵

| 组件 | 版本 | 兼容性说明 |
|------|------|------------|
| Spring Boot | 3.2.x | 基础框架 |
| Spring Cloud | 2023.0.x | 微服务框架 |
| Java | 21 | 运行时环境 |
| MySQL | 8.0+ | 主数据库 |
| Redis | 7.x | 缓存数据库 |
| Nacos | 2.3.x | 注册中心 |
| 阿里云OSS | 3.17.4 | 对象存储 |

## 依赖管理

### 父POM版本
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>
```

### MyBatis-Plus依赖管理
```xml
<!-- 在父POM的dependencyManagement中添加BOM管理 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-bom</artifactId>
    <version>${mybatis-plus.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>

<!-- 在子模块中使用，无需指定版本 -->
<!-- Spring Boot 3.x项目 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
</dependency>

<!-- JDK 11+项目 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-jsqlparser</artifactId>
</dependency>
```

### 阿里云OSS依赖
```xml
<!-- 阿里云OSS SDK -->
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.17.4</version>
</dependency>

<!-- Java 9+ 需要添加JAXB依赖 -->
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.1</version>
</dependency>
<dependency>
    <groupId>javax.activation</groupId>
    <artifactId>activation</artifactId>
    <version>1.1.1</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>2.3.3</version>
</dependency>
```

### Spring Cloud版本
```xml
<properties>
    <spring-cloud.version>2023.0.0</spring-cloud.version>
</properties>
```

## 开发环境要求

### 最低配置
- **JDK**: 21+
- **内存**: 8GB+
- **存储**: 20GB+
- **网络**: 稳定的互联网连接

### 推荐配置
- **JDK**: 21+
- **内存**: 16GB+
- **存储**: 50GB+ SSD
- **网络**: 稳定的互联网连接

## 部署环境

### 开发环境
- **操作系统**: macOS / Windows / Linux
- **容器**: Docker Desktop
- **数据库**: Docker容器化部署

### 生产环境
- **操作系统**: Linux (CentOS 7+ / Ubuntu 18+)
- **容器**: Docker + Kubernetes
- **数据库**: 独立服务器部署
- **负载均衡**: Nginx / HAProxy

## 性能基准

### 响应时间要求
- **API响应时间**: < 200ms (95%请求)
- **数据库查询**: < 50ms (简单查询)
- **缓存命中率**: > 90%
- **文件上传**: < 5MB/s

### 并发要求
- **并发用户**: 1000+
- **QPS**: 1000+
- **连接池**: 最大100个连接

## 安全配置

### 加密算法
- **密码加密**: BCrypt (cost=12)
- **JWT签名**: HMAC-SHA256
- **数据传输**: TLS 1.3

### 权限控制
- **角色权限**: RBAC模型
- **API权限**: 基于注解的权限控制
- **数据权限**: 行级权限控制

## 监控指标

### 应用监控
- **JVM指标**: 内存使用、GC频率
- **业务指标**: 任务完成率、积分发放量
- **性能指标**: 响应时间、吞吐量

### 基础设施监控
- **数据库**: 连接数、查询性能
- **缓存**: 命中率、内存使用
- **网络**: 带宽使用、延迟