# 模块开发规则

## 📋 规则概述

**ID**: ARCH-002  
**Name**: 模块开发规则  
**Status**: Active  
**创建时间**: 2025-08-04  

## 🎯 核心原则

### 1. 模块职责分离
- **service-base**: 基础依赖和配置管理
- **service-common**: 通用组件和工具类
- **service-auth**: 认证和授权服务
- **service-user**: 用户管理服务
- **service-gateway**: API网关服务
- **core-publisher**: 核心业务发布者服务
- **aliyun-oss**: 第三方OSS服务

### 2. 配置管理原则
- **配置类**: 必须在 `service-base` 模块，自动加载生效
- **工具类**: 必须在 `service-common` 模块，供其他模块共享
- **依赖关系**: 配置类优先加载，工具类按需依赖

### 3. 文档组织原则
- **模块文档**: 在 `zinfra/moudleDocs/` 下按模块创建文件夹
- **设计文档**: 每个模块包含设计文档和接口测试文档
- **Feign使用**: 文档中需注明是否使用Feign

## 🏗️ 模块设计规范

### 1. 模块结构规范
```
模块名/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/origin/模块名/
│   │   │       ├── config/          # 配置类
│   │   │       ├── controller/      # 控制器
│   │   │       ├── service/         # 服务层
│   │   │       │   └── impl/        # 服务实现
│   │   │       ├── mapper/          # 数据访问层
│   │   │       ├── entity/          # 实体类
│   │   │       ├── dto/             # 数据传输对象
│   │   │       ├── exception/       # 异常处理
│   │   │       ├── feign/           # Feign客户端
│   │   │       └── util/            # 工具类
│   │   └── resources/
│   │       ├── dev/                 # 开发环境配置
│   │       ├── test/                # 测试环境配置
│   │       ├── prod/                # 生产环境配置
│   │       └── mapper/              # MyBatis映射文件
│   └── test/
│       └── java/
│           └── com/origin/模块名/
└── pom.xml
```

### 2. 包命名规范
- **基础包名**: `com.origin.模块名`
- **配置包**: `com.origin.模块名.config`
- **控制器包**: `com.origin.模块名.controller`
- **服务包**: `com.origin.模块名.service`
- **实体包**: `com.origin.模块名.entity`
- **DTO包**: `com.origin.模块名.dto`
- **异常包**: `com.origin.模块名.exception`
- **Feign包**: `com.origin.模块名.feign`
- **工具包**: `com.origin.模块名.util`

### 3. 类命名规范
- **配置类**: `XxxConfig`
- **控制器类**: `XxxController`
- **服务接口**: `XxxService`
- **服务实现**: `XxxServiceImpl`
- **实体类**: `Xxx`
- **DTO类**: `XxxRequest`, `XxxResponse`
- **异常类**: `XxxException`
- **Feign客户端**: `XxxFeignClient`
- **工具类**: `XxxUtil`

## 📦 依赖管理规范

### 1. 依赖层次结构
```
service-gateway
    ↓ (依赖)
service-common
    ↓ (依赖)
service-base

service-auth
    ↓ (依赖)
service-common

service-user
    ↓ (依赖)
service-common

core-publisher
    ↓ (依赖)
service-common, service-base

aliyun-oss
    ↓ (依赖)
service-common
```

### 2. 依赖原则
- **单向依赖**: 避免循环依赖
- **分层依赖**: 上层依赖下层，同层不依赖
- **最小依赖**: 只依赖必要的模块
- **版本统一**: 在父项目中统一管理版本

### 3. 依赖排除原则
- **排除冲突**: 排除冲突的依赖
- **排除冗余**: 排除不必要的传递依赖
- **排除重复**: 避免重复引入相同依赖

## 🔧 配置类管理规范

### 1. 配置类位置
- **统一位置**: 所有配置类必须在 `service-base` 模块
- **自动加载**: 使用 `@EnableAutoConfiguration` 自动加载
- **条件配置**: 使用 `@ConditionalOnProperty` 条件配置

### 2. 配置类示例
```java
@Configuration
@EnableAutoConfiguration
@ConditionalOnProperty(name = "spring.datasource.enabled", havingValue = "true", matchIfMissing = true)
public class DataSourceConfig {
    
    @Bean
    @Primary
    public DataSource dataSource() {
        // 数据源配置
    }
}
```

### 3. 配置类命名
- **数据源配置**: `DataSourceConfig`
- **MyBatis配置**: `MyBatisPlusConfig`
- **Redis配置**: `RedisConfig`
- **Swagger配置**: `Knife4jConfig`
- **安全配置**: `SecurityConfig`

## 🛠️ 工具类管理规范

### 1. 工具类位置
- **统一位置**: 所有工具类必须在 `service-common` 模块
- **共享使用**: 供其他模块共享使用
- **按需依赖**: 其他模块按需依赖

### 2. 工具类分类
- **通用工具**: `CommonUtil`
- **日期工具**: `DateUtil`
- **字符串工具**: `StringUtil`
- **加密工具**: `EncryptUtil`
- **验证工具**: `ValidateUtil`

### 3. 工具类设计原则
- **静态方法**: 工具类方法应该是静态的
- **无状态**: 工具类应该是无状态的
- **线程安全**: 工具类应该是线程安全的
- **异常处理**: 工具类应该有完善的异常处理

## 📝 文档组织规范

### 1. 文档结构
```
zinfra/moudleDocs/
├── service-auth/
│   ├── 模块设计.md
│   ├── 模块迭代设计.md
│   └── 模块迭代说明.md
├── service-user/
│   ├── 模块设计.md
│   ├── 模块迭代设计.md
│   └── 模块迭代说明.md
├── service-gateway/
│   ├── 模块设计.md
│   ├── 模块迭代设计.md
│   └── 模块迭代说明.md
├── core-publisher/
│   ├── 模块设计.md
│   ├── 模块迭代设计.md
│   └── 模块迭代说明.md
└── third-party-oss/
    ├── 模块设计.md
    ├── 模块迭代设计.md
    └── 模块迭代说明.md
```

### 2. 文档内容要求
- **模块设计.md**: 模块的整体设计思路
- **模块迭代设计.md**: 模块的迭代设计计划
- **模块迭代说明.md**: 模块迭代的详细说明
- **Feign使用**: 文档中需注明是否使用Feign

### 3. 文档更新原则
- **及时更新**: 模块变更时及时更新文档
- **版本管理**: 文档变更进行版本管理
- **评审机制**: 重要文档变更需要评审

## 🔗 服务间通信规范

### 1. Feign使用规范
- **使用场景**: 服务间同步调用
- **命名规范**: `XxxFeignClient`
- **降级处理**: 必须配置降级处理
- **超时配置**: 合理配置超时时间

### 2. Feign配置示例
```java
@FeignClient(
    name = "service-user",
    fallback = UserFeignClientFallback.class,
    configuration = FeignConfig.class
)
public interface UserFeignClient {
    
    @GetMapping("/user/{id}")
    ResultData<UserInfo> getUserById(@PathVariable("id") String id);
}
```

### 3. 降级处理示例
```java
@Component
public class UserFeignClientFallback implements UserFeignClient {
    
    @Override
    public ResultData<UserInfo> getUserById(String id) {
        return ResultData.error("服务调用失败");
    }
}
```

## 🚫 禁止事项

### 严格禁止
1. **循环依赖** - 模块间不允许循环依赖
2. **配置类分散** - 配置类不能分散在各个模块
3. **工具类重复** - 避免工具类重复定义
4. **文档缺失** - 每个模块必须有设计文档

### 不推荐
1. **过度依赖** - 避免不必要的模块依赖
2. **紧耦合设计** - 避免模块间紧耦合
3. **配置硬编码** - 避免配置信息硬编码

## ✅ 推荐事项

### 最佳实践
1. **模块自治** - 每个模块独立开发和部署
2. **接口优先** - 先定义接口再实现
3. **配置集中** - 配置类集中管理
4. **工具共享** - 工具类共享使用
5. **文档同步** - 代码和文档同步更新

### 设计模式
1. **依赖注入模式** - 使用Spring的依赖注入
2. **工厂模式** - 使用工厂模式创建对象
3. **策略模式** - 使用策略模式处理不同场景
4. **模板方法模式** - 使用模板方法模式定义流程

---

**版本**: v1.0  
**创建时间**: 2025-08-04  
**最后更新**: 2025-08-04  
**维护者**: scccy 