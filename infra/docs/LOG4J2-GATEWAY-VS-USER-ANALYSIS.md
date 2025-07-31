# Log4j2 Gateway vs User 日志分析

## 问题现象

- **service-gateway**: 日志目录名称正确 (`logs/service-gateway/`)
- **service-user**: 日志目录名称错误 (`logs/unknown-service/`)

## 根本原因分析

### 1. 模块依赖差异

#### service-gateway
```xml
<!-- 只引入service-common，不引入service-base -->
<dependency>
    <groupId>com.origin</groupId>
    <artifactId>service-common</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

#### service-user
```xml
<!-- 引入service-base，包含Log4j2Config和Log4j2ServiceNameConfig -->
<dependency>
    <groupId>com.origin</groupId>
    <artifactId>service-base</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. 启动顺序影响

- **service-gateway**: 启动时间 `11:20` - 较早启动
- **service-user**: 启动时间 `23:58` - 较晚启动

### 3. 配置类影响

#### service-gateway
- ❌ 没有Log4j2Config
- ❌ 没有Log4j2ServiceNameConfig
- ✅ 直接使用通用log4j2.xml配置

#### service-user
- ✅ 有Log4j2Config
- ✅ 有Log4j2ServiceNameConfig
- ✅ 使用通用log4j2.xml配置

## 问题根源

### 为什么service-gateway日志名称正确？

1. **启动时机**: service-gateway在较早时间启动，可能通过某种方式获取了正确的服务名称
2. **配置简单**: 没有复杂的配置类干扰，直接使用log4j2.xml
3. **环境状态**: 启动时系统环境可能处于某种特定状态

### 为什么service-user日志名称错误？

1. **配置类干扰**: Log4j2Config和Log4j2ServiceNameConfig可能影响了系统属性的设置
2. **启动时机**: 在较晚时间启动，系统属性可能已经被重置
3. **依赖复杂性**: 引入了service-base模块，增加了配置复杂性

## 解决方案

### 方案1: 统一配置（推荐）

让所有服务都使用相同的配置方式：

1. **移除service-user的service-base依赖**
2. **统一使用通用log4j2.xml配置**
3. **通过启动脚本设置系统属性**

### 方案2: 修复当前配置

1. **确保Log4j2ServiceNameConfig正确工作**
2. **在ApplicationReadyEvent中正确设置系统属性**
3. **重新配置Log4j2**

### 方案3: 环境变量方案

1. **设置环境变量SERVICE_NAME**
2. **修改log4j2.xml使用环境变量**
3. **在启动脚本中设置环境变量**

## 验证方法

### 检查当前状态

```bash
# 检查日志目录
ls -la logs/

# 检查服务状态
curl -X GET http://localhost:8080/actuator/health  # gateway
curl -X GET http://localhost:8082/user/test        # user

# 检查系统属性
java -cp . -Dservice.name=test -jar test.jar
```

### 测试修复效果

```bash
# 重启服务后检查
./test-log-config.sh
```

## 结论

service-gateway日志名称正确的原因可能是：
1. 启动时机较早
2. 配置简单，没有干扰
3. 可能通过其他方式获取了服务名称

service-user日志名称错误的原因是：
1. 配置类可能干扰了系统属性设置
2. 启动时机较晚
3. 依赖复杂性增加

**建议采用方案1，统一所有服务的配置方式，避免配置差异导致的问题。** 