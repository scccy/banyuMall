# LR-008: Spring配置类优先base模块规则

**ID**: LR-008  
**Name**: Spring配置类优先base模块规则  
**Status**: Active  
**创建时间**: 2025-01-27  

## 触发情景 (Context/Trigger)
当需要创建Spring配置类（如MyBatisPlusConfig、RedisConfig、WebConfig等）时。

## 指令 (Directive)

### 1. 配置类放置原则
- **必须 (MUST)** 将通用配置类放在 `service-base` 模块中
- **必须 (MUST)** 将业务特定配置类放在对应的微服务模块中
- **禁止 (MUST NOT)** 在业务模块中重复创建通用配置类

### 2. Base模块配置类范围
- **MyBatisPlus配置**: MyBatisPlusConfig、分页插件、自动填充处理器
- **Redis配置**: RedisConfig、序列化配置、连接池配置
- **Web配置**: WebMvcConfig、跨域配置、消息转换器
- **Feign配置**: OpenFeignConfig、负载均衡配置
- **Knife4j配置**: Knife4jConfig、API文档配置
- **事务配置**: TransactionConfig、事务管理器配置

### 3. 业务模块配置类范围
- **业务特定配置**: 业务相关的Bean配置、拦截器配置
- **模块特定配置**: 模块独有的配置项
- **外部服务配置**: 特定于该模块的外部服务配置

### 4. 依赖管理
- **必须 (MUST)** 确保所有微服务模块都依赖 `service-base` 模块
- **必须 (MUST)** 在微服务启动类中添加 `@ComponentScan` 扫描base模块
- **禁止 (MUST NOT)** 在业务模块中重复定义base模块已有的配置

## 理由 (Justification)
此规则源于项目架构设计原则，确保：
1. **配置复用**: 避免重复配置，提高开发效率
2. **维护性**: 统一管理配置，便于维护和升级
3. **一致性**: 确保所有微服务使用相同的配置标准
4. **模块化**: 清晰的模块职责划分

## 示例 (Examples)

### ✅ 正确做法
```java
// service-base模块中的MyBatisPlusConfig
@Configuration
@ConditionalOnClass(MybatisConfiguration.class)
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 分页插件配置
    }
    
    @Component
    public static class MetaObjectHandlerImpl implements MetaObjectHandler {
        // 自动填充处理器
    }
}

// 微服务启动类
@SpringBootApplication(scanBasePackages = {"com.origin.base", "com.origin.publisher"})
@EnableDiscoveryClient
public class CorePublisherApplication {
    // 启动类配置
}
```

### ❌ 错误做法
```java
// 在业务模块中重复创建MyBatisPlusConfig
@Configuration
public class MyBatisPlusConfig {
    // 重复的配置
}
```

## 相关规则
- **LR-002**: MyBatis-Plus依赖版本一致性规则
- **LR-005**: Feign客户端配置规范
- **DEV-012**: 统一响应数据类和实体类规范 