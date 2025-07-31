# Gateway服务 - 开发环境配置文件说明

## 文件结构

```
dev/
├── application.yml          # 主启动配置文件
└── service-gateway.yaml     # Nacos远程配置文件
```

## 配置文件职责分工

### 1. application.yml - 主启动配置文件

**职责**: 应用启动必需的基础配置

**包含内容**:
- 服务器配置（端口、上下文路径）
- Spring应用名称
- Nacos连接配置（服务发现和配置中心）
- 管理端点配置
- 基础日志配置
- 开发环境性能优化配置

**特点**:
- 不包含敏感信息
- 不包含业务配置
- 只包含启动必需的基础配置
- 可以提交到代码仓库

### 2. service-gateway.yaml - Nacos远程配置文件

**职责**: 业务配置和敏感信息

**包含内容**:
- 服务器性能配置
- Spring Cloud Gateway路由配置
- 限流配置
- 熔断配置
- 跨域配置
- 性能优化配置
- 监控配置
- 安全配置
- 业务日志配置

**特点**:
- 包含业务逻辑配置
- 支持环境变量覆盖
- 支持配置热刷新
- 不应提交到代码仓库

## 配置加载顺序

1. **application.yml** 首先加载，提供基础配置
2. **service-gateway.yaml** 从Nacos加载，覆盖和补充业务配置
3. 环境变量可以覆盖任何配置项

## 环境变量配置

### 必需环境变量
| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| NACOS_SERVER_ADDR | Nacos服务器地址 | `117.50.197.170:8848` |
| NACOS_USERNAME | Nacos用户名 | `nacos` |
| NACOS_PASSWORD | Nacos密码 | `olN2y)]VzEISLITO#_}L0J^$>Nk)1S~W` |
| NACOS_NAMESPACE | Nacos命名空间 | `ba1493dc-20fa-413b-84e1-d929c9a4aeac` |

### 可选环境变量
| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| GATEWAY_CONNECTION_TIMEOUT | 连接超时时间 | `10000` |
| GATEWAY_READ_TIMEOUT | 读取超时时间 | `15000` |
| GATEWAY_WRITE_TIMEOUT | 写入超时时间 | `15000` |
| GATEWAY_MAX_CONNECTIONS | 最大连接数 | `4096` |
| AUTH_RATE_LIMIT | 认证服务限流 | `10` |
| USER_RATE_LIMIT | 用户服务限流 | `10` |
| DEFAULT_RATE_LIMIT | 默认限流 | `100` |
| CORS_ALLOWED_ORIGINS | 跨域允许源 | `*` |
| RATE_LIMIT_ENABLED | 限流开关 | `true` |
| CIRCUIT_BREAKER_ENABLED | 熔断开关 | `true` |

## 使用方法

### 1. 本地开发
```bash
# 使用默认配置启动
./mvnw spring-boot:run -pl service/service-gateway

# 指定环境变量启动
export NACOS_SERVER_ADDR="your-nacos-server"
export GATEWAY_MAX_CONNECTIONS="2048"
./mvnw spring-boot:run -pl service/service-gateway
```

### 2. 配置热刷新
修改Nacos中的 `infra-gateway.yaml` 配置后，应用会自动刷新配置，无需重启。

### 3. 配置验证
```bash
# 验证配置文件语法
yamllint dev/application.yml
yamllint dev/service-gateway.yaml

# 验证配置加载
curl -X GET "http://localhost:8080/actuator/configprops"
```

## 路由配置说明

### 认证服务路由
- **路径**: `/auth/**`
- **目标**: `service-auth`
- **限流**: 每秒10个请求
- **前缀处理**: 不剥离前缀

### 用户服务路由
- **路径**: `/user/**`
- **目标**: `service-user`
- **限流**: 每秒10个请求
- **前缀处理**: 不剥离前缀

### 默认路由
- **路径**: `/**`
- **目标**: `service-auth`
- **限流**: 每秒100个请求
- **前缀处理**: 不剥离前缀

## 安全注意事项

1. **敏感信息保护**:
   - 生产环境必须使用环境变量覆盖敏感信息
   - 不要在代码中硬编码密码
   - 定期轮换密钥和密码

2. **配置文件管理**:
   - `application.yml` 可以提交到代码仓库
   - `service-gateway.yaml` 不应提交到代码仓库
   - 使用 `.env` 文件管理本地环境变量

3. **访问控制**:
   - 限制Nacos配置中心的访问权限
   - 定期审计配置变更
   - 监控配置访问日志

## 故障排查

### 1. 配置加载失败
- 检查Nacos连接是否正常
- 验证配置文件语法是否正确
- 检查环境变量是否正确设置

### 2. 路由不生效
- 检查路由配置是否正确
- 验证目标服务是否可用
- 检查限流配置是否过于严格

### 3. 性能问题
- 检查连接池配置
- 验证线程池设置
- 监控限流和熔断状态 