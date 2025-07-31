# Log4j2 故障排查指南

## 常见问题

### 1. 包扫描警告

**问题描述**:
```
WARN StatusConsoleListener The use of package scanning to locate plugins is deprecated and will be removed in a future release
```

**原因分析**:
- Log4j2在启动时会自动扫描包来定位插件
- 这个功能在新版本中已被弃用，将在未来版本中移除
- 警告信息表明使用了过时的包扫描机制

**解决方案**:

#### 方案1: 修改log4j2.xml配置（推荐）
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="ERROR" monitorInterval="30" strict="true">
    <!-- 禁用状态日志，避免包扫描警告 -->
    <StatusLogger level="ERROR" />
    
    <!-- 其他配置... -->
    
    <Loggers>
        <!-- 禁用Log4j2内部日志 -->
        <Logger name="org.apache.logging.log4j" level="ERROR" additivity="false">
            <AppenderRef ref="Console"/>
        </Logger>
        
        <Logger name="org.apache.logging.log4j.core" level="ERROR" additivity="false">
            <AppenderRef ref="Console"/>
        </Logger>
        
        <!-- 其他日志配置... -->
    </Loggers>
</Configuration>
```

#### 方案2: 使用JVM参数
```bash
# 设置JVM参数
export JAVA_OPTS="$JAVA_OPTS \
-Dlog4j2.disable.jmx=true \
-Dlog4j2.skipJansi=true \
-Dlog4j2.statusLogger.level=ERROR \
-Dlog4j2.configurationFactory=org.apache.logging.log4j.core.config.ConfigurationFactory \
-Dlog4j2.disableStatusLogger=true"

# 启动应用
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="$JAVA_OPTS"
```

#### 方案3: 使用启动脚本
```bash
# 使用提供的启动脚本
./start-dev.sh
```

#### 方案4: 在application.yml中配置
```yaml
logging:
  level:
    org.apache.logging.log4j.status: ERROR
    org.apache.logging.log4j.core.config: ERROR
```

### 2. 日志文件权限问题

**问题描述**:
```
ERROR Unable to create file logs/service-auth/service-auth.log
```

**解决方案**:
```bash
# 创建日志目录并设置权限
mkdir -p logs/service-auth
chmod 755 logs/service-auth
```

### 3. 异步日志性能问题

**问题描述**:
- 日志输出缓慢
- 应用启动时间过长

**解决方案**:
```xml
<!-- 调整异步日志配置 -->
<Async name="AsyncConsole" ringBufferSize="512">
    <AppenderRef ref="Console"/>
</Async>
```

### 4. 日志级别配置问题

**问题描述**:
- 日志输出过多或过少
- 无法看到特定包的日志

**解决方案**:
```xml
<Loggers>
    <!-- 设置特定包的日志级别 -->
    <Logger name="com.origin.auth" level="debug" additivity="false">
        <AppenderRef ref="AsyncConsole"/>
        <AppenderRef ref="AsyncFile"/>
    </Logger>
    
    <!-- 设置第三方库的日志级别 -->
    <Logger name="org.springframework" level="info" additivity="false">
        <AppenderRef ref="AsyncConsole"/>
        <AppenderRef ref="AsyncFile"/>
    </Logger>
</Loggers>
```

## 配置最佳实践

### 1. 开发环境配置
```xml
<Configuration status="ERROR" monitorInterval="30">
    <!-- 开发环境使用详细日志 -->
    <Logger name="com.origin" level="debug" additivity="false">
        <AppenderRef ref="AsyncConsole"/>
        <AppenderRef ref="AsyncFile"/>
    </Logger>
</Configuration>
```

### 2. 生产环境配置
```xml
<Configuration status="ERROR" monitorInterval="30">
    <!-- 生产环境使用信息级别日志 -->
    <Logger name="com.origin" level="info" additivity="false">
        <AppenderRef ref="AsyncFile"/>
    </Logger>
</Configuration>
```

### 3. 性能优化配置
```xml
<!-- 使用异步日志提高性能 -->
<Async name="AsyncFile" ringBufferSize="1024">
    <AppenderRef ref="FileAppender"/>
</Async>

<!-- 配置日志文件滚动策略 -->
<RollingFile name="FileAppender" 
             fileName="${LOG_FILE_PATH}/${SERVICE_NAME}.log"
             filePattern="${LOG_FILE_PATH}/${SERVICE_NAME}-%d{yyyy-MM-dd}-%i.log.gz">
    <Policies>
        <TimeBasedTriggeringPolicy/>
        <SizeBasedTriggeringPolicy size="100MB"/>
    </Policies>
    <DefaultRolloverStrategy max="10"/>
</RollingFile>
```

## 监控和调试

### 1. 检查日志配置
```bash
# 查看当前日志配置
curl -X GET "http://localhost:8081/actuator/loggers"

# 修改日志级别
curl -X POST "http://localhost:8081/actuator/loggers/com.origin.auth" \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```

### 2. 检查日志文件
```bash
# 查看日志文件
tail -f logs/service-auth/service-auth.log

# 查看错误日志
tail -f logs/service-auth/service-auth-error.log
```

### 3. 性能监控
```bash
# 检查日志写入性能
iostat -x 1

# 检查磁盘空间
df -h logs/
```

## 常见错误代码

| 错误代码 | 说明 | 解决方案 |
|----------|------|----------|
| ERROR | 严重错误 | 检查配置文件和权限 |
| WARN | 警告信息 | 通常可以忽略，但建议修复 |
| INFO | 信息日志 | 正常运行信息 |
| DEBUG | 调试信息 | 开发环境使用 |
| TRACE | 跟踪信息 | 详细调试使用 |

## 联系支持

如果遇到无法解决的问题，请：
1. 收集完整的错误日志
2. 记录系统环境信息
3. 提供配置文件内容
4. 联系技术支持团队 