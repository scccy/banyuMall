# 规则名称: MyBatis-Plus依赖版本必须与父类保持一致

## 触发情景 (Context/Trigger)
当项目中添加新的微服务模块或修改现有模块的MyBatis-Plus依赖配置时。

## 指令 (Directive)

### 1. 依赖版本一致性
- **必须 (MUST)** 使用 `mybatis-plus-spring-boot3-starter` 而不是 `mybatis-plus-boot-starter`
- **必须 (MUST)** 不指定版本号，让父类pom.xml中的版本管理生效
- **禁止 (MUST NOT)** 在子模块中覆盖父类定义的MyBatis-Plus版本

### 2. 配置一致性
- **必须 (MUST)** 在启动类中添加 `@MapperScan` 注解
- **必须 (MUST)** 使用正确的Mapper包路径：`@MapperScan("com.origin.{module}.mapper")`
- **必须 (MUST)** 在MyBatisPlusConfig中只配置分页插件，不配置SqlSessionFactory

### 3. 依赖排除规则
- **必须 (MUST)** 在MyBatis-Plus依赖中排除Jackson相关依赖
- **必须 (MUST)** 保持FastJSON2作为序列化方案的一致性

## 理由 (Justification)
此规则源于任务 `task_20250801_2340_cache_configuration_fix.md`。在该任务中，发布者服务使用了错误的MyBatis-Plus依赖 `mybatis-plus-boot-starter`，而父类定义的是 `mybatis-plus-spring-boot3-starter`，导致Spring Boot启动时出现 `IllegalArgumentException: Invalid value type for attribute 'factoryBeanObjectType': java.lang.String` 错误。

## 技术细节
- 父类pom.xml中MyBatis-Plus版本：3.5.12
- 正确的依赖：`mybatis-plus-spring-boot3-starter`
- 错误的依赖：`mybatis-plus-boot-starter`
- Spring Boot版本：3.2.5

## 影响范围
- 所有使用MyBatis-Plus的微服务模块
- 新创建的微服务模块
- 依赖配置修改时

## 验证方法
1. 检查pom.xml中的MyBatis-Plus依赖配置
2. 确认启动类中有@MapperScan注解
3. 验证服务能够正常启动，无FactoryBean相关错误 