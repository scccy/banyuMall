#!/bin/bash

# 设置日志环境变量
echo "设置日志环境变量..."

# 设置服务名称环境变量
export SERVICE_NAME=service-user

# 设置系统属性
export JAVA_OPTS="-Dservice.name=$SERVICE_NAME"

echo "环境变量设置完成："
echo "SERVICE_NAME=$SERVICE_NAME"
echo "JAVA_OPTS=$JAVA_OPTS"

echo ""
echo "现在可以使用以下命令启动服务："
echo "cd service/service-user && mvn spring-boot:run -Dspring-boot.run.jvmArguments=\"$JAVA_OPTS\"" 