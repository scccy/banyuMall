#!/bin/bash

# 微服务Git仓库创建脚本（使用main分支）
# 作者: scccy

# 定义微服务列表
MICROSERVICES=(
    "service/service-common"
    "service/service-base"
    "service/service-auth"
    "service/service-gateway" 
    "service/service-user"
    "core/core-publisher"
    "third-party/third-party-aliyunOss"
)

# 远程仓库基础URL
REMOTE_BASE_URL="http://117.50.221.113:8077/banyu"

# 临时目录
TEMP_DIR="../temp-microservices"

echo "开始创建微服务独立Git仓库（使用main分支）..."

# 创建临时目录
mkdir -p $TEMP_DIR

# 遍历每个微服务
for service in "${MICROSERVICES[@]}"; do
    # 提取服务名称
    service_name=$(basename $service)
    
    echo "处理微服务: $service_name"
    
    # 复制服务目录
    cp -r $service $TEMP_DIR/
    
    # 进入服务目录
    cd $TEMP_DIR/$service_name
    
    # 删除原有的.git目录
    rm -rf .git
    
    # 初始化新的Git仓库
    git init
    
    # 设置默认分支为main
    git branch -M main
    
    # 添加所有文件
    git add .
    
    # 提交初始代码
    git commit -m "feat: 初始化${service_name}微服务仓库（main分支）"
    
    # 添加远程仓库
    git remote add origin $REMOTE_BASE_URL/$service_name.git
    
    # 先删除远程master分支（如果存在）
    echo "删除远程master分支（如果存在）..."
    git push origin --delete master 2>/dev/null || echo "master分支不存在或已删除"
    
    # 强制推送到远程仓库，设置main为默认分支
    echo "强制推送 $service_name 到远程仓库（main分支）..."
    git push -f -u origin main
    
    echo "$service_name 处理完成"
    echo "----------------------------------------"
    
    # 返回原项目目录
    cd /Volumes/project/github/banyuMall
done

echo "所有微服务仓库创建完成（使用main分支）！"
echo "临时目录: $TEMP_DIR" 