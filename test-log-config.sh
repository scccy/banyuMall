#!/bin/bash

echo "=== 测试日志配置 ==="

# 检查当前日志目录
echo "当前日志目录内容："
ls -la logs/

# 测试service-user服务
echo -e "\n测试service-user服务："
curl -X GET http://localhost:8082/user/test

# 等待一下让日志生成
sleep 2

# 检查是否有新的日志文件生成
echo -e "\n检查日志文件："
ls -la logs/

# 检查service-user日志目录
if [ -d "logs/service-user" ]; then
    echo -e "\nservice-user日志文件："
    ls -la logs/service-user/
    
    if [ -f "logs/service-user/service-user.log" ]; then
        echo -e "\nservice-user.log的最后几行："
        tail -5 logs/service-user/service-user.log
    fi
else
    echo -e "\n未找到service-user日志目录，检查unknown-service目录："
    if [ -d "logs/unknown-service" ]; then
        echo -e "\nunknown-service日志文件："
        ls -la logs/unknown-service/
        
        if [ -f "logs/unknown-service/unknown-service.log" ]; then
            echo -e "\nunknown-service.log的最后几行："
            tail -5 logs/unknown-service/unknown-service.log
        fi
    fi
fi

echo -e "\n=== 测试完成 ===" 