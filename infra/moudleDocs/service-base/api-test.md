> **文档位置**: infra/moudleDocs/{模块名称}/api-test.md

# Service-Base API测试文档

## 模块说明

**Service-Base** 是一个基础配置模块，**不提供任何API接口**。它的作用是：

- 提供Spring Boot的公共配置类
- 为其他微服务模块提供基础依赖
- 包含MyBatis-Plus、Redis、Knife4j等通用配置
- 提供全局异常处理、拦截器等基础设施

## 配置测试

虽然service-base没有API接口，但可以通过以下方式验证配置是否正确：

### 1. 依赖注入测试

在其他服务中注入service-base的配置类，验证配置是否生效：

```java
@Autowired
private MyBatisPlusConfig myBatisPlusConfig;

@Autowired
private RedisConfig redisConfig;

@Autowired
private Knife4jConfig knife4jConfig;
```

### 2. 配置类验证

检查配置类是否正确加载：

```java
// 验证MyBatis-Plus配置
@Configuration
public class MyBatisPlusConfig {
    // 配置内容
}

// 验证Redis配置
@Configuration
public class RedisConfig {
    // 配置内容
}

// 验证Knife4j配置
@Configuration
public class Knife4jConfig {
    // 配置内容
}
```

### 3. 自动配置验证

检查`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`文件：

```properties
com.origin.base.config.MyBatisPlusConfig
com.origin.base.config.RedisConfig
com.origin.base.config.Knife4jConfig
com.origin.base.config.GlobalExceptionHandler
com.origin.base.config.WebMvcConfig
com.origin.base.config.TransactionConfig
com.origin.base.config.Log4j2Config
com.origin.base.config.OssConfig
com.origin.base.config.OpenFeignConfig
```

## 使用说明

### 1. 在其他模块中引入

```xml
<dependency>
    <groupId>com.origin</groupId>
    <artifactId>service-base</artifactId>
    <version>${project.version}</version>
</dependency>
```

### 2. 配置生效验证

启动其他微服务模块时，检查日志中是否包含service-base的配置信息：

```bash
# 启动service-auth时，应该看到以下日志：
2024-12-19 10:00:00 INFO  MyBatisPlusConfig - MyBatis-Plus配置已加载
2024-12-19 10:00:00 INFO  RedisConfig - Redis配置已加载
2024-12-19 10:00:00 INFO  Knife4jConfig - Knife4j配置已加载
```

### 3. 功能验证

在其他模块中验证service-base提供的功能：

```java
// 验证MyBatis-Plus功能
@Autowired
private UserMapper userMapper;

public void testMyBatisPlus() {
    List<User> users = userMapper.selectList(null);
    // 验证查询是否正常
}

// 验证Redis功能
@Autowired
private RedisTemplate<String, Object> redisTemplate;

public void testRedis() {
    redisTemplate.opsForValue().set("test", "value");
    Object value = redisTemplate.opsForValue().get("test");
    // 验证Redis操作是否正常
}

// 验证全局异常处理
public void testGlobalException() {
    throw new BusinessException(ErrorCode.PARAM_ERROR, "测试异常");
    // 验证异常是否被正确捕获和处理
}
```

## 配置检查清单

### ✅ MyBatis-Plus配置
- [ ] 分页插件配置
- [ ] 乐观锁插件配置
- [ ] 逻辑删除配置
- [ ] 字段自动填充配置

### ✅ Redis配置
- [ ] 连接池配置
- [ ] 序列化器配置（FastJSON2）
- [ ] 键过期策略配置
- [ ] 异常处理配置

### ✅ Knife4j配置
- [ ] API文档配置
- [ ] 分组配置
- [ ] 安全配置
- [ ] 响应配置

### ✅ 全局异常处理
- [ ] 业务异常处理
- [ ] 参数验证异常处理
- [ ] 系统异常处理
- [ ] 统一响应格式

### ✅ Web配置
- [ ] CORS配置
- [ ] 拦截器配置
- [ ] 参数解析器配置
- [ ] 消息转换器配置

## 故障排查

### 1. 配置未生效
**问题**: service-base的配置在其他模块中未生效
**排查步骤**:
1. 检查依赖是否正确引入
2. 检查包扫描路径是否正确
3. 检查自动配置文件是否存在
4. 检查启动日志是否有错误

### 2. 配置冲突
**问题**: service-base的配置与其他模块配置冲突
**排查步骤**:
1. 检查配置优先级
2. 检查条件注解是否正确
3. 检查配置类是否重复
4. 检查依赖版本是否一致

### 3. 功能异常
**问题**: service-base提供的功能在其他模块中异常
**排查步骤**:
1. 检查配置参数是否正确
2. 检查依赖服务是否可用
3. 检查日志中的错误信息
4. 检查网络连接是否正常

## 最佳实践

### 1. 配置管理
- 使用条件注解控制配置生效
- 提供合理的默认配置
- 支持配置参数外部化
- 提供配置验证机制

### 2. 依赖管理
- 明确依赖版本
- 避免循环依赖
- 提供依赖排除配置
- 定期更新依赖版本

### 3. 测试验证
- 为每个配置类编写单元测试
- 验证配置在不同环境下的表现
- 测试配置的容错能力
- 验证配置的性能影响

## 注意事项

1. **无API接口**: service-base不提供任何API接口，只提供配置和工具类
2. **依赖基础**: 其他模块依赖service-base，确保其稳定性
3. **配置隔离**: 避免配置冲突，使用条件注解控制
4. **版本兼容**: 确保配置与Spring Boot版本兼容
5. **性能影响**: 合理配置，避免对性能造成负面影响 