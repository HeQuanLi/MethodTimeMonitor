# Android 方法耗时统计插件
最新版本：1.0.0

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

### 1. 添加 mavenCentral 仓库

在项目根目录的`settings.gradle.kts`中添加：

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral() // 添加mavenCentral仓库
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral() // 添加mavenCentral仓库
    }
}
```

### 2. 在项目根目录的`build.gradle.kts`中添加插件依赖

```kotlin
buildscript {
    dependencies {
        classpath("io.github.hequanli:method-timer-plugin:1.0.0")
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
    implementation("io.github.hequanli:method-timer-runtime:1.0.0")
    // 其他依赖...
}

// 配置方法耗时统计插件
methodTimer {
    enabled.set(true)           // 启用插件
    mainThreadOnly.set(true)    // 只监控主线程
    minDuration.set(10L)        // 只记录超过10ms的方法
}
```
### 4. 查看统计结果

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