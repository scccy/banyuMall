#!/bin/bash

# BanyuMall Docker镜像构建脚本
# 使用方法: ./build.sh <service-name> [environment] [version]

set -e

# 配置
REGISTRY="banyumall"
DEFAULT_ENV="dev"
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
    echo "BanyuMall Docker镜像构建脚本"
    echo ""
    echo "使用方法:"
    echo "  $0 <service-name> [environment] [version]"
    echo ""
    echo "参数:"
    echo "  service-name    服务名称 (service-auth, service-user, service-gateway等)"
    echo "  environment     环境 (dev, test, prod) [默认: dev]"
    echo "  version         版本号 [默认: latest]"
    echo ""
    echo "示例:"
    echo "  $0 service-auth"
    echo "  $0 service-user prod v1.0.0"
    echo "  $0 service-gateway test v1.2.3"
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
ENVIRONMENT=${2:-$DEFAULT_ENV}
VERSION=${3:-$DEFAULT_VERSION}

# 验证服务名称
VALID_SERVICES=("service-auth" "service-user" "service-gateway" "service-base")
if [[ ! " ${VALID_SERVICES[@]} " =~ " ${SERVICE_NAME} " ]]; then
    log_error "不支持的服务: $SERVICE_NAME"
    echo "支持的服务: ${VALID_SERVICES[*]}"
    exit 1
fi

# 验证环境
VALID_ENVIRONMENTS=("dev" "test" "prod")
if [[ ! " ${VALID_ENVIRONMENTS[@]} " =~ " ${ENVIRONMENT} " ]]; then
    log_error "不支持的环境: $ENVIRONMENT"
    echo "支持的环境: ${VALID_ENVIRONMENTS[*]}"
    exit 1
fi

# 设置镜像标签
IMAGE_NAME="${REGISTRY}/${SERVICE_NAME}"
IMAGE_TAG="${IMAGE_NAME}:${VERSION}"
IMAGE_TAG_ENV="${IMAGE_NAME}:${ENVIRONMENT}"

# 检查服务目录是否存在
SERVICE_DIR="service/${SERVICE_NAME}"
if [ ! -d "$SERVICE_DIR" ]; then
    log_error "服务目录不存在: $SERVICE_DIR"
    exit 1
fi

# 检查Dockerfile是否存在
if [ ! -f "$SERVICE_DIR/Dockerfile" ]; then
    log_warning "Dockerfile不存在，使用模板创建..."
    cp infra/docker/templates/Dockerfile.template "$SERVICE_DIR/Dockerfile"
    log_success "已创建Dockerfile: $SERVICE_DIR/Dockerfile"
fi

# 检查.dockerignore是否存在
if [ ! -f "$SERVICE_DIR/.dockerignore" ]; then
    log_warning ".dockerignore不存在，使用模板创建..."
    cp infra/docker/templates/dockerignore.template "$SERVICE_DIR/.dockerignore"
    log_success "已创建.dockerignore: $SERVICE_DIR/.dockerignore"
fi

# 开始构建
log_info "开始构建Docker镜像..."
log_info "服务: $SERVICE_NAME"
log_info "环境: $ENVIRONMENT"
log_info "版本: $VERSION"
log_info "镜像: $IMAGE_TAG"

# 构建镜像
cd "$SERVICE_DIR"
docker build \
    --build-arg ENVIRONMENT="$ENVIRONMENT" \
    --build-arg VERSION="$VERSION" \
    -t "$IMAGE_TAG" \
    -t "$IMAGE_TAG_ENV" \
    .

# 检查构建结果
if [ $? -eq 0 ]; then
    log_success "镜像构建成功!"
    log_info "镜像标签:"
    echo "  $IMAGE_TAG"
    echo "  $IMAGE_TAG_ENV"
    
    # 显示镜像信息
    log_info "镜像信息:"
    docker images "$IMAGE_NAME" --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"
else
    log_error "镜像构建失败!"
    exit 1
fi

log_success "构建完成!" 