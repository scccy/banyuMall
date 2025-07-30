# MySQL连接器坐标更新说明

## 更新背景

MySQL官方已将MySQL Connector/J的Maven坐标从旧的反向DNS格式更新为符合Maven 2+规范的反向DNS格式。

## 变更详情

### 旧坐标（已弃用）
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

### 新坐标（推荐）
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>
```

## 更新原因

1. **Maven规范**: 符合Maven 2+的反向DNS坐标规范
2. **官方推荐**: MySQL官方推荐使用新的坐标
3. **避免警告**: 消除Maven构建时的警告信息
4. **未来兼容**: 确保与未来版本的兼容性

## 已更新的文件

- ✅ `pom.xml` (根目录)
- ✅ `service/pom.xml`
- ✅ `service/service-user/pom.xml`

## 影响说明

### 功能影响
- **无功能影响**: 新坐标提供相同的功能
- **版本一致**: 版本号保持不变 (8.0.33)
- **向后兼容**: 完全向后兼容

### 构建影响
- **消除警告**: 不再出现坐标变更警告
- **构建速度**: 可能略微提升构建速度（避免重定向）

## 验证方法

### 1. 检查依赖
```bash
./mvnw dependency:tree | grep mysql
```

### 2. 清理并重新构建
```bash
./mvnw clean compile
```

### 3. 检查警告
确保不再出现以下警告：
```
[WARNING] The artifact mysql:mysql-connector-java:jar:8.0.33 has been relocated to com.mysql:mysql-connector-j:jar:8.0.33
```

## 最佳实践

1. **统一管理**: 在根pom.xml的dependencyManagement中统一管理版本
2. **版本控制**: 定期检查MySQL连接器的新版本
3. **测试验证**: 更新后确保数据库连接功能正常

## 相关链接

- [MySQL Connector/J官方文档](https://dev.mysql.com/doc/connector-j/8.0/en/)
- [Maven坐标规范](https://maven.apache.org/guides/mini/guide-naming-conventions.html) 