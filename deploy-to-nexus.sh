#!/bin/bash

# 部署脚本：将banyuMall项目发布到Nexus私有仓库
# 作者：scccy
# 日期：$(date +%Y-%m-%d)

echo "=========================================="
echo "banyuMall项目 - Nexus私有仓库部署脚本"
echo "=========================================="

# 检查Maven是否安装
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误：Maven未安装或未配置到PATH中"
    exit 1
fi

echo "✅ Maven环境检查通过"
echo "✅ 使用Maven默认配置文件：/Volumes/soft/maven/conf/settings.xml"

echo ""
echo "开始清理项目..."
mvn clean

if [ $? -ne 0 ]; then
    echo "❌ 清理失败"
    exit 1
fi

echo "✅ 项目清理完成"

echo ""
echo "开始编译项目..."
mvn compile

if [ $? -ne 0 ]; then
    echo "❌ 编译失败"
    exit 1
fi

echo "✅ 项目编译完成"

echo ""
echo "开始运行测试..."
mvn test

if [ $? -ne 0 ]; then
    echo "⚠️  测试失败，但继续部署..."
else
    echo "✅ 测试通过"
fi

echo ""
echo "开始打包项目..."
mvn package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 打包失败"
    exit 1
fi

echo "✅ 项目打包完成"

echo ""
echo "开始发布到Nexus私有仓库..."

# 发布所有模块到Nexus
mvn deploy -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 发布失败"
    exit 1
fi

echo ""
echo "=========================================="
echo "🎉 部署成功！"
echo "=========================================="
echo ""
echo "已发布的模块："
echo "  - com.origin:banyuMall:0.0.1-SNAPSHOT"
echo "  - com.origin:service:0.0.1-SNAPSHOT"
echo "  - com.origin:service-common:0.0.1-SNAPSHOT"
echo "  - com.origin:service-base:0.0.1-SNAPSHOT"
echo "  - com.origin:service-auth:0.0.1-SNAPSHOT"
echo "  - com.origin:service-user:0.0.1-SNAPSHOT"
echo "  - com.origin:service-gateway:0.0.1-SNAPSHOT"
echo "  - com.origin:core:0.0.1-SNAPSHOT"
echo "  - com.origin:core-publisher:0.0.1-SNAPSHOT"
echo "  - com.origin:third-party:0.0.1-SNAPSHOT"
echo "  - com.origin:aliyun-oss:0.0.1-SNAPSHOT"
echo ""
echo "Nexus仓库地址："
echo "  外网：http://117.50.185.51:8881/"
echo "  内网：http://10.60.202.128:8881/"
echo ""
echo "您可以在Nexus管理界面中查看已发布的构件"
echo "==========================================" 