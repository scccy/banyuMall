# Nacos配置中心 - service-auth 配置说明

## 概述
service-auth服务的配置已迁移到Nacos配置中心，支持动态配置更新。

## 配置信息
- **配置文件名**: `service-auth.yaml`
- **命名空间**: `ba1493dc-20fa-413b-84e1-d929c9a4aeac`
- **分组**: `DEFAULT_GROUP`

## 配置结构

### 1. 认证服务配置 (auth)
```yaml
auth:
  # JWT配置
  jwt:
    enabled: true                    # 是否启用JWT认证
    secret: ${JWT_SECRET:default}    # JWT密钥（支持环境变量）
    expiration: 7200                 # JWT过期时间（秒）
    refresh-expiration: 604800       # JWT刷新时间（秒）
  
  # 拦截器配置
  interceptor:
    # 需要拦截的路径模式
    include-patterns:
      - "/**"
    # 不需要拦截的路径模式
    exclude-patterns:
      # 认证相关路径
      - "/auth/login"
      - "/auth/logout"
      - "/auth/captcha"
      - "/auth/register"
      # 服务路径
      - "/service/auth/login"
      - "/service/auth/logout"
      - "/service/auth/captcha"
      - "/service/auth/register"
      - "/service/auth/test"
      - "/service/auth/password/encrypt"
      - "/service/auth/password/verify"
      - "/service/auth/user/info"
      - "/service/auth/validate"
      - "/service/auth/logout/force/**"
      # 测试路径
      - "/test/**"
      - "/jwt-test/**"
      - "/password-test/**"
      # 文档路径
      - "/doc.html"
      - "/webjars/**"
      - "/v3/api-docs/**"
      - "/swagger-ui/**"
      - "/swagger-resources/**"
      # 健康检查路径
      - "/actuator/**"
      - "/health"
      - "/info"
```

### 2. 安全配置 (security)
```yaml
security:
  # 不需要认证的路径列表
  permit-all:
    # 认证相关接口
    - /service/auth/login
    - /service/auth/logout
    - /service/auth/register
    - /service/auth/captcha
    
    # 测试相关接口
    - /test/**
    - /jwt-test/**
    - /password-test/**
    - /service/auth/test
    - /auth/test/**
    
    # 密码相关接口
    - /service/auth/password/encrypt
    - /service/auth/password/verify
    
    # 用户信息接口
    - /service/auth/user/info
    
    # JWT验证接口
    - /service/auth/validate
    
    # 强制登出接口
    - /service/auth/logout/force/**
    
    # 文档相关接口
    - /doc.html
    - /webjars/**
    - /v3/api-docs/**
    - /swagger-ui/**
    - /swagger-resources/**
    
    # 监控相关接口
    - /actuator/health
    - /actuator/info
    - /actuator/metrics
    
    # 根路径
    - /
```

## 环境变量支持

### 开发环境
- `JWT_SECRET`: JWT密钥
- `NACOS_SERVER`: Nacos服务器地址
- `NACOS_USERNAME`: Nacos用户名
- `NACOS_PASSWORD`: Nacos密码

### 生产环境
- `JWT_SECRET`: JWT密钥（必需）
- `NACOS_SERVER`: Nacos服务器地址
- `NACOS_USERNAME`: Nacos用户名
- `NACOS_PASSWORD`: Nacos密码
- `NACOS_NAMESPACE`: Nacos命名空间

## 动态配置更新

### 1. 配置刷新
服务支持配置的动态刷新，修改Nacos中的配置后，服务会自动更新：
- JWT配置更新
- 拦截器路径配置更新

### 2. 刷新方式
- **自动刷新**: 修改Nacos配置后，服务会自动检测并更新
- **手动刷新**: 通过Actuator端点手动触发刷新

### 3. 配置验证
修改配置后，可以通过以下方式验证：
- 查看服务日志
- 访问 `/actuator/refresh` 端点
- 测试相关接口功能

## 注意事项

1. **敏感信息**: JWT密钥等敏感信息建议使用环境变量
2. **路径配置**: 拦截器路径配置要谨慎修改，避免影响正常功能
3. **环境隔离**: 不同环境使用不同的命名空间和配置
4. **配置备份**: 重要配置建议定期备份

## 故障排查

### 1. 配置加载失败
- 检查Nacos连接配置
- 验证命名空间和分组设置
- 查看服务启动日志

### 2. 配置更新不生效
- 检查@RefreshScope注解
- 验证配置格式是否正确
- 查看配置刷新日志

### 3. 拦截器配置问题
- 检查路径模式语法
- 验证排除路径是否正确
- 测试相关接口访问 