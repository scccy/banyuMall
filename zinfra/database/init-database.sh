#!/bin/bash

# 数据库初始化脚本
# 作者: scccy
# 创建时间: 2025-01-27

# 配置
DB_NAME="banyu_mall"
DB_USER="root"
DB_HOST="localhost"
DB_PORT="3306"

# 颜色定义
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

# 检查MySQL连接
check_mysql_connection() {
    log_info "检查MySQL连接..."
    
    if command -v mysql &> /dev/null; then
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p -e "SELECT 1;" > /dev/null 2>&1
        if [ $? -eq 0 ]; then
            log_success "MySQL连接正常"
            return 0
        else
            log_error "MySQL连接失败，请检查配置"
            return 1
        fi
    else
        log_error "MySQL客户端未安装"
        return 1
    fi
}

# 创建数据库
create_database() {
    log_info "创建数据库: $DB_NAME"
    
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p -e "
    CREATE DATABASE IF NOT EXISTS \`$DB_NAME\` 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_0900_bin;
    "
    
    if [ $? -eq 0 ]; then
        log_success "数据库创建成功"
    else
        log_error "数据库创建失败"
        return 1
    fi
}

# 执行SQL脚本
execute_sql_script() {
    local script_path="$1"
    local script_name="$2"
    
    log_info "执行SQL脚本: $script_name"
    
    if [ -f "$script_path" ]; then
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p "$DB_NAME" < "$script_path"
        
        if [ $? -eq 0 ]; then
            log_success "$script_name 执行成功"
        else
            log_error "$script_name 执行失败"
            return 1
        fi
    else
        log_error "SQL脚本文件不存在: $script_path"
        return 1
    fi
}

# 验证初始化结果
verify_initialization() {
    log_info "验证数据库初始化结果..."
    
    # 检查用户表
    local user_count=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p -s -N -e "
    SELECT COUNT(*) FROM \`$DB_NAME\`.sys_user;
    ")
    
    if [ "$user_count" -ge 3 ]; then
        log_success "用户表初始化成功，共 $user_count 条记录"
    else
        log_warning "用户表记录数异常: $user_count"
    fi
    
    # 检查任务表
    local task_count=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p -s -N -e "
    SELECT COUNT(*) FROM \`$DB_NAME\`.publisher_task;
    " 2>/dev/null || echo "0")
    
    if [ "$task_count" -ge 0 ]; then
        log_success "任务表初始化成功，共 $task_count 条记录"
    else
        log_warning "任务表记录数异常: $task_count"
    fi
    
    # 检查文件存储表
    local storage_count=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p -s -N -e "
    SELECT COUNT(*) FROM \`$DB_NAME\`.oss_file_storage;
    " 2>/dev/null || echo "0")
    
    if [ "$storage_count" -ge 0 ]; then
        log_success "文件存储表初始化成功，共 $storage_count 条记录"
    else
        log_warning "文件存储表记录数异常: $storage_count"
    fi
}

# 显示帮助信息
show_help() {
    echo "数据库初始化脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  --help, -h          显示帮助信息"
    echo "  --check             仅检查MySQL连接"
    echo "  --create-db         仅创建数据库"
    echo "  --user-only         仅初始化用户模块"
    echo "  --publisher-only    仅初始化发布者模块"
    echo "  --oss-only          仅初始化OSS模块"
    echo ""
    echo "环境变量:"
    echo "  DB_NAME             数据库名称 (默认: banyu_mall)"
    echo "  DB_USER             数据库用户 (默认: root)"
    echo "  DB_HOST             数据库主机 (默认: localhost)"
    echo "  DB_PORT             数据库端口 (默认: 3306)"
    echo ""
    echo "示例:"
    echo "  $0                  执行完整初始化"
    echo "  $0 --check          检查MySQL连接"
    echo "  DB_USER=myuser $0   使用自定义用户初始化"
}

# 主函数
main() {
    log_info "开始数据库初始化..."
    log_info "数据库配置: $DB_USER@$DB_HOST:$DB_PORT/$DB_NAME"
    echo ""
    
    # 检查MySQL连接
    if ! check_mysql_connection; then
        exit 1
    fi
    
    # 创建数据库
    if ! create_database; then
        exit 1
    fi
    
    # 获取脚本目录
    local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    
    # 执行SQL脚本
    if [ "$1" = "--user-only" ]; then
        execute_sql_script "$script_dir/data/user/user-schema.sql" "用户模块"
    elif [ "$1" = "--publisher-only" ]; then
        execute_sql_script "$script_dir/data/publisher/publisher-schema.sql" "发布者模块"
    elif [ "$1" = "--oss-only" ]; then
        execute_sql_script "$script_dir/data/third-party/oss-schema.sql" "OSS模块"
    else
        # 执行所有脚本
        execute_sql_script "$script_dir/data/user/user-schema.sql" "用户模块"
        execute_sql_script "$script_dir/data/publisher/publisher-schema.sql" "发布者模块"
        execute_sql_script "$script_dir/data/third-party/oss-schema.sql" "OSS模块"
    fi
    
    # 验证初始化结果
    verify_initialization
    
    echo ""
    log_success "数据库初始化完成！"
    log_info "详细说明请查看: $script_dir/DATABASE-MODEL-SUMMARY.md"
}

# 脚本入口
case "$1" in
    --help|-h)
        show_help
        exit 0
        ;;
    --check)
        check_mysql_connection
        exit $?
        ;;
    --create-db)
        check_mysql_connection && create_database
        exit $?
        ;;
    --user-only|--publisher-only|--oss-only)
        main "$1"
        ;;
    "")
        main
        ;;
    *)
        log_error "未知选项: $1"
        show_help
        exit 1
        ;;
esac 