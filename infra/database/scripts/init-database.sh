#!/bin/bash

# 数据库初始化脚本
# 用于开发环境快速初始化数据库

set -e

# 配置变量
DB_NAME="banyu_mall"
DB_USER="root"
DB_HOST="localhost"
DB_PORT="3306"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$(dirname "$(dirname "$SCRIPT_DIR")")")"

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

# 检查MySQL连接
check_mysql_connection() {
    log_info "检查MySQL连接..."
    
    if ! mysql -u"$DB_USER" -h"$DB_HOST" -P"$DB_PORT" -e "SELECT 1;" >/dev/null 2>&1; then
        log_error "无法连接到MySQL数据库"
        log_error "请确保MySQL服务正在运行，并且用户 $DB_USER 有足够的权限"
        exit 1
    fi
    
    log_info "MySQL连接正常"
}

# 创建数据库
create_database() {
    log_info "创建数据库 $DB_NAME..."
    
    if mysql -u"$DB_USER" -h"$DB_HOST" -P"$DB_PORT" -e "CREATE DATABASE IF NOT EXISTS \`$DB_NAME\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null; then
        log_info "数据库 $DB_NAME 创建成功或已存在"
    else
        log_error "创建数据库失败"
        exit 1
    fi
}

# 执行SQL文件
execute_sql_file() {
    local sql_file="$1"
    local description="$2"
    
    if [ ! -f "$sql_file" ]; then
        log_error "SQL文件不存在: $sql_file"
        exit 1
    fi
    
    log_info "执行 $description..."
    
    if mysql -u"$DB_USER" -h"$DB_HOST" -P"$DB_PORT" "$DB_NAME" < "$sql_file"; then
        log_info "$description 执行成功"
    else
        log_error "$description 执行失败"
        exit 1
    fi
}

# 验证初始化结果
verify_initialization() {
    log_info "验证初始化结果..."
    
    # 检查表是否存在
    local tables=("sys_user" "sys_role" "sys_user_role" "sys_permission" "sys_role_permission" "user_profile" "user_config")
    
    for table in "${tables[@]}"; do
        if mysql -u"$DB_USER" -h"$DB_HOST" -P"$DB_PORT" "$DB_NAME" -e "DESCRIBE \`$table\`;" >/dev/null 2>&1; then
            log_info "表 $table 创建成功"
        else
            log_error "表 $table 创建失败"
            exit 1
        fi
    done
    
    # 检查初始数据
    local user_count=$(mysql -u"$DB_USER" -h"$DB_HOST" -P"$DB_PORT" "$DB_NAME" -s -N -e "SELECT COUNT(*) FROM sys_user;")
    local role_count=$(mysql -u"$DB_USER" -h"$DB_HOST" -P"$DB_PORT" "$DB_NAME" -s -N -e "SELECT COUNT(*) FROM sys_role;")
    
    log_info "初始化数据统计:"
    log_info "  - 用户数量: $user_count"
    log_info "  - 角色数量: $role_count"
}

# 显示使用说明
show_usage() {
    echo "数据库初始化脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --host HOST     数据库主机 (默认: localhost)"
    echo "  -P, --port PORT     数据库端口 (默认: 3306)"
    echo "  -u, --user USER     数据库用户 (默认: root)"
    echo "  -p, --password      提示输入密码"
    echo "  -d, --database NAME 数据库名称 (默认: banyu_mall)"
    echo "  --help              显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0                    # 使用默认配置"
    echo "  $0 -h 192.168.1.100   # 指定数据库主机"
    echo "  $0 -u myuser -p       # 指定用户并提示输入密码"
}

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--host)
            DB_HOST="$2"
            shift 2
            ;;
        -P|--port)
            DB_PORT="$2"
            shift 2
            ;;
        -u|--user)
            DB_USER="$2"
            shift 2
            ;;
        -p|--password)
            echo -n "请输入数据库密码: "
            read -s DB_PASSWORD
            echo
            export MYSQL_PWD="$DB_PASSWORD"
            shift
            ;;
        -d|--database)
            DB_NAME="$2"
            shift 2
            ;;
        --help)
            show_usage
            exit 0
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
    log_info "开始初始化数据库..."
    log_info "数据库配置:"
    log_info "  - 主机: $DB_HOST:$DB_PORT"
    log_info "  - 用户: $DB_USER"
    log_info "  - 数据库: $DB_NAME"
    echo
    
    # 检查MySQL连接
    check_mysql_connection
    
    # 创建数据库
    create_database
    
    # 执行表结构创建
    execute_sql_file "$PROJECT_ROOT/infra/database/schema/unified-user-schema.sql" "表结构创建"
    
    # 执行初始化数据
    execute_sql_file "$PROJECT_ROOT/infra/database/data/unified-init-data.sql" "初始化数据"
    
    # 验证初始化结果
    verify_initialization
    
    log_info "数据库初始化完成！"
    log_info "默认管理员账户:"
    log_info "  - 用户名: admin"
    log_info "  - 密码: 123456"
}

# 执行主函数
main "$@" 