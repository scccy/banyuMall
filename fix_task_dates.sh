#!/bin/bash
# 任务文件名时间戳修正脚本
# 将所有任务文件名中的错误时间戳替换为正确的2025-07-30

echo "开始修正任务文件名中的时间戳..."

# 获取当前时间作为任务执行时间
CURRENT_DATE=$(date +"%Y%m%d")
CURRENT_TIME=$(date +"%H%M")

echo "当前日期: $CURRENT_DATE"
echo "当前时间: $CURRENT_TIME"

# 进入归档目录
cd .docs/TEMP/archive/2025-07-30

# 1. 重命名所有任务文件，将错误的时间戳替换为正确的
echo "重命名任务文件..."

# 处理20250127开头的文件
for file in task_20250127_*.md; do
    if [ -f "$file" ]; then
        # 提取时间部分（HHMMSS）
        time_part=$(echo "$file" | sed 's/task_20250127_\([0-9]*\)_.*\.md/\1/')
        # 生成新文件名
        new_name="task_${CURRENT_DATE}_${time_part}_$(echo "$file" | sed 's/task_20250127_[0-9]*_//')"
        echo "重命名: $file -> $new_name"
        mv "$file" "$new_name"
    fi
done

# 处理20241219开头的文件
for file in task_20241219_*.md; do
    if [ -f "$file" ]; then
        # 提取描述部分
        desc_part=$(echo "$file" | sed 's/task_20241219_\(.*\)\.md/\1/')
        # 生成新文件名
        new_name="task_${CURRENT_DATE}_${CURRENT_TIME}_${desc_part}.md"
        echo "重命名: $file -> $new_name"
        mv "$file" "$new_name"
    fi
done

# 2. 更新TASK-EXECUTION-SUMMARY.md中的引用
echo "更新TASK-EXECUTION-SUMMARY.md中的文件引用..."

cd ../../..

# 替换所有任务ID引用
sed -i '' "s/task_20250127_/task_${CURRENT_DATE}_/g" .docs/TEMP/TASK-EXECUTION-SUMMARY.md
sed -i '' "s/task_20241219_/task_${CURRENT_DATE}_/g" .docs/TEMP/TASK-EXECUTION-SUMMARY.md

# 3. 更新归档目录README.md中的文件列表
echo "更新归档目录README.md..."

cd .docs/TEMP/archive/2025-07-30

# 重新生成文件列表
echo "# 2025-07-30 归档任务文件" > README.md
echo "" >> README.md
echo "**归档时间**: 2025-07-30" >> README.md
echo "**文件数量**: $(ls task_*.md | wc -l)" >> README.md
echo "" >> README.md
echo "## 任务文件列表" >> README.md
echo "" >> README.md

# 按时间排序并列出所有任务文件
ls task_*.md | sort | while read file; do
    echo "- \`$file\`" >> README.md
done

echo "" >> README.md
echo "## 提取说明" >> README.md
echo "" >> README.md
echo "如需查看原始文件内容，请使用以下命令：" >> README.md
echo "\`\`\`bash" >> README.md
echo "tar -xzf archive-2025-07-30.tar.gz" >> README.md
echo "\`\`\`" >> README.md

cd ../../..

echo "时间戳修正完成！"
echo "已将所有任务文件名中的错误时间戳替换为: ${CURRENT_DATE}"
echo "已更新TASK-EXECUTION-SUMMARY.md中的文件引用"
echo "已重新生成归档目录README.md" 