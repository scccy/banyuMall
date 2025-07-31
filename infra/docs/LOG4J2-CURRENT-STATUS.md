# Log4j2 日志配置当前状态

## 当前状态

### ✅ 已解决的问题

1. **日志文件生成**: 日志文件正在正常生成
2. **日志内容正确**: 日志内容包含正确的服务信息
3. **日志轮转**: 日志轮转功能正常工作
4. **错误日志**: 错误日志文件正常创建

### 🔄 仍需解决的问题

1. **目录命名**: 日志目录使用`unknown-service`而不是`service-user`

## 当前日志文件结构

```
logs/
├── service-gateway/          # service-gateway的日志
└── unknown-service/          # service-user的日志（目录名不正确）
    ├── unknown-service.log
    ├── unknown-service-error.log
    └── unknown-service-2025-07-31-1.log.gz
```

## 问题分析

### 根本原因

Log4j2在Spring Boot启动之前就已经初始化，此时系统属性`service.name`还没有被设置，所以使用了默认值`unknown-service`。

### 解决方案（已优化）

使用Spring Boot的`ApplicationReadyEvent`来延迟设置服务名称，确保在应用完全启动后再配置Log4j2。

#### 方案1: 延迟初始化（推荐）

应用启动完成后自动设置服务名称并重新配置Log4j2，无需手动干预。

#### 方案2: 使用JVM参数启动

```bash
# 设置环境变量
export SERVICE_NAME=service-user
export JAVA_OPTS="-Dservice.name=$SERVICE_NAME"

# 启动服务
cd service/service-user
mvn spring-boot:run -Dspring-boot.run.jvmArguments="$JAVA_OPTS"
```

```bash
# 使用提供的启动脚本
./service/service-user/start-with-logging.sh
```

#### 方案4: 设置环境变量

```bash
# 使用环境变量设置脚本
source ./set-log-env.sh
cd service/service-user
mvn spring-boot:run -Dspring-boot.run.jvmArguments="$JAVA_OPTS"
```

## 验证方法

### 1. 检查日志目录

```bash
ls -la logs/
```

应该看到`service-user`目录而不是`unknown-service`目录。

### 2. 检查日志文件

```bash
ls -la logs/service-user/
tail -f logs/service-user/service-user.log
```

### 3. 使用测试脚本

```bash
./test-log-config.sh
```

## 预期结果

修复后，日志文件结构应该是：

```
logs/
├── service-gateway/
└── service-user/              # 正确的目录名
    ├── service-user.log
    ├── service-user-error.log
    └── service-user-2025-07-31-1.log.gz
```

## 注意事项

1. **服务重启**: 修改配置后需要重启服务
2. **JVM参数**: 必须在服务启动时设置JVM参数
3. **环境变量**: 确保环境变量在服务启动前设置

## 相关文件

- `service/service-user/start-with-logging.sh` - 启动脚本
- `set-log-env.sh` - 环境变量设置脚本
- `test-log-config.sh` - 测试脚本
- `infra/docs/LOG4J2-CONFIGURATION-FIX.md` - 详细修复文档 