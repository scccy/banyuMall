# Spring Cloud Alibaba 配置解决方案

## 问题描述
遇到错误：`org.springframework.cloud:spring-cloud-starter-gateway:jar:unknown was not found`

## 解决方案

### 1. 使用Spring Cloud Alibaba BOM
根据[Spring Cloud Alibaba官方文档](https://spring.io/projects/spring-cloud-alibaba)，已正确配置依赖管理：

```xml
<!-- 根POM中的配置 -->
<dependencyManagement>
    <dependencies>
        <!-- Spring Cloud BOM -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2025.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        
        <!-- Spring Cloud Alibaba BOM -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>2022.0.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. 版本兼容性
| Spring Boot版本 | Spring Cloud版本 | Spring Cloud Alibaba版本 |
|----------------|-----------------|------------------------|
| 3.5.x | 2025.0.x | 2022.0.0.0 |

### 3. 清理Maven缓存
```bash
# 方法1: 清理并强制更新
./mvnw clean -U

# 方法2: 删除本地仓库中的缓存
rm -rf ~/.m2/repository/org/springframework/cloud/spring-cloud-starter-gateway

# 方法3: 强制更新特定依赖
./mvnw dependency:purge-local-repository -DmanualInclude="org.springframework.cloud:spring-cloud-starter-gateway"
```

### 4. 添加Maven仓库配置
在根POM中添加仓库配置：

```xml
<repositories>
    <repository>
        <id>aliyun</id>
        <name>Aliyun Maven Repository</name>
        <url>https://maven.aliyun.com/repository/public</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```

### 5. 验证依赖配置
```bash
# 查看依赖树
./mvnw dependency:tree -Dverbose

# 编译项目
./mvnw clean compile
```

### 6. 项目结构
```
banyuMall/
├── infra/
│   ├── pom.xml
│   └── infra-gateway/          # 网关服务
│       ├── pom.xml
│       └── src/
├── service/
│   ├── service-auth/           # 认证服务
│   └── service-base/
└── pom.xml                     # 根POM
```

### 7. 启动命令
```bash
# 启动网关服务
./mvnw spring-boot:run -pl infra/infra-gateway

# 启动认证服务
./mvnw spring-boot:run -pl service/service-auth
```

## 注意事项
- 确保Java 21环境已安装
- 确保网络能够访问Maven仓库
- 首次启动可能需要较长时间下载依赖
- 如果仍有问题，可以尝试使用Maven Central仓库而不是阿里云仓库

## 参考文档
- [Spring Cloud Alibaba官方文档](https://spring.io/projects/spring-cloud-alibaba)
- [Spring Cloud官方文档](https://spring.io/projects/spring-cloud) 