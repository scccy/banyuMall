#!/bin/bash

# Gateway服务 - 开发环境启动脚本
# 用于解决SLF4J警告问题

echo "🚀 启动Gateway服务（开发环境）..."

# 设置JVM参数，禁用SLF4J警告
export JAVA_OPTS="$JAVA_OPTS \
-Dlogging.file.name=service-gateway \
-Dlog4j2.disable.jmx=true \
-Dlog4j2.skipJansi=true \
-Dlog4j2.statusLogger.level=ERROR \
-Dlog4j2.configurationFactory=org.apache.logging.log4j.core.config.ConfigurationFactory \
-Dlog4j2.disableStatusLogger=true"

# 设置Spring Boot配置
export SPRING_PROFILES_ACTIVE=dev

# 启动应用
echo "📝 使用配置: dev"
echo "🔧 JVM参数: $JAVA_OPTS"
echo "🌱 Spring Profile: $SPRING_PROFILES_ACTIVE"

# 使用Maven启动
./mvnw spring-boot:run -pl service/service-gateway \
  -Dspring-boot.run.profiles=dev \
  -Dspring-boot.run.jvmArguments="$JAVA_OPTS" 