#!/bin/bash

# 微服务Git仓库拆分脚本
# 作者: scccy
# 用途: 将当前全局git仓库拆分为独立的微服务仓库

set -e  # 遇到错误立即退出

# 配置变量 - 请根据实际情况修改
REMOTE_BASE_URL=""  # 远程仓库基础URL，例如: "https://github.com/your-org"
BACKUP_DIR="../banyuMall-backup-$(date +%Y%m%d_%H%M%S)"
TEMP_DIR="../temp-microservices-$(date +%Y%m%d_%H%M%S)"

# 微服务列表及其远程仓库地址
declare -A MICROSERVICES=(
    ["service/service-common"]=""
    ["service/service-base"]=""
    ["service/service-auth"]=""
    ["service/service-gateway"]=""
    ["service/service-user"]=""
    ["core/core-publisher"]=""
    ["third-party/third-party-aliyunOss"]=""
)

# 颜色输出函数
print_info() {
    echo -e "\033[32m[INFO]\033[0m $1"
}

print_warn() {
    echo -e "\033[33m[WARN]\033[0m $1"
}

print_error() {
    echo -e "\033[31m[ERROR]\033[0m $1"
}

# 检查配置
check_config() {
    print_info "检查配置..."
    
    if [ -z "$REMOTE_BASE_URL" ]; then
        print_error "请设置 REMOTE_BASE_URL 变量"
        exit 1
    fi
    
    # 检查是否有未配置的微服务
    for service_path in "${!MICROSERVICES[@]}"; do
        if [ -z "${MICROSERVICES[$service_path]}" ]; then
            print_error "微服务 $service_path 的远程仓库地址未配置"
            exit 1
        fi
    done
    
    print_info "配置检查通过"
}

# 创建备份
create_backup() {
    print_info "创建项目备份到: $BACKUP_DIR"
    mkdir -p "$BACKUP_DIR"
    cp -r . "$BACKUP_DIR/"
    print_info "备份完成"
}

# 拆分单个微服务
split_microservice() {
    local service_path=$1
    local remote_url=$2
    local service_name=$(basename "$service_path")
    
    print_info "开始拆分微服务: $service_name"
    
    # 创建临时目录
    local temp_service_dir="$TEMP_DIR/$service_name"
    mkdir -p "$temp_service_dir"
    
    # 复制微服务目录
    print_info "复制 $service_path 到临时目录"
    cp -r "$service_path"/* "$temp_service_dir/"
    
    # 复制必要的根目录文件
    if [ -f "pom.xml" ]; then
        cp "pom.xml" "$temp_service_dir/"
    fi
    
    # 进入临时目录
    cd "$temp_service_dir"
    
    # 初始化Git仓库
    print_info "初始化Git仓库"
    git init
    git branch -M main
    
    # 添加所有文件
    git add .
    
    # 提交初始代码
    git commit -m "feat: 初始化 $service_name 微服务仓库
    
    - 从 banyuMall 项目拆分
    - 时间: $(date)
    - 作者: scccy"
    
    # 添加远程仓库
    print_info "添加远程仓库: $remote_url"
    git remote add origin "$remote_url"
    
    # 推送到远程仓库
    print_info "推送到远程仓库"
    git push -f -u origin main
    
    print_info "$service_name 拆分完成"
    echo "----------------------------------------"
    
    # 返回原目录
    cd - > /dev/null
}

# 主函数
main() {
    print_info "开始微服务Git仓库拆分..."
    print_info "项目根目录: $(pwd)"
    print_info "备份目录: $BACKUP_DIR"
    print_info "临时目录: $TEMP_DIR"
    
    # 检查配置
    check_config
    
    # 创建备份
    create_backup
    
    # 创建临时目录
    mkdir -p "$TEMP_DIR"
    
    # 拆分每个微服务
    for service_path in "${!MICROSERVICES[@]}"; do
        if [ -d "$service_path" ]; then
            split_microservice "$service_path" "${MICROSERVICES[$service_path]}"
        else
            print_warn "微服务目录不存在: $service_path"
        fi
    done
    
    print_info "所有微服务拆分完成！"
    print_info "备份位置: $BACKUP_DIR"
    print_info "临时目录: $TEMP_DIR"
    print_info "请检查远程仓库确认推送结果"
}

# 显示帮助信息
show_help() {
    echo "微服务Git仓库拆分脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help     显示此帮助信息"
    echo "  -c, --check    仅检查配置，不执行拆分"
    echo ""
    echo "配置说明:"
    echo "  1. 修改脚本开头的 REMOTE_BASE_URL 变量"
    echo "  2. 修改 MICROSERVICES 数组中的远程仓库地址"
    echo "  3. 运行脚本执行拆分"
}

# 参数处理
case "${1:-}" in
    -h|--help)
        show_help
        exit 0
        ;;
    -c|--check)
        check_config
        print_info "配置检查完成"
        exit 0
        ;;
    "")
        main
        ;;
    *)
        print_error "未知参数: $1"
        show_help
        exit 1
        ;;
esac 