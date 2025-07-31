#!/bin/bash

# service-user启动脚本
# 设置正确的日志系统属性

echo "启动service-user服务..."

# 设置系统属性
export JAVA_OPTS="-Dservice.name=service-user -Dlog4j2.configurationFile=classpath:log4j2.xml"

# 启动服务
cd "$(dirname "$0")"
mvn spring-boot:run -Dspring-boot.run.jvmArguments="$JAVA_OPTS"

echo "服务启动完成，日志文件将生成在 logs/service-user/ 目录下" 