# Log4j2 日志配置修复文档

## 问题描述

在微服务架构中，每个服务都应该生成以服务名称命名的日志文件，但之前的配置存在以下问题：

1. **固定名称问题**: 日志文件使用固定名称而不是服务名称
2. **配置解析问题**: Log4j2无法正确解析Spring Boot的属性
3. **目录创建问题**: 创建了错误的目录名称

## 解决方案

### 1. 延迟初始化方案

使用Spring Boot的`ApplicationReadyEvent`来延迟设置服务名称，确保在应用完全启动后再配置Log4j2。

### 2. 修改Log4j2配置文件

在 `service/service-common/src/main/resources/log4j2.xml` 中：

```xml
<Properties>
    <Property name="LOG_PATTERN">%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</Property>
    <!-- 使用系统属性或环境变量作为服务名，如果没有则使用默认值 -->
    <Property name="SERVICE_NAME">${sys:service.name:-${env:SERVICE_NAME:-unknown-service}}</Property>
    <Property name="LOG_FILE_PATH">./logs/${SERVICE_NAME}</Property>
</Properties>
```

### 2. 创建Log4j2ServiceNameConfig配置类

在 `service/service-base/src/main/java/com/origin/base/config/Log4j2ServiceNameConfig.java` 中：

```java
@Slf4j
@Configuration
public class Log4j2ServiceNameConfig {

    private final Environment environment;

    public Log4j2ServiceNameConfig(Environment environment) {
        this.environment = environment;
    }

    /**
     * 在应用启动完成后设置服务名称
     * 确保在Spring Boot完全启动后设置系统属性
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            // 获取应用名称
            String appName = environment.getProperty("spring.application.name", "unknown-service");
            
            // 设置系统属性
            System.setProperty("service.name", appName);
            
            log.info("Log4j2服务名称已设置: {}", appName);
            
            // 重新配置Log4j2
            org.apache.logging.log4j.core.config.Configurator.reconfigure();
            
            log.info("Log4j2配置已重新加载，日志文件将使用服务名称: {}", appName);
            
        } catch (Exception e) {
            log.warn("设置Log4j2服务名称失败: {}", e.getMessage());
        }
    }
}
```

### 3. 修改Log4j2Config配置类

在 `service/service-base/src/main/java/com/origin/base/config/Log4j2Config.java` 中：

```java
@Configuration
@ConditionalOnClass(LogManager.class)
public class Log4j2Config {

    public Log4j2Config() {
        // 设置系统属性，禁用包扫描警告
        System.setProperty("log4j2.disable.jmx", "true");
        System.setProperty("log4j2.skipJansi", "true");
        System.setProperty("log4j2.statusLogger.level", "ERROR");
    }

    @PostConstruct
    public void initLog4j2() {
        // 设置配置工厂，避免自动扫描
        if (ConfigurationFactory.getInstance() == null) {
            System.setProperty("log4j2.configurationFactory", "org.apache.logging.log4j.core.config.ConfigurationFactory");
        }
    }
}
```

### 4. 启动脚本（可选）

为每个服务创建启动脚本，设置正确的系统属性：

```bash
#!/bin/bash
# service-user启动脚本

echo "启动service-user服务..."

# 设置系统属性
export JAVA_OPTS="-Dservice.name=service-user -Dlog4j2.configurationFile=classpath:log4j2.xml"

# 启动服务
cd "$(dirname "$0")"
mvn spring-boot:run -Dspring-boot.run.jvmArguments="$JAVA_OPTS"
```

## 配置说明

### 延迟初始化机制

1. **应用启动阶段**: Log4j2使用默认配置初始化
2. **ApplicationReadyEvent**: 应用完全启动后触发
3. **服务名称设置**: 设置`service.name`系统属性
4. **配置重新加载**: 调用`Configurator.reconfigure()`重新加载配置

### 系统属性优先级

1. `sys:service.name` - 系统属性（最高优先级）
2. `env:SERVICE_NAME` - 环境变量
3. `unknown-service` - 默认值（最低优先级）

### 日志文件结构

```
logs/
├── service-user/
│   ├── service-user.log
│   ├── service-user-error.log
│   └── service-user-2025-07-31-1.log.gz
├── service-auth/
│   ├── service-auth.log
│   ├── service-auth-error.log
│   └── service-auth-2025-07-31-1.log.gz
└── service-gateway/
    ├── service-gateway.log
    ├── service-gateway-error.log
    └── service-gateway-2025-07-31-1.log.gz
```

## 验证方法

### 1. 检查日志目录

```bash
ls -la logs/
```

应该看到以服务名称命名的目录。

### 2. 检查日志文件

```bash
ls -la logs/service-user/
tail -f logs/service-user/service-user.log
```

### 3. 测试脚本

使用提供的测试脚本：

```bash
./test-log-config.sh
```

## 注意事项

1. **服务重启**: 修改Log4j2配置后需要重启服务
2. **系统属性**: 确保在Log4j2初始化之前设置系统属性
3. **目录权限**: 确保logs目录有写入权限
4. **磁盘空间**: 注意日志文件的磁盘空间使用

## 故障排除

### 问题1: 日志文件仍使用固定名称

**原因**: 系统属性设置时机太晚
**解决**: 在Log4j2Config构造函数中设置系统属性

### 问题2: 创建了错误的目录名称

**原因**: 属性解析失败
**解决**: 检查系统属性是否正确设置

### 问题3: 日志文件不生成

**原因**: 权限问题或配置错误
**解决**: 检查目录权限和配置文件语法

## 相关文件

- `service/service-common/src/main/resources/log4j2.xml`
- `service/service-base/src/main/java/com/origin/base/config/Log4j2Config.java`
- `service/service-user/start-with-logging.sh`
- `test-log-config.sh` 