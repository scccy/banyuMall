#!/bin/bash
# 阿里云OSS服务开发环境启动脚本

# 检查环境变量文件
if [ -f ".env" ]; then
    echo "加载环境变量文件 .env"
    source .env
else
    echo "警告: 未找到 .env 文件，请确保已设置必要的环境变量"
    echo "可以复制 env-example.sh 为 .env 并填入实际值"
fi

# 检查必要的环境变量
if [ -z "$OSS_ACCESS_KEY_ID" ] || [ -z "$OSS_ACCESS_KEY_SECRET" ]; then
    echo "错误: 缺少阿里云OSS密钥配置"
    echo "请设置以下环境变量:"
    echo "  OSS_ACCESS_KEY_ID"
    echo "  OSS_ACCESS_KEY_SECRET"
    exit 1
fi

echo "启动阿里云OSS服务..."
echo "服务端口: 8085"
echo "OSS端点: ${OSS_ENDPOINT:-oss-cn-hangzhou.aliyuncs.com}"
echo "存储桶: ${OSS_BUCKET_NAME:-banyumall-files}"

# 启动服务
mvn spring-boot:run -Dspring-boot.run.profiles=dev 