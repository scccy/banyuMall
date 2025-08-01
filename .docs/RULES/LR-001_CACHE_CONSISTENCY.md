# 规则名称: 缓存配置原则 - 非必要不添加，独立配置

## 触发情景 (Context/Trigger)
当需要为微服务添加缓存功能时，或现有缓存配置需要修改时。

## 指令 (Directive)

### 1. 缓存添加原则
- **必须 (MUST)** 遵循"非必要不添加"原则
- **必须 (MUST)** 在添加缓存前进行性能分析，确认确实需要缓存
- **禁止 (MUST NOT)** 为了缓存而缓存，必须有明确的性能瓶颈证据
- **必须 (MUST)** 在添加缓存前进行充分的测试验证
- **必须 (MUST)** 启用MyBatis-Plus缓存，提高数据库查询性能

### 1.1 缓存分类和原则
- **MyBatis-Plus缓存**：**必须启用** - `mybatis-plus.configuration.cache-enabled: true`
- **JWT令牌缓存**：**必须启用** - 认证服务必需，避免用户频繁登录
- **业务数据缓存**：**非必要不添加** - 需要性能分析后决定
- **计算结果缓存**：**非必要不添加** - 需要性能分析后决定

### 2. 缓存配置独立性
- **必须 (MUST)** 每个微服务独立配置自己的缓存策略
- **必须 (MUST)** 缓存配置不能跨服务共享，避免服务间耦合
- **必须 (MUST)** 每个服务的缓存配置放在自己的配置文件中
- **禁止 (MUST NOT)** 在公共配置模板中添加缓存配置

### 3. 缓存配置规范
- **必须 (MUST)** 使用 `spring.cache.type: redis` 配置Redis缓存
- **必须 (MUST)** 设置合理的TTL（time-to-live）值
- **必须 (MUST)** 配置 `cache-null-values: false` 避免缓存空值
- **必须 (MUST)** 使用有意义的缓存键前缀，格式：`{service}:{module}:{key}`
- **必须 (MUST)** 启用MyBatis-Plus缓存：`mybatis-plus.configuration.cache-enabled: true`

### 3.1 缓存配置分层原则
- **MyBatis-Plus缓存**：在Service层自动生效，无需Controller层干预
- **JWT令牌缓存**：在Service层使用@Cacheable注解，Controller层不直接操作
- **业务缓存**：在Service层配置和使用，Controller层通过Service接口调用
- **禁止**：Controller层直接操作任何缓存

### 4. 缓存注解使用规范
- **必须 (MUST)** 只在确实需要缓存的业务方法上使用 `@Cacheable`
- **必须 (MUST)** 在数据更新时使用 `@CacheEvict` 清除相关缓存
- **必须 (MUST)** 使用 `@CacheConfig` 统一配置缓存名称
- **禁止 (MUST NOT)** 在查询方法上随意添加缓存注解

### 5. 缓存监控和维护
- **必须 (MUST)** 添加缓存命中率监控
- **必须 (MUST)** 定期清理过期缓存
- **必须 (MUST)** 监控缓存内存使用情况
- **必须 (MUST)** 提供缓存手动清理接口

## 理由 (Justification)
此规则源于任务 `task_20250801_2340_cache_configuration_fix.md`。在该任务中，项目中的缓存配置导致了Spring Boot启动错误，并且缓存配置分散在多个地方，缺乏统一管理。通过"非必要不添加"原则和独立配置要求，可以避免不必要的缓存复杂性，确保每个服务的缓存策略都是经过深思熟虑的。

**重要说明**：MyBatis-Plus缓存是必要的，因为它可以显著提高数据库查询性能，减少重复查询的开销，特别是在高并发场景下。认证服务的JWT缓存也是必要的，可以避免用户频繁登录。

## 技术细节

### 缓存配置示例
```yaml
# 每个服务独立配置
spring:
  cache:
    type: redis
    redis:
      time-to-live: 3600000  # 1小时
      cache-null-values: false
      key-prefix: "service-name:"
```

### 缓存注解示例
```java
@Service
@CacheConfig(cacheNames = "user:profile")
public class UserProfileServiceImpl {
    
    @Cacheable(key = "#userId")
    public UserProfile getProfileByUserId(String userId) {
        // 业务逻辑
    }
    
    @CacheEvict(key = "#userId")
    public boolean updateProfile(String userId, UserProfile profile) {
        // 更新逻辑
    }
}
```

## 影响范围
- 所有微服务模块
- 新创建的微服务
- 现有缓存的维护和更新

## 验证方法
1. 检查是否遵循"非必要不添加"原则
2. 验证每个服务的缓存配置是否独立
3. 确认缓存配置不在公共模板中
4. 测试缓存功能的正确性
5. 监控缓存性能和命中率

## 例外情况
- 只有在明确的性能瓶颈证据下才允许添加缓存
- 必须经过团队评审和性能测试验证
- 缓存配置必须独立，不能影响其他服务 