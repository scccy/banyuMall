# 新模块网关接口同步规则

**ID**: DEV-007  
**Name**: 新模块网关接口同步规则  
**Status**: Active  
**创建时间**: 2025-07-30  

## 触发情景 (Context/Trigger)
当创建新的微服务模块或修改现有模块的API接口时。

**特别注意**: 创建新微服务模块时，**必须立即**在网关中添加相应的路由配置，确保新服务能够通过网关正常访问。

## 指令 (Directive)

### 1. 网关路由配置同步
- **必须 (MUST)** 在新模块创建时同步更新网关路由配置
- **必须 (MUST)** 在API接口变更时同步更新网关路由
- **禁止 (MUST NOT)** 新模块上线后网关路由未配置
- **必须 (MUST)** 新微服务创建完成后立即在网关中添加路由配置
- **必须 (MUST)** 确保新微服务能够通过网关路径 `/api/{service-name}/**` 正常访问

### 2. 路由配置要求
每个新模块必须在网关中配置以下路由：

#### 2.1 基础路由配置
```yaml
spring:
  cloud:
    gateway:
      routes:
        # 新模块路由配置
        - id: service-{module-name}
          uri: lb://service-{module-name}
          predicates:
            - Path=/api/{module-name}/**
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
            - name: Retry
              args:
                retries: 3
                statuses: BAD_GATEWAY
```

#### 2.2 认证路由配置
```yaml
        # 认证相关路由（仅auth模块）
        - id: service-auth
          uri: lb://service-auth
          predicates:
            - Path=/api/auth/**
          filters:
            - StripPrefix=1
            - name: AuthFilter
```

### 3. 网关过滤器同步
- **必须 (MUST)** 更新网关的全局过滤器以支持新模块
- **必须 (MUST)** 配置新模块的认证和权限验证
- **必须 (MUST)** 配置新模块的限流和熔断策略

### 4. 网关配置更新步骤
创建新模块时，必须按以下步骤更新网关：

**重要提醒**: 新微服务创建完成后，**必须立即**执行以下步骤，确保服务能够通过网关正常访问。

#### 步骤1: 更新路由配置
- 在`infra/infra-gateway/src/main/resources/application.yml`中添加新模块路由
- 配置正确的服务名称和路径前缀
- 设置适当的限流和重试策略

#### 步骤2: 更新过滤器配置
- 在`infra/infra-gateway/src/main/java/com/origin/gateway/filter/GlobalFilter.java`中添加新模块的处理逻辑
- 配置新模块的认证验证规则
- 设置新模块的权限检查逻辑

#### 步骤3: 更新网关配置类
- 在`infra/infra-gateway/src/main/java/com/origin/gateway/config/GatewayConfig.java`中添加新模块的配置
- 配置新模块的跨域设置
- 设置新模块的负载均衡策略

#### 步骤4: 更新Nacos配置
- 在`infra/infra-gateway/nacos-config-template.yml`中添加新模块的配置模板
- 确保新模块的配置能够通过Nacos动态更新

### 5. 接口文档同步
- **必须 (MUST)** 更新网关的API文档配置
- **必须 (MUST)** 确保新模块的接口在网关文档中可见
- **必须 (MUST)** 配置新模块的接口分组和标签

### 6. 监控和日志同步
- **必须 (MUST)** 配置新模块的监控指标
- **必须 (MUST)** 设置新模块的日志收集规则
- **必须 (MUST)** 配置新模块的告警规则

### 7. 测试验证
- **必须 (MUST)** 测试新模块通过网关的访问
- **必须 (MUST)** 验证认证和权限功能
- **必须 (MUST)** 测试限流和熔断功能
- **必须 (MUST)** 验证监控和日志功能

## 配置模板

### 新模块网关路由模板
```yaml
# 新模块路由配置模板
- id: service-{module-name}
  uri: lb://service-{module-name}
  predicates:
    - Path=/api/{module-name}/**
  filters:
    - StripPrefix=1
    - name: RequestRateLimiter
      args:
        redis-rate-limiter.replenishRate: 10
        redis-rate-limiter.burstCapacity: 20
    - name: Retry
      args:
        retries: 3
        statuses: BAD_GATEWAY
    - name: CircuitBreaker
      args:
        name: {module-name}-circuit-breaker
        fallbackUri: forward:/fallback/{module-name}
```

### 网关过滤器更新模板
```java
// 在GlobalFilter中添加新模块处理逻辑
if (path.startsWith("/api/{module-name}/")) {
    // 新模块特定的处理逻辑
    return handle{ModuleName}Request(exchange);
}
```

## 理由 (Justification)
此规则源于微服务架构中网关作为统一入口的重要性。新模块创建后，如果网关配置未同步更新，会导致：
1. **服务不可访问**: 新模块无法通过网关访问
2. **认证失效**: 新模块的认证和权限验证无法正常工作
3. **监控缺失**: 新模块的访问无法被监控和统计
4. **配置不一致**: 网关配置与实际服务不匹配

通过强制同步更新网关配置，确保：
1. **统一入口**: 所有服务通过网关统一访问
2. **安全控制**: 统一的认证和权限管理
3. **流量控制**: 统一的限流和熔断策略
4. **监控统一**: 统一的监控和日志收集

## 检查清单
创建新模块时，必须检查：
- [ ] **网关路由配置已添加** (新微服务创建后立即执行)
- [ ] **新微服务能够通过网关路径正常访问** (优先级最高)
- [ ] 网关过滤器已更新
- [ ] 网关配置类已更新
- [ ] Nacos配置模板已更新
- [ ] API文档配置已更新
- [ ] 监控配置已设置
- [ ] 日志配置已设置
- [ ] 通过网关访问测试通过
- [ ] 认证功能测试通过
- [ ] 权限功能测试通过
- [ ] 限流功能测试通过
- [ ] 熔断功能测试通过
- [ ] 监控功能测试通过
- [ ] 日志功能测试通过

**新微服务创建后的立即检查项**:
- [ ] 网关路由配置文件中已添加新服务路由
- [ ] 新服务能够通过 `http://gateway-host:port/api/{service-name}/**` 访问
- [ ] 网关启动后新服务路由生效
- [ ] 新服务的健康检查接口能够正常响应

## 新微服务创建时的网关路由要求

### 立即执行要求
当创建新的微服务模块时，**必须立即**执行以下操作：

1. **立即添加网关路由配置**
   ```yaml
   # 在 infra/infra-gateway/src/main/resources/application.yml 中添加
   spring:
     cloud:
       gateway:
         routes:
           - id: service-{new-service-name}
             uri: lb://service-{new-service-name}
             predicates:
               - Path=/api/{new-service-name}/**
             filters:
               - StripPrefix=1
   ```

2. **立即验证路由配置**
   - 重启网关服务
   - 测试新服务是否能够通过网关访问
   - 验证路径 `/api/{new-service-name}/**` 是否正常工作

3. **立即更新相关配置**
   - 更新网关过滤器配置
   - 更新Nacos配置模板
   - 更新API文档配置

### 常见错误和解决方案
- **错误**: 新服务创建后无法通过网关访问
  - **解决**: 检查网关路由配置是否正确添加
- **错误**: 网关路由配置后仍然无法访问
  - **解决**: 检查服务名称是否与路由配置中的uri一致
- **错误**: 路径前缀不匹配
  - **解决**: 确保predicates中的Path与服务的实际路径一致

## 相关文件
- `infra/infra-gateway/src/main/resources/application.yml`
- `infra/infra-gateway/src/main/java/com/origin/gateway/filter/GlobalFilter.java`
- `infra/infra-gateway/src/main/java/com/origin/gateway/config/GatewayConfig.java`
- `infra/infra-gateway/nacos-config-template.yml` 