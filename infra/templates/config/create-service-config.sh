#!/bin/bash

# BanyuMall微服务配置文件生成脚本
# 使用方法: ./create-service-config.sh <service-name>

set -e

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
    echo "BanyuMall微服务配置文件生成脚本"
    echo ""
    echo "使用方法:"
    echo "  $0 <service-name>"
    echo ""
    echo "参数:"
    echo "  service-name    服务名称 (service-auth, service-user, service-gateway等)"
    echo ""
    echo "示例:"
    echo "  $0 service-auth"
    echo "  $0 service-user"
    echo "  $0 service-gateway"
    echo ""
    echo "功能:"
    echo "  1. 创建服务配置文件结构"
    echo "  2. 生成各环境配置文件"
    echo "  3. 创建Nacos配置模板"
    echo "  4. 更新配置模板内容"
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

# 验证服务名称
VALID_SERVICES=("service-auth" "service-user" "service-gateway" "service-base" "service-task" "service-points" "service-file" "service-sync")
if [[ ! " ${VALID_SERVICES[@]} " =~ " ${SERVICE_NAME} " ]]; then
    log_warning "服务名称不在预定义列表中，但将继续创建配置"
fi

# 设置目录路径
SERVICE_DIR="service/${SERVICE_NAME}"
RESOURCES_DIR="${SERVICE_DIR}/src/main/resources"
TEMPLATES_DIR="infra/templates/config"

# 检查服务目录是否存在
if [ ! -d "$SERVICE_DIR" ]; then
    log_error "服务目录不存在: $SERVICE_DIR"
    log_info "请先创建服务模块目录"
    exit 1
fi

# 创建resources目录
if [ ! -d "$RESOURCES_DIR" ]; then
    log_info "创建resources目录: $RESOURCES_DIR"
    mkdir -p "$RESOURCES_DIR"
fi

# 生成配置文件
log_info "开始生成配置文件..."

# 1. 生成主配置文件
log_info "生成主配置文件: application.yml"
cp "${TEMPLATES_DIR}/application.yml.template" "${RESOURCES_DIR}/application.yml"

# 替换服务名称
sed -i.bak "s/service-xxx/${SERVICE_NAME}/g" "${RESOURCES_DIR}/application.yml"
rm "${RESOURCES_DIR}/application.yml.bak"

# 2. 生成开发环境配置
log_info "生成开发环境配置: application-dev.yml"
cp "${TEMPLATES_DIR}/application-dev.yml.template" "${RESOURCES_DIR}/application-dev.yml"

# 3. 生成测试环境配置
log_info "生成测试环境配置: application-test.yml"
cp "${TEMPLATES_DIR}/application-test.yml.template" "${RESOURCES_DIR}/application-test.yml"

# 4. 生成生产环境配置
log_info "生成生产环境配置: application-prod.yml"
cp "${TEMPLATES_DIR}/application-prod.yml.template" "${RESOURCES_DIR}/application-prod.yml"

# 5. 生成Docker环境配置
log_info "生成Docker环境配置: application-docker.yml"
cp "${TEMPLATES_DIR}/application-docker.yml.template" "${RESOURCES_DIR}/application-docker.yml"

# 6. 生成Nacos配置模板（公有配置）
log_info "生成Nacos公有配置模板"
NACOS_TEMPLATE="${TEMPLATES_DIR}/nacos-config-template-${SERVICE_NAME}.yml"
cp "${TEMPLATES_DIR}/nacos-config-template.yml" "$NACOS_TEMPLATE"

log_info "Nacos公有配置模板已生成: $NACOS_TEMPLATE"
log_info "注意: 此模板包含所有微服务共享的配置项，个性配置在各微服务的本地配置文件中管理"

# 显示生成结果
log_success "配置文件生成完成!"
log_info "生成的文件:"
echo "  ${RESOURCES_DIR}/application.yml"
echo "  ${RESOURCES_DIR}/application-dev.yml"
echo "  ${RESOURCES_DIR}/application-test.yml"
echo "  ${RESOURCES_DIR}/application-prod.yml"
echo "  ${RESOURCES_DIR}/application-docker.yml"
echo "  ${NACOS_TEMPLATE}"

# 显示使用说明
log_info "使用说明:"
echo "1. 检查并修改生成的配置文件"
echo "2. 将Nacos公有配置模板上传到Nacos控制台（命名为: ${SERVICE_NAME}.yaml）"
echo "3. 在Nacos中配置敏感信息（数据库密码、Redis密码等）"
echo "4. 在各微服务的本地配置文件中添加个性配置"
echo "5. 启动服务验证配置"

log_success "配置生成完成!" 