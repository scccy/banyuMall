#!/bin/bash

# 全局日期替换脚本
# 将2025-01-27替换为2025-07-30

echo "开始全局日期替换..."

# 替换.docs目录下的所有文件
find .docs -name "*.md" -type f -exec sed -i '' 's/2025-01-27/2025-07-30/g' {} \;

# 替换归档目录名称
if [ -d ".docs/STATE/archive/2025-01-27" ]; then
    echo "重命名归档目录..."
    mv .docs/STATE/archive/2025-01-27 .docs/STATE/archive/2025-07-30
fi

# 替换规则文档中的日期
find .docs/RULES -name "*.md" -type f -exec sed -i '' 's/2025-01-27/2025-07-30/g' {} \;

# 替换任务文档中的日期
find .docs/TEMP -name "*.md" -type f -exec sed -i '' 's/2025-01-27/2025-07-30/g' {} \;

echo "日期替换完成！"
echo "已将所有2025-01-27替换为2025-07-30"
echo "归档目录已重命名为2025-07-30" 