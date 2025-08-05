#!/bin/bash

# 修复远程分支脚本
# 作者: scccy

# 定义微服务列表
MICROSERVICES=(
    "service-common"
    "service-base"
    "service-auth"
    "service-gateway" 
    "service-user"
    "core-publisher"
    "third-party-aliyunOss"
)

# 远程仓库基础URL
REMOTE_BASE_URL="http://117.50.221.113:8077/banyu"

# 临时目录
TEMP_DIR="../temp-microservices"

echo "开始修复远程分支，删除master分支并设置main为默认分支..."

# 遍历每个微服务
for service in "${MICROSERVICES[@]}"; do
    echo "处理微服务: $service"
    
    # 进入服务目录
    cd $TEMP_DIR/$service
    
    # 删除远程master分支
    echo "删除远程master分支..."
    git push origin --delete master 2>/dev/null || echo "master分支不存在或已删除"
    
    # 强制推送main分支
    echo "强制推送main分支..."
    git push -f origin main
    
    echo "$service 处理完成"
    echo "----------------------------------------"
    
    # 返回原项目目录
    cd /Volumes/project/github/banyuMall
done

echo "所有远程分支修复完成！" 