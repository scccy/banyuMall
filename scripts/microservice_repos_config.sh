#!/bin/bash

# 微服务仓库配置模板
# 作者: scccy
# 用途: 配置各微服务的远程仓库地址

# ========================================
# 配置区域 - 请根据实际情况修改
# ========================================

# 远程仓库基础URL
# 例如: "https://github.com/your-org" 或 "http://117.50.221.113:8077/banyu"
REMOTE_BASE_URL=""

# 各微服务的远程仓库地址
# 格式: ["服务路径"]="完整远程仓库URL"
declare -A MICROSERVICES=(
    # 基础服务模块
    ["service/service-common"]=""      # 通用工具模块
    ["service/service-base"]=""        # 基础配置模块
    
    # 业务服务模块  
    ["service/service-auth"]=""        # 认证服务
    ["service/service-gateway"]=""     # 网关服务
    ["service/service-user"]=""        # 用户服务
    
    # 核心业务模块
    ["core/core-publisher"]=""         # 发布者核心服务
    
    # 第三方服务模块
    ["third-party/third-party-aliyunOss"]=""  # 阿里云OSS服务
)

# ========================================
# 配置示例
# ========================================

# 示例1: GitHub组织仓库
# REMOTE_BASE_URL="https://github.com/banyuMall"
# declare -A MICROSERVICES=(
#     ["service/service-common"]="https://github.com/banyuMall/service-common"
#     ["service/service-base"]="https://github.com/banyuMall/service-base"
#     ["service/service-auth"]="https://github.com/banyuMall/service-auth"
#     ["service/service-gateway"]="https://github.com/banyuMall/service-gateway"
#     ["service/service-user"]="https://github.com/banyuMall/service-user"
#     ["core/core-publisher"]="https://github.com/banyuMall/core-publisher"
#     ["third-party/third-party-aliyunOss"]="https://github.com/banyuMall/third-party-aliyunOss"
# )

# 示例2: GitLab仓库
# REMOTE_BASE_URL="http://117.50.221.113:8077/banyu"
# declare -A MICROSERVICES=(
#     ["service/service-common"]="http://117.50.221.113:8077/banyu/service-common.git"
#     ["service/service-base"]="http://117.50.221.113:8077/banyu/service-base.git"
#     ["service/service-auth"]="http://117.50.221.113:8077/banyu/service-auth.git"
#     ["service/service-gateway"]="http://117.50.221.113:8077/banyu/service-gateway.git"
#     ["service/service-user"]="http://117.50.221.113:8077/banyu/service-user.git"
#     ["core/core-publisher"]="http://117.50.221.113:8077/banyu/core-publisher.git"
#     ["third-party/third-party-aliyunOss"]="http://117.50.221.113:8077/banyu/third-party-aliyunOss.git"
# )

# ========================================
# 配置说明
# ========================================

# 1. 设置 REMOTE_BASE_URL
#    - 如果是GitHub: "https://github.com/your-org"
#    - 如果是GitLab: "http://your-gitlab-server/group"
#    - 如果是Gitee: "https://gitee.com/your-org"

# 2. 设置各微服务的远程仓库地址
#    - 每个微服务都需要一个独立的远程仓库
#    - 仓库名称建议与微服务名称一致
#    - 确保远程仓库已经创建（可以是空仓库）

# 3. 仓库创建建议
#    - 先在各Git平台创建空仓库
#    - 仓库名称: service-common, service-base, service-auth 等
#    - 设置默认分支为 main
#    - 不要初始化README文件

# 4. 权限要求
#    - 确保有推送权限到所有远程仓库
#    - 如果使用HTTPS，需要配置用户名密码或Token
#    - 如果使用SSH，需要配置SSH密钥

# ========================================
# 验证配置
# ========================================

validate_config() {
    echo "验证配置..."
    
    if [ -z "$REMOTE_BASE_URL" ]; then
        echo "❌ 错误: REMOTE_BASE_URL 未设置"
        return 1
    fi
    
    echo "✅ REMOTE_BASE_URL: $REMOTE_BASE_URL"
    
    for service_path in "${!MICROSERVICES[@]}"; do
        if [ -z "${MICROSERVICES[$service_path]}" ]; then
            echo "❌ 错误: $service_path 的远程仓库地址未配置"
            return 1
        else
            echo "✅ $service_path -> ${MICROSERVICES[$service_path]}"
        fi
    done
    
    echo "✅ 配置验证通过"
    return 0
}

# 如果直接运行此脚本，则验证配置
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    validate_config
fi 