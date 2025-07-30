#!/bin/bash

# 部署脚本
# 用于部署BanyuMall项目

set -e

# 配置变量
PROJECT_NAME="banyuMall"
DEPLOY_ENV="dev"
SERVICE_LIST=("service-auth" "service-user" "infra-gateway")

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$(dirname "$SCRIPT_DIR")")"

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# 显示使用说明
show_usage() {
    echo "部署脚本"
    echo ""
    echo "用法: $0 [选项] [服务名]"
    echo ""
    echo "选项:"
    echo "  -e, --env ENV        部署环境 (dev/test/prod, 默认: dev)"
    echo "  -c, --clean          清理构建"
    echo "  -b, --build          构建项目"
    echo "  -d, --deploy         部署服务"
    echo "  -a, --all            执行所有步骤"
    echo "  --help               显示此帮助信息"
    echo ""
    echo "服务名:"
    echo "  all                  所有服务"
    echo "  auth                 认证服务"
    echo "  user                 用户服务"
    echo "  gateway              网关服务"
    echo ""
    echo "示例:"
    echo "  $0 -a all             # 构建并部署所有服务"
    echo "  $0 -b auth            # 构建认证服务"
    echo "  $0 -d user            # 部署用户服务"
    echo "  $0 -c                 # 清理构建"
}

# 清理构建
clean_build() {
    log_step "清理构建文件..."
    
    cd "$PROJECT_ROOT"
    
    if [ -f "mvnw" ]; then
        ./mvnw clean
        log_info "Maven清理完成"
    else
        log_warn "未找到Maven Wrapper，跳过清理"
    fi
    
    # 清理日志文件
    if [ -d "logs" ]; then
        rm -rf logs/*
        log_info "日志文件清理完成"
    fi
}

# 构建项目
build_project() {
    local service_name="$1"
    
    log_step "构建项目: $service_name"
    
    cd "$PROJECT_ROOT"
    
    if [ "$service_name" = "all" ]; then
        if [ -f "mvnw" ]; then
            ./mvnw clean package -DskipTests
            log_info "所有服务构建完成"
        else
            log_error "未找到Maven Wrapper"
            exit 1
        fi
    else
        local module_path=""
        case "$service_name" in
            "auth")
                module_path="service/service-auth"
                ;;
            "user")
                module_path="service/service-user"
                ;;
            "gateway")
                module_path="service/service-gateway"
                ;;
            *)
                log_error "未知服务: $service_name"
                exit 1
                ;;
        esac
        
        if [ -f "mvnw" ]; then
            ./mvnw clean package -pl "$module_path" -DskipTests
            log_info "服务 $service_name 构建完成"
        else
            log_error "未找到Maven Wrapper"
            exit 1
        fi
    fi
}

# 部署服务
deploy_service() {
    local service_name="$1"
    
    log_step "部署服务: $service_name"
    
    cd "$PROJECT_ROOT"
    
    case "$service_name" in
        "auth")
            deploy_auth_service
            ;;
        "user")
            deploy_user_service
            ;;
        "gateway")
            deploy_gateway_service
            ;;
        "all")
            deploy_all_services
            ;;
        *)
            log_error "未知服务: $service_name"
            exit 1
            ;;
    esac
}

# 部署认证服务
deploy_auth_service() {
    log_info "部署认证服务..."
    
    # 检查JAR文件是否存在
    local jar_file="service/service-auth/target/service-auth-*.jar"
    if ! ls $jar_file >/dev/null 2>&1; then
        log_error "认证服务JAR文件不存在，请先构建项目"
        exit 1
    fi
    
    # 停止现有服务
    pkill -f "service-auth" || true
    
    # 启动服务
    nohup java -jar service/service-auth/target/service-auth-*.jar \
        --spring.profiles.active="$DEPLOY_ENV" > logs/auth-service.log 2>&1 &
    
    log_info "认证服务启动完成，PID: $!"
    log_info "日志文件: logs/auth-service.log"
}

# 部署用户服务
deploy_user_service() {
    log_info "部署用户服务..."
    
    # 检查JAR文件是否存在
    local jar_file="service/service-user/target/service-user-*.jar"
    if ! ls $jar_file >/dev/null 2>&1; then
        log_error "用户服务JAR文件不存在，请先构建项目"
        exit 1
    fi
    
    # 停止现有服务
    pkill -f "service-user" || true
    
    # 启动服务
    nohup java -jar service/service-user/target/service-user-*.jar \
        --spring.profiles.active="$DEPLOY_ENV" > logs/user-service.log 2>&1 &
    
    log_info "用户服务启动完成，PID: $!"
    log_info "日志文件: logs/user-service.log"
}

# 部署网关服务
deploy_gateway_service() {
    log_info "部署网关服务..."
    
    # 检查JAR文件是否存在
    local jar_file="service/service-gateway/target/service-gateway-*.jar"
    if ! ls $jar_file >/dev/null 2>&1; then
        log_error "网关服务JAR文件不存在，请先构建项目"
        exit 1
    fi
    
    # 停止现有服务
    pkill -f "service-gateway" || true
    
    # 启动服务
    nohup java -jar service/service-gateway/target/service-gateway-*.jar \
        --spring.profiles.active="$DEPLOY_ENV" > logs/gateway-service.log 2>&1 &
    
    log_info "网关服务启动完成，PID: $!"
    log_info "日志文件: logs/gateway-service.log"
}

# 部署所有服务
deploy_all_services() {
    log_info "部署所有服务..."
    
    deploy_auth_service
    sleep 5
    
    deploy_user_service
    sleep 5
    
    deploy_gateway_service
    
    log_info "所有服务部署完成"
}

# 检查服务状态
check_service_status() {
    local service_name="$1"
    
    log_info "检查服务状态: $service_name"
    
    case "$service_name" in
        "auth")
            if pgrep -f "service-auth" >/dev/null; then
                log_info "认证服务运行中"
            else
                log_warn "认证服务未运行"
            fi
            ;;
        "user")
            if pgrep -f "service-user" >/dev/null; then
                log_info "用户服务运行中"
            else
                log_warn "用户服务未运行"
            fi
            ;;
        "gateway")
            if pgrep -f "service-gateway" >/dev/null; then
                log_info "网关服务运行中"
            else
                log_warn "网关服务未运行"
            fi
            ;;
        "all")
            check_service_status "auth"
            check_service_status "user"
            check_service_status "gateway"
            ;;
    esac
}

# 解析命令行参数
CLEAN=false
BUILD=false
DEPLOY=false
SERVICE="all"

while [[ $# -gt 0 ]]; do
    case $1 in
        -e|--env)
            DEPLOY_ENV="$2"
            shift 2
            ;;
        -c|--clean)
            CLEAN=true
            shift
            ;;
        -b|--build)
            BUILD=true
            shift
            ;;
        -d|--deploy)
            DEPLOY=true
            shift
            ;;
        -a|--all)
            CLEAN=true
            BUILD=true
            DEPLOY=true
            shift
            ;;
        --help)
            show_usage
            exit 0
            ;;
        auth|user|gateway|all)
            SERVICE="$1"
            shift
            ;;
        *)
            log_error "未知参数: $1"
            show_usage
            exit 1
            ;;
    esac
done

# 主函数
main() {
    log_info "开始部署 $PROJECT_NAME"
    log_info "部署环境: $DEPLOY_ENV"
    log_info "目标服务: $SERVICE"
    echo
    
    # 创建日志目录
    mkdir -p "$PROJECT_ROOT/logs"
    
    # 执行清理
    if [ "$CLEAN" = true ]; then
        clean_build
    fi
    
    # 执行构建
    if [ "$BUILD" = true ]; then
        build_project "$SERVICE"
    fi
    
    # 执行部署
    if [ "$DEPLOY" = true ]; then
        deploy_service "$SERVICE"
        
        # 等待服务启动
        log_info "等待服务启动..."
        sleep 10
        
        # 检查服务状态
        check_service_status "$SERVICE"
    fi
    
    log_info "部署完成！"
    
    # 显示服务信息
    echo
    log_info "服务访问地址:"
    log_info "  - 认证服务: http://localhost:8081/auth"
    log_info "  - 用户服务: http://localhost:8082/user"
    log_info "  - 网关服务: http://localhost:8080"
    log_info "  - API文档: http://localhost:8081/auth/doc.html"
}

# 执行主函数
main "$@" 