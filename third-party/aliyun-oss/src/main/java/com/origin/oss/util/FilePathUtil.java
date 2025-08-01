package com.origin.oss.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 文件路径工具类
 */
public class FilePathUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    
    /**
     * 生成文件路径
     * 格式：{sourceService}/{businessType}/{yyyy/MM/dd}/
     */
    public static String generateFilePath(String sourceService, String businessType) {
        String datePath = LocalDateTime.now().format(DATE_FORMATTER);
        return String.format("%s/%s/%s/", sourceService, businessType, datePath);
    }
    
    /**
     * 生成对象键
     * 格式：{filePath}{timestamp}_{originalName}
     */
    public static String generateObjectKey(String filePath, String originalName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return filePath + timestamp + "_" + originalName;
    }
    
    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
} 