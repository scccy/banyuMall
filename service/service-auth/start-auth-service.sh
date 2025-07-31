#!/bin/bash

# 认证服务启动脚本
# 解决类路径问题

echo "🚀 启动认证服务..."

# 设置Java环境
export JAVA_HOME=/Volumes/opt/lan/java/jdk-21.0.8+9/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

# 检查Java环境
echo "📝 Java版本:"
java -version

# 设置Spring Boot配置
export SPRING_PROFILES_ACTIVE=dev

# 设置JVM参数
export JAVA_OPTS="$JAVA_OPTS \
-Dlog4j2.disable.jmx=true \
-Dlog4j2.skipJansi=true \
-Dlog4j2.statusLogger.level=ERROR \
-Dlog4j2.configurationFactory=org.apache.logging.log4j.core.config.ConfigurationFactory \
-Dlog4j2.disableStatusLogger=true"

echo "📝 使用配置: $SPRING_PROFILES_ACTIVE"
echo "🔧 JVM参数: $JAVA_OPTS"

# 使用Maven启动（推荐方式）
echo "🔧 使用Maven启动服务..."
./mvnw spring-boot:run \
  -Dspring-boot.run.profiles=dev \
  -Dspring-boot.run.jvmArguments="$JAVA_OPTS"

# 如果Maven启动失败，尝试直接运行
if [ $? -ne 0 ]; then
    echo "⚠️  Maven启动失败，尝试直接运行..."
    
    # 编译项目
    echo "🔧 编译项目..."
    ./mvnw clean compile -q
    
    if [ $? -eq 0 ]; then
        echo "✅ 编译成功，启动服务..."
        
        # 构建类路径
        CLASSPATH="target/classes:target/dependency/*"
        
        # 下载依赖到target/dependency
        ./mvnw dependency:copy-dependencies -DoutputDirectory=target/dependency -q
        
        # 运行应用
        java $JAVA_OPTS \
          -cp "$CLASSPATH" \
          -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE \
          com.origin.auth.ServiceAuthApplication
    else
        echo "❌ 编译失败，请检查代码"
        exit 1
    fi
fi 