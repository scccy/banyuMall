# Spring Security配置说明

## 默认用户配置

### 问题描述
Spring Security在启动时会自动生成一个随机密码，如：
```
Using generated security password: 5bf9002b-ed7f-4334-a15a-8b1553184ec2
```

这会导致每次启动时密码都不同，不利于开发和测试。

### 解决方案

#### 方法1：配置文件方式（推荐）

在 `application.yml` 中配置：

```yaml
spring:
  security:
    user:
      name: admin
      password: admin123
      roles: ADMIN
```

#### 方法2：环境变量方式

```bash
export SECURITY_USER_NAME=admin
export SECURITY_USER_PASSWORD=admin123
export SECURITY_USER_ROLES=ADMIN
```

#### 方法3：启动参数方式

```bash
java -jar app.jar --spring.security.user.name=admin --spring.security.user.password=admin123
```

### 当前配置

#### 默认值
- **用户名**: `admin`
- **密码**: `admin123`
- **角色**: `ADMIN`

#### 环境变量支持
- `SECURITY_USER_NAME`: 用户名
- `SECURITY_USER_PASSWORD`: 密码
- `SECURITY_USER_ROLES`: 角色（多个角色用逗号分隔）

### 安全配置详情

#### SecurityConfig.java
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.user.name:admin}")
    private String username;

    @Value("${spring.security.user.password:admin123}")
    private String password;

    @Value("${spring.security.user.roles:ADMIN}")
    private String roles;

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roles.split(","))
                .build();
        
        return new InMemoryUserDetailsManager(user);
    }
}
```

### 访问控制

#### 允许访问的路径
- `/auth/login` - 登录接口
- `/auth/logout` - 登出接口
- `/auth/captcha` - 验证码接口
- `/doc.html` - Knife4j文档界面
- `/webjars/**` - 静态资源
- `/v3/api-docs/**` - OpenAPI文档
- `/swagger-ui/**` - Swagger界面

#### 需要认证的路径
- 其他所有请求都需要认证

### 测试方法

#### 1. 启动服务
```bash
cd service/service-auth
./mvnw spring-boot:run
```

#### 2. 访问需要认证的接口
```bash
# 未认证访问（会返回401）
curl http://localhost:8081/auth/test/health

# 使用默认用户认证访问
curl -u admin:admin123 http://localhost:8081/auth/test/health
```

#### 3. 访问API文档
```bash
# 直接访问（已配置为允许访问）
curl http://localhost:8081/auth/doc.html
```

### 生产环境建议

#### 1. 使用环境变量
```bash
export SECURITY_USER_NAME=your_username
export SECURITY_USER_PASSWORD=your_secure_password
export SECURITY_USER_ROLES=ADMIN,USER
```

#### 2. 使用配置文件
创建 `application-prod.yml`：
```yaml
spring:
  security:
    user:
      name: ${PROD_USER_NAME}
      password: ${PROD_USER_PASSWORD}
      roles: ${PROD_USER_ROLES}
```

#### 3. 禁用默认用户
如果不需要默认用户，可以在配置中禁用：
```yaml
spring:
  security:
    user:
      name: ""
      password: ""
```

### 注意事项

1. **密码安全**: 生产环境请使用强密码
2. **角色管理**: 根据实际需求配置合适的角色
3. **环境隔离**: 不同环境使用不同的用户配置
4. **日志安全**: 避免在日志中输出密码信息

### 故障排除

#### 1. 配置不生效
- 检查配置文件格式是否正确
- 确认环境变量是否正确设置
- 验证SecurityConfig是否正确加载

#### 2. 认证失败
- 确认用户名和密码是否正确
- 检查密码是否被正确加密
- 验证角色配置是否正确

#### 3. 权限不足
- 检查用户角色是否包含所需权限
- 确认请求路径的权限配置
- 验证SecurityConfig中的授权配置 