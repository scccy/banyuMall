#!/bin/bash

# BanyuMall Docker镜像推送脚本
# 使用方法: ./push.sh <service-name> [version]

set -e

# 配置
REGISTRY="banyumall"
DEFAULT_VERSION="latest"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 显示帮助信息
show_help() {
    echo "BanyuMall Docker镜像推送脚本"
    echo ""
    echo "使用方法:"
    echo "  $0 <service-name> [version]"
    echo ""
    echo "参数:"
    echo "  service-name    服务名称 (service-auth, service-user, service-gateway等)"
    echo "  version         版本号 [默认: latest]"
    echo ""
    echo "示例:"
    echo "  $0 service-auth"
    echo "  $0 service-user v1.0.0"
    echo "  $0 service-gateway v1.2.3"
    echo ""
    echo "支持的服务:"
    echo "  - service-auth     认证授权服务"
    echo "  - service-user     用户管理服务"
    echo "  - service-gateway  API网关服务"
    echo "  - service-base     基础服务模块"
}

# 检查参数
if [ $# -eq 0 ]; then
    show_help
    exit 1
fi

if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
    show_help
    exit 0
fi

# 解析参数
SERVICE_NAME=$1
VERSION=${2:-$DEFAULT_VERSION}

# 验证服务名称
VALID_SERVICES=("service-auth" "service-user" "service-gateway" "service-base")
if [[ ! " ${VALID_SERVICES[@]} " =~ " ${SERVICE_NAME} " ]]; then
    log_error "不支持的服务: $SERVICE_NAME"
    echo "支持的服务: ${VALID_SERVICES[*]}"
    exit 1
fi

# 设置镜像标签
IMAGE_NAME="${REGISTRY}/${SERVICE_NAME}"
IMAGE_TAG="${IMAGE_NAME}:${VERSION}"

# 检查镜像是否存在
if ! docker images "$IMAGE_TAG" | grep -q "$IMAGE_NAME"; then
    log_error "镜像不存在: $IMAGE_TAG"
    log_info "请先运行构建脚本: ./build.sh $SERVICE_NAME"
    exit 1
fi

# 检查Docker登录状态
if ! docker info | grep -q "Username"; then
    log_warning "未检测到Docker登录，尝试登录..."
    log_info "请确保已配置Docker Registry认证信息"
fi

# 开始推送
log_info "开始推送Docker镜像..."
log_info "服务: $SERVICE_NAME"
log_info "版本: $VERSION"
log_info "镜像: $IMAGE_TAG"

# 推送镜像
docker push "$IMAGE_TAG"

# 检查推送结果
if [ $? -eq 0 ]; then
    log_success "镜像推送成功!"
    log_info "推送的镜像: $IMAGE_TAG"
else
    log_error "镜像推送失败!"
    exit 1
fi

log_success "推送完成!" 