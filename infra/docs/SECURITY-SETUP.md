# 安全配置说明

## 重要安全更新

由于GitHub检测到代码中包含敏感信息（阿里云AccessKey），我们已经进行了以下安全处理：

### 1. 已清理的敏感信息
- ✅ 阿里云AccessKey ID: [已移除]
- ✅ 阿里云AccessKey Secret: [已移除]
- ✅ Nacos密码: [已移除]
- ✅ 数据库密码: [已移除]

### 2. 安全措施
1. **立即禁用泄露的AccessKey** - 请在阿里云控制台禁用或删除这些凭据
2. **创建新的AccessKey** - 如果需要继续使用OSS服务
3. **使用环境变量** - 所有敏感信息现在通过环境变量配置
4. **使用Nacos配置中心** - 生产环境配置存储在Nacos中

### 3. 配置方式

#### 开发环境
在 `service/service-auth/src/main/resources/application.yml` 中使用环境变量：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_SERVER_ADDR:localhost:8848}
        username: ${NACOS_USERNAME:nacos}
        password: ${NACOS_PASSWORD:nacos}
```

#### 生产环境
1. 在Nacos配置中心创建配置文件
2. 使用环境变量或启动参数传入敏感信息

### 4. 环境变量示例

```bash
# Nacos配置
export NACOS_SERVER_ADDR=your_nacos_server:8848
export NACOS_USERNAME=nacos
export NACOS_PASSWORD=your_nacos_password
export NACOS_NAMESPACE=your_namespace

# 数据库配置
export DB_HOST=your_db_host
export DB_PORT=3306
export DB_NAME=banyu_mall
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password

# Redis配置
export REDIS_HOST=your_redis_host
export REDIS_PORT=6379
export REDIS_PASSWORD=your_redis_password

# JWT配置
export JWT_SECRET=your-jwt-secret-key-here

# 阿里云OSS配置
export OSS_ENABLED=true
export OSS_ENDPOINT=oss-cn-beijing.aliyuncs.com
export OSS_ACCESS_KEY_ID=your_new_access_key_id
export OSS_ACCESS_KEY_SECRET=your_new_access_key_secret
export OSS_BUCKET_NAME=your_bucket_name
export OSS_REGION=cn-beijing
```

### 5. 安全最佳实践

1. **永远不要在代码中硬编码敏感信息**
2. **使用环境变量或配置中心管理敏感信息**
3. **定期轮换AccessKey和密码**
4. **使用最小权限原则**
5. **启用GitHub安全扫描**

### 6. 紧急联系人

如果发现任何安全问题，请立即联系项目维护者。 