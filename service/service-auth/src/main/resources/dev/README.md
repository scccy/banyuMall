# Auth服务 - 开发环境配置文件说明

## 文件结构

```
dev/
├── application.yml          # 主启动配置文件
└── service-auth.yaml        # Nacos远程配置文件
```

## 配置文件职责分工

### 1. application.yml - 主启动配置文件

**职责**: 应用启动必需的基础配置

**包含内容**:
- 服务器配置（端口、上下文路径）
- Spring应用名称
- Nacos连接配置（服务发现和配置中心）
- Spring Security基础配置
- 管理端点配置
- 基础日志配置
- 开发环境性能优化配置

**特点**:
- 不包含敏感信息
- 不包含业务配置
- 只包含启动必需的基础配置
- 可以提交到代码仓库

### 2. service-auth.yaml - Nacos远程配置文件

**职责**: 业务配置和敏感信息

**包含内容**:
- 数据源配置（数据库连接信息）
- Redis配置
- MyBatis-Plus配置
- JWT配置
- 安全配置
- Swagger配置
- 业务相关配置
- 监控配置
- 限流配置
- 审计日志配置

**特点**:
- 包含敏感信息（数据库密码、Redis密码、JWT密钥等）
- 包含业务逻辑配置
- 支持环境变量覆盖
- 支持配置热刷新
- 不应提交到代码仓库

## 配置加载顺序

1. **application.yml** 首先加载，提供基础配置
2. **service-auth.yaml** 从Nacos加载，覆盖和补充业务配置
3. 环境变量可以覆盖任何配置项

## 环境变量配置

### 必需环境变量
| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| MYSQL_URL | MySQL连接URL | `jdbc:mysql://117.50.197.170:3306/banyu` |
| MYSQL_USERNAME | MySQL用户名 | `root` |
| MYSQL_PASSWORD | MySQL密码 | `qUhquc-dagpup-5rubvu` |
| REDIS_HOST | Redis主机 | `117.50.197.170` |
| REDIS_PORT | Redis端口 | `16379` |
| REDIS_PASSWORD | Redis密码 | `qUhquc-dagpup-5rubvu` |
| JWT_SECRET | JWT密钥 | `nG9dT@e4^M7#pKc!Wz0qF8vRtLx*A6s1YhJ2BrCm` |

### 可选环境变量
| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| NACOS_SERVER_ADDR | Nacos服务器地址 | `117.50.197.170:8848` |
| NACOS_USERNAME | Nacos用户名 | `nacos` |
| NACOS_PASSWORD | Nacos密码 | `olN2y)]VzEISLITO#_}L0J^$>Nk)1S~W` |
| NACOS_NAMESPACE | Nacos命名空间 | `ba1493dc-20fa-413b-84e1-d929c9a4aeac` |

## 使用方法

### 1. 本地开发
```bash
# 使用默认配置启动
./mvnw spring-boot:run -pl service/service-auth

# 指定环境变量启动
export MYSQL_PASSWORD="your-password"
export JWT_SECRET="your-secret"
./mvnw spring-boot:run -pl service/service-auth
```

### 2. 配置热刷新
修改Nacos中的 `service-auth.yaml` 配置后，应用会自动刷新配置，无需重启。

### 3. 配置验证
```bash
# 验证配置文件语法
yamllint dev/application.yml
yamllint dev/service-auth.yaml

# 验证配置加载
curl -X GET "http://localhost:8081/actuator/configprops"
```

## 安全注意事项

1. **敏感信息保护**:
   - 生产环境必须使用环境变量覆盖敏感信息
   - 不要在代码中硬编码密码
   - 定期轮换密钥和密码

2. **配置文件管理**:
   - `application.yml` 可以提交到代码仓库
   - `service-auth.yaml` 不应提交到代码仓库
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

### 2. 配置不生效
- 检查配置刷新是否启用
- 验证配置优先级是否正确
- 检查是否有配置冲突

### 3. 性能问题
- 检查数据库连接池配置
- 验证Redis连接配置
- 监控配置加载时间 