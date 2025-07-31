# Service-User 开发环境配置说明

## 配置文件说明

### 1. application.yml
**本地配置文件**，包含：
- 服务器端口配置（8082）
- Nacos服务注册与发现配置
- Nacos配置中心连接信息
- 开发环境性能优化配置
- 日志配置

### 2. service-user.yaml
**Nacos远程配置文件**，包含：
- 数据源配置（MySQL）
- Redis缓存配置
- MyBatis-Plus配置
- 用户服务业务配置
- 缓存策略配置
- 安全配置
- Swagger文档配置
- OpenFeign配置
- 监控和限流配置

## 配置加载顺序

1. **本地配置** (`application.yml`) - 基础配置和Nacos连接
2. **Nacos配置** (`service-user.yaml`) - 业务配置和敏感信息
3. **环境变量** - 覆盖敏感配置

## 开发环境特性

### 性能优化
- 懒加载初始化
- 禁用JMX
- 详细的SQL日志
- 缓存配置

### 调试功能
- 详细的日志输出
- Swagger文档启用
- 健康检查端点
- 指标监控

### 安全配置
- 宽松的限流策略
- 开发环境允许的操作
- 详细的审计日志

## 启动方式

### 方式一：使用本地配置
```bash
java -jar service-user.jar --spring.profiles.active=dev
```

### 方式二：指定配置文件
```bash
java -jar service-user.jar --spring.config.location=classpath:dev/application.yml
```

## 配置验证

启动后检查以下端点：
- 健康检查：`http://localhost:8082/actuator/health`
- Swagger文档：`http://localhost:8082/swagger-ui.html`
- 服务测试：`http://localhost:8082/user/test`

## 注意事项

1. **数据库连接**：确保MySQL服务可访问
2. **Redis连接**：确保Redis服务可访问
3. **Nacos连接**：确保Nacos配置中心可访问
4. **端口占用**：确保8082端口未被占用

## 常见问题

### 1. 配置加载失败
- 检查Nacos连接信息
- 检查命名空间和配置组
- 检查配置文件名称

### 2. 数据库连接失败
- 检查数据库服务状态
- 检查连接字符串和凭据
- 检查网络连接

### 3. Redis连接失败
- 检查Redis服务状态
- 检查连接参数
- 检查数据库编号

### 4. 服务注册失败
- 检查Nacos服务状态
- 检查服务发现配置
- 检查网络连接 