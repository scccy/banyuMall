#!/bin/bash

# 数据库备份脚本
# 用于备份BanyuMall项目数据库

set -e

# 配置变量
DB_NAME="banyu_mall"
DB_USER="root"
DB_HOST="localhost"
DB_PORT="3306"
BACKUP_DIR="./backups"
BACKUP_PREFIX="banyu_mall_backup"

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

# 生成备份文件名
generate_backup_filename() {
    local timestamp=$(date +"%Y%m%d_%H%M%S")
    echo "${BACKUP_PREFIX}_${timestamp}.sql"
}

# 创建备份目录
create_backup_dir() {
    if [ ! -d "$BACKUP_DIR" ]; then
        log_info "创建备份目录: $BACKUP_DIR"
        mkdir -p "$BACKUP_DIR"
    fi
}

# 执行备份
perform_backup() {
    local backup_file="$1"
    
    log_info "开始备份数据库 $DB_NAME..."
    
    if mysqldump -u"$DB_USER" -h"$DB_HOST" -P"$DB_PORT" \
        --single-transaction \
        --routines \
        --triggers \
        --events \
        --add-drop-database \
        --databases "$DB_NAME" > "$backup_file"; then
        log_info "数据库备份成功: $backup_file"
        
        # 显示备份文件信息
        local file_size=$(du -h "$backup_file" | cut -f1)
        log_info "备份文件大小: $file_size"
    else
        log_error "数据库备份失败"
        rm -f "$backup_file"
        exit 1
    fi
}

# 清理旧备份
cleanup_old_backups() {
    local max_backups=10
    local backup_count=$(ls -1 "$BACKUP_DIR"/${BACKUP_PREFIX}_*.sql 2>/dev/null | wc -l)
    
    if [ "$backup_count" -gt "$max_backups" ]; then
        log_info "清理旧备份文件（保留最新的 $max_backups 个）..."
        
        # 按修改时间排序，删除最旧的文件
        ls -t "$BACKUP_DIR"/${BACKUP_PREFIX}_*.sql | tail -n +$((max_backups + 1)) | xargs rm -f
        
        log_info "旧备份文件清理完成"
    fi
}

# 显示使用说明
show_usage() {
    echo "数据库备份脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --host HOST     数据库主机 (默认: localhost)"
    echo "  -P, --port PORT     数据库端口 (默认: 3306)"
    echo "  -u, --user USER     数据库用户 (默认: root)"
    echo "  -p, --password      提示输入密码"
    echo "  -d, --database NAME 数据库名称 (默认: banyu_mall)"
    echo "  -o, --output DIR    备份目录 (默认: ./backups)"
    echo "  --cleanup           清理旧备份文件"
    echo "  --help              显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0                    # 使用默认配置备份"
    echo "  $0 -h 192.168.1.100   # 指定数据库主机"
    echo "  $0 -u myuser -p       # 指定用户并提示输入密码"
    echo "  $0 --cleanup          # 清理旧备份文件"
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
        -o|--output)
            BACKUP_DIR="$2"
            shift 2
            ;;
        --cleanup)
            create_backup_dir
            cleanup_old_backups
            exit 0
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
    log_info "开始数据库备份..."
    log_info "数据库配置:"
    log_info "  - 主机: $DB_HOST:$DB_PORT"
    log_info "  - 用户: $DB_USER"
    log_info "  - 数据库: $DB_NAME"
    log_info "  - 备份目录: $BACKUP_DIR"
    echo
    
    # 创建备份目录
    create_backup_dir
    
    # 生成备份文件名
    local backup_file="$BACKUP_DIR/$(generate_backup_filename)"
    
    # 执行备份
    perform_backup "$backup_file"
    
    # 清理旧备份
    cleanup_old_backups
    
    log_info "数据库备份完成！"
    log_info "备份文件: $backup_file"
}

# 执行主函数
main "$@" 