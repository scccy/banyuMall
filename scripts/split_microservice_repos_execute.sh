#!/bin/bash

# 微服务Git仓库拆分执行脚本
# 作者: scccy
# 用途: 将当前全局git仓库拆分为独立的微服务仓库
# 生成时间: 2025-01-27

set -e  # 遇到错误立即退出

# 配置变量
REMOTE_BASE_URL="git@117.50.221.113:banyu"
BACKUP_DIR="../banyuMall-backup-$(date +%Y%m%d_%H%M%S)"
TEMP_DIR="../temp-microservices-$(date +%Y%m%d_%H%M%S)"

# 微服务列表及其远程仓库地址 (SSH方式)
MICROSERVICES=(
    "service/service-common:git@117.50.221.113:banyu/service-common.git"
    "service/service-base:git@117.50.221.113:banyu/service-base.git"
    "service/service-auth:git@117.50.221.113:banyu/service-auth.git"
    "service/service-gateway:git@117.50.221.113:banyu/service-gateway.git"
    "service/service-user:git@117.50.221.113:banyu/service-user.git"
    "core/core-publisher:git@117.50.221.113:banyu/core-publisher.git"
    "third-party/third-party-aliyunOss:git@117.50.221.113:banyu/aliyun-oss.git"
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

print_success() {
    echo -e "\033[36m[SUCCESS]\033[0m $1"
}

# 检查配置
check_config() {
    print_info "检查配置..."
    
    if [ -z "$REMOTE_BASE_URL" ]; then
        print_error "请设置 REMOTE_BASE_URL 变量"
        exit 1
    fi
    
    # 检查是否有未配置的微服务
    for service_item in "${MICROSERVICES[@]}"; do
        service_path=$(echo "$service_item" | cut -d':' -f1)
        remote_url=$(echo "$service_item" | cut -d':' -f2)
        if [ -z "$remote_url" ]; then
            print_error "微服务 $service_path 的远程仓库地址未配置"
            exit 1
        fi
    done
    
    print_success "配置检查通过"
}

# 创建备份
create_backup() {
    print_info "创建项目备份到: $BACKUP_DIR"
    mkdir -p "$BACKUP_DIR"
    cp -r . "$BACKUP_DIR/"
    print_success "备份完成: $BACKUP_DIR"
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
    
    # 复制 .gitignore 文件
    if [ -f ".gitignore" ]; then
        cp ".gitignore" "$temp_service_dir/"
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
- 作者: scccy
- 原始路径: $service_path"
    
    # 添加远程仓库
    print_info "添加远程仓库: $remote_url"
    git remote add origin "$remote_url"
    
    # 验证远程仓库连接
    print_info "验证远程仓库连接..."
    if git ls-remote origin > /dev/null 2>&1; then
        print_success "远程仓库连接成功"
    else
        print_error "远程仓库连接失败: $remote_url"
        return 1
    fi
    
    # 推送到远程仓库
    print_info "推送到远程仓库"
    git push -f -u origin main
    
    print_success "$service_name 拆分完成"
    echo "----------------------------------------"
    
    # 返回原目录
    cd - > /dev/null
}

# 显示拆分计划
show_plan() {
    print_info "微服务拆分计划:"
    echo ""
    for service_item in "${MICROSERVICES[@]}"; do
        service_path=$(echo "$service_item" | cut -d':' -f1)
        remote_url=$(echo "$service_item" | cut -d':' -f2)
        local service_name=$(basename "$service_path")
        echo "  📦 $service_name"
        echo "     路径: $service_path"
        echo "     远程: $remote_url"
        echo ""
    done
    
    echo "备份目录: $BACKUP_DIR"
    echo "临时目录: $TEMP_DIR"
    echo ""
}

# 主函数
main() {
    print_info "开始微服务Git仓库拆分..."
    print_info "项目根目录: $(pwd)"
    
    # 显示拆分计划
    show_plan
    
    # 检查配置
    check_config
    
    # 创建备份
    create_backup
    
    # 创建临时目录
    mkdir -p "$TEMP_DIR"
    
    # 拆分每个微服务
    local success_count=0
    local total_count=0
    
    for service_item in "${MICROSERVICES[@]}"; do
        service_path=$(echo "$service_item" | cut -d':' -f1)
        remote_url=$(echo "$service_item" | cut -d':' -f2)
        total_count=$((total_count + 1))
        if [ -d "$service_path" ]; then
            if split_microservice "$service_path" "$remote_url"; then
                success_count=$((success_count + 1))
            fi
        else
            print_warn "微服务目录不存在: $service_path"
        fi
    done
    
    print_success "微服务拆分完成！"
    print_info "成功拆分: $success_count/$total_count"
    print_info "备份位置: $BACKUP_DIR"
    print_info "临时目录: $TEMP_DIR"
    print_info "请检查远程仓库确认推送结果"
    
    # 显示后续步骤
    echo ""
    print_info "后续步骤:"
    echo "1. 检查各远程仓库的代码推送情况"
    echo "2. 删除当前全局仓库的 .git 目录"
    echo "3. 为每个微服务创建独立的开发目录"
    echo "4. 更新项目文档和README"
}

# 显示帮助信息
show_help() {
    echo "微服务Git仓库拆分执行脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help     显示此帮助信息"
    echo "  -c, --check    仅检查配置，不执行拆分"
    echo "  -p, --plan     显示拆分计划，不执行拆分"
    echo ""
    echo "配置说明:"
    echo "  此脚本已配置好所有微服务的远程仓库地址"
    echo "  如需修改，请编辑脚本开头的 MICROSERVICES 数组"
}

# 参数处理
case "${1:-}" in
    -h|--help)
        show_help
        exit 0
        ;;
    -c|--check)
        check_config
        print_success "配置检查完成"
        exit 0
        ;;
    -p|--plan)
        show_plan
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