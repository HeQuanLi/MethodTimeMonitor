# Android 方法耗时统计插件

[![](https://jitpack.io/v/yourusername/method-timer-plugin.svg)](https://jitpack.io/#yourusername/method-timer-plugin)
[![Build Status](https://github.com/yourusername/method-timer-plugin/workflows/Build%20and%20Test/badge.svg)](https://github.com/yourusername/method-timer-plugin/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

这是一个使用ASM字节码插桩技术的Android Gradle插件，用于统计主线程中的方法耗时，适配AGP 8.0以上版本。

## 功能特性

- ✅ 使用ASM字节码插桩，无侵入性
- ✅ 支持主线程方法耗时统计
- ✅ 可配置最小记录时长
- ✅ 实时统计和日志输出
- ✅ 适配AGP 8.0+版本
- ✅ 支持方法调用次数、平均耗时、最大/最小耗时统计
- ✅ 自动打印功能，无需手动调用

## 快速开始

### 1. 添加JitPack仓库

在项目根目录的`settings.gradle.kts`中添加：

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") } // 添加JitPack仓库
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // 添加JitPack仓库
    }
}
```

### 2. 在项目根目录的`build.gradle.kts`中添加插件依赖

```kotlin
buildscript {
    dependencies {
        classpath("com.github.yourusername:method-timer-plugin:1.0.0")
    }
}
```

### 3. 应用插件

在app模块的`build.gradle.kts`中：

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.methodtimer.plugin")
}

dependencies {
    implementation("com.github.yourusername:method-timer-runtime:1.0.0")
    // 其他依赖...
}

// 配置方法耗时统计插件
methodTimer {
    enabled.set(true)           // 启用插件
    mainThreadOnly.set(true)    // 只监控主线程
    minDuration.set(10L)        // 只记录超过10ms的方法
}
```

### 4. 在代码中使用

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 配置自动打印：当方法数量达到3个时自动打印
        MethodTimer.configAutoPrint(true, 3)
        
        // 测试方法耗时统计
        testMethodTiming()
        simulateLongRunningTask(2000)
    }
    
    override fun onResume() {
        super.onResume()
        // 页面显示时也打印一次统计信息
        MethodTimer.printAllStats()
    }
    
    private fun testMethodTiming() {
        heavyComputation()
        lightComputation()
    }
    
    private fun heavyComputation() {
        // 模拟重计算
        var result = 0
        for (i in 0..1000000) {
            result += i
        }
    }
    
    private fun lightComputation() {
        // 轻量计算
        val result = 2 + 2
    }
    
    fun simulateLongRunningTask(durationMillis: Long) {
        Thread.sleep(durationMillis) // 模拟耗时操作
    }
}
```

### 5. 查看统计结果

插件现在支持自动打印和手动打印两种方式：

**自动打印模式**：
```
D/MethodTimer: Auto print configured: enabled=true, threshold=3
D/MethodTimer: [com.example.myapplication.MainActivity.heavyComputation] 25ms (avg: 25.0ms, count: 1)
D/MethodTimer: [com.example.myapplication.MainActivity.testMethodTiming] 28ms (avg: 28.0ms, count: 1)
D/MethodTimer: [com.example.myapplication.MainActivity.simulateLongRunningTask] 2003ms (avg: 2003.0ms, count: 1)
D/MethodTimer: === 自动打印统计信息 (方法数量达到3) ===
D/MethodTimer: === Method Timer Statistics ===
D/MethodTimer: com.example.myapplication.MainActivity.simulateLongRunningTask: avg=2003.0ms, max=2003ms, min=2003ms, total=2003ms, count=1
D/MethodTimer: com.example.myapplication.MainActivity.testMethodTiming: avg=28.0ms, max=28ms, min=28ms, total=28ms, count=1
D/MethodTimer: com.example.myapplication.MainActivity.heavyComputation: avg=25.0ms, max=25ms, min=25ms, total=25ms, count=1
D/MethodTimer: === End of Statistics ===
```

**页面显示时打印**：
```
D/MethodTimer: === Method Timer Statistics ===
D/MethodTimer: com.example.myapplication.MainActivity.simulateLongRunningTask: avg=2003.0ms, max=2003ms, min=2003ms, total=2003ms, count=1
D/MethodTimer: com.example.myapplication.MainActivity.testMethodTiming: avg=28.0ms, max=28ms, min=28ms, total=28ms, count=1
D/MethodTimer: com.example.myapplication.MainActivity.heavyComputation: avg=25.0ms, max=25ms, min=25ms, total=25ms, count=1
D/MethodTimer: === End of Statistics ===
```

## API说明

### MethodTimer类

```java
// 配置自动打印功能
public static void configAutoPrint(boolean enabled, int threshold)

// 检查当前是否在主线程
public static boolean isMainThread()

// 获取方法统计信息
public static MethodStats getMethodStats(String methodName)

// 获取所有方法统计信息
public static ConcurrentHashMap<String, MethodStats> getAllMethodStats()

// 清空所有统计信息
public static void clearStats()

// 手动打印所有方法统计信息到Logcat
public static void printAllStats()
```

### MethodStats类

```java
// 获取总耗时
public long getTotalDuration()

// 获取调用次数
public long getCallCount()

// 获取平均耗时
public double getAverageDuration()

// 获取最大耗时
public long getMaxDuration()

// 获取最小耗时
public long getMinDuration()
```

## 插件配置选项

```kotlin
methodTimer {
    enabled.set(true)           // 是否启用插件，默认true
    mainThreadOnly.set(true)    // 是否只监控主线程，默认true
    minDuration.set(10L)        // 最小记录时长(毫秒)，默认10ms
}
```

## 技术实现

1. **字节码插桩**: 使用ASM在编译时对方法进行插桩
2. **AGP 8.0+适配**: 使用新的`AsmClassVisitorFactory`和`AndroidComponentsExtension`
3. **主线程检测**: 通过`Looper.myLooper() == Looper.getMainLooper()`检查
4. **性能统计**: 使用`System.nanoTime()`进行高精度计时
5. **线程安全**: 使用`ConcurrentHashMap`和`AtomicLong`保证线程安全

## 过滤规则

插件会自动过滤以下类型的方法，避免影响系统性能：

- Android系统类 (`android.*`)
- AndroidX类 (`androidx.*`)
- Java标准库类 (`java.*`)
- Kotlin标准库类 (`kotlin.*`)
- 插件自身类 (`com.methodtimer.*`)
- R资源类
- 构造函数和静态初始化块
- 抽象方法

## 发布到JitPack

如果你想发布自己的版本，请按照以下步骤：

### 1. Fork项目并修改配置

1. Fork这个项目到你的GitHub账户
2. 将所有配置文件中的`yourusername`替换为你的GitHub用户名
3. 提交更改到你的仓库

### 2. 创建Release

1. 在GitHub仓库页面点击"Releases"
2. 点击"Create a new release"
3. 创建一个新的tag，例如`v1.0.0`
4. 填写Release说明
5. 发布Release

### 3. 在JitPack上构建

1. 访问 [JitPack.io](https://jitpack.io)
2. 输入你的GitHub仓库地址：`https://github.com/yourusername/method-timer-plugin`
3. 点击"Look up"
4. 选择你创建的版本号，点击"Get it"
5. 等待构建完成

### 4. 使用你发布的版本

将README中的依赖改为：
```kotlin
classpath("com.github.yourusername:method-timer-plugin:v1.0.0")
implementation("com.github.yourusername:method-timer-runtime:v1.0.0")
```

## 本地开发和测试

### 构建说明

```bash
# 清理项目
./gradlew clean

# 构建插件
./gradlew :method-timer-plugin:build

# 构建运行时库
./gradlew :method-timer-runtime:build

# 构建示例应用
./gradlew :app:assembleDebug

# 发布到本地Maven仓库（用于本地测试）
./gradlew publishToMavenLocal
```

## 注意事项

1. 插件会增加方法的执行时间（约1-2μs），但对性能影响很小
2. 建议只在debug版本中启用，release版本关闭
3. 可以通过`minDuration`参数过滤短时间方法，减少日志输出
4. 统计数据存储在内存中，应用重启后会清空

## 版本要求

- Android Gradle Plugin: 8.0+
- Gradle: 7.0+
- JDK: 17+
- Kotlin: 2.0+