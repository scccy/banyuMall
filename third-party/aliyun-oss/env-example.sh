#!/bin/bash
# 阿里云OSS服务环境变量配置示例
# 请复制此文件为 .env 并填入实际的值

# 阿里云OSS配置
export OSS_ENDPOINT="oss-cn-hangzhou.aliyuncs.com"
export OSS_ACCESS_KEY_ID="your-actual-access-key-id"
export OSS_ACCESS_KEY_SECRET="your-actual-access-key-secret"
export OSS_BUCKET_NAME="banyumall-files"

# Nacos配置
export NACOS_SERVER="117.50.197.170:8848"
export NACOS_NAMESPACE="ba1493dc-20fa-413b-84e1-d929c9a4aeac"

# 数据库配置
export MYSQL_HOST="localhost"
export MYSQL_PORT="3306"
export MYSQL_DATABASE="banyumall"
export MYSQL_USERNAME="root"
export MYSQL_PASSWORD="your-database-password"

echo "环境变量配置完成"
echo "请确保在启动服务前设置这些环境变量" 