#!/bin/bash

# 构建和发布脚本
set -e

echo "开始构建 Method Timer Plugin..."

# 清理项目
echo "清理项目..."
./gradlew clean

# 构建插件
echo "构建插件..."
./gradlew :method-timer-plugin:build

# 构建运行时库
echo "构建运行时库..."
./gradlew :method-timer-runtime:build

# 运行测试
echo "运行测试..."
./gradlew test

# 发布到本地Maven仓库
echo "发布到本地Maven仓库..."
./gradlew publishToMavenLocal

echo "构建完成！"
echo "你现在可以："
echo "1. 提交代码到GitHub"
echo "2. 创建一个新的Release"
echo "3. 在JitPack.io上构建你的版本"