package com.hql.methodtimer.runtime;

import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 方法耗时统计工具类
 */
public class MethodTimer {
    private static final String TAG = "MethodTimer";
    private static final boolean DEBUG = true;
    
    // 存储方法调用统计信息
    private static final ConcurrentHashMap<String, MethodStats> methodStatsMap = new ConcurrentHashMap<>();
    
    // 自动打印配置
    private static boolean autoPrintEnabled = true;
    private static int autoPrintThreshold = 5; // 当方法数量达到阈值时自动打印
    
    /**
     * 记录方法耗时
     * @param methodName 方法全名
     * @param duration 耗时(毫秒)
     * @param minDuration 最小记录时长(毫秒)
     * @param mainThreadOnly 是否只记录主线程
     */
    public static void recordMethod(String methodName, long duration, long minDuration, boolean mainThreadOnly) {
        // 如果只监控主线程，检查当前是否在主线程
        if (mainThreadOnly && !isMainThread()) {
            return;
        }
        
        if (duration >= minDuration) {
            MethodStats stats = methodStatsMap.get(methodName);
            if (stats == null) {
                stats = new MethodStats();
                MethodStats existing = methodStatsMap.putIfAbsent(methodName, stats);
                if (existing != null) {
                    stats = existing;
                }
            }
            stats.recordCall(duration);
            
            if (DEBUG) {
                Log.d(TAG, String.format("[%s] %dms (avg: %.1fms, count: %d)", 
                    methodName, duration, stats.getAverageDuration(), stats.getCallCount()));
            }
            
            // 检查是否需要自动打印统计信息
            checkAutoPrint();
        }
    }
    
    /**
     * 检查是否需要自动打印统计信息
     */
    private static void checkAutoPrint() {
        if (autoPrintEnabled && methodStatsMap.size() >= autoPrintThreshold) {
            // 只有在方法数量达到阈值时才打印一次
            if (methodStatsMap.size() == autoPrintThreshold) {
                Log.d(TAG, "=== 自动打印统计信息 (方法数量达到" + autoPrintThreshold + ") ===");
                printAllStats();
            }
        }
    }
    
    /**
     * 记录方法耗时 (兼容旧API)
     * @param methodName 方法全名
     * @param duration 耗时(毫秒)
     * @param minDuration 最小记录时长(毫秒)
     */
    public static void recordMethod(String methodName, long duration, long minDuration) {
        recordMethod(methodName, duration, minDuration, false);
    }
    
    /**
     * 检查当前是否在主线程
     * @return true如果在主线程
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
    
    /**
     * 获取方法统计信息
     * @param methodName 方法名
     * @return 统计信息，如果不存在返回null
     */
    public static MethodStats getMethodStats(String methodName) {
        return methodStatsMap.get(methodName);
    }
    
    /**
     * 获取所有方法统计信息
     * @return 所有统计信息的副本
     */
    public static ConcurrentHashMap<String, MethodStats> getAllMethodStats() {
        return new ConcurrentHashMap<>(methodStatsMap);
    }
    
    /**
     * 清空所有统计信息
     */
    public static void clearStats() {
        methodStatsMap.clear();
        Log.d(TAG, "All method stats cleared");
    }
    
    /**
     * 配置自动打印
     * @param enabled 是否启用自动打印
     * @param threshold 自动打印的方法数量阈值
     */
    public static void configAutoPrint(boolean enabled, int threshold) {
        autoPrintEnabled = enabled;
        autoPrintThreshold = threshold;
        Log.d(TAG, "Auto print configured: enabled=" + enabled + ", threshold=" + threshold);
    }
    
    /**
     * 打印所有方法统计信息
     */
    public static void printAllStats() {
        Log.d(TAG, "=== Method Timer Statistics ===");
        
        // 转换为列表并排序
        List<Map.Entry<String, MethodStats>> entries = new ArrayList<>(methodStatsMap.entrySet());
        
        Collections.sort(entries, (e1, e2) -> Long.compare(e2.getValue().getTotalDuration(), e1.getValue().getTotalDuration()));
        
        for (Map.Entry<String, MethodStats> entry : entries) {
            String methodName = entry.getKey();
            MethodStats stats = entry.getValue();
            Log.d(TAG, String.format("%s: avg=%.1fms, max=%dms, min=%dms, total=%dms, count=%d",
                    methodName,
                    stats.getAverageDuration(),
                    stats.getMaxDuration(),
                    stats.getMinDuration(),
                    stats.getTotalDuration(),
                    stats.getCallCount()));
        }
        Log.d(TAG, "=== End of Statistics ===");
    }
    
    /**
     * 方法统计信息类
     */
    public static class MethodStats {
        private final AtomicLong totalDuration = new AtomicLong(0);
        private final AtomicLong callCount = new AtomicLong(0);
        private volatile long maxDuration = 0;
        private volatile long minDuration = Long.MAX_VALUE;
        
        void recordCall(long duration) {
            totalDuration.addAndGet(duration);
            callCount.incrementAndGet();
            
            // 更新最大值
            if (duration > maxDuration) {
                maxDuration = duration;
            }
            
            // 更新最小值
            if (duration < minDuration) {
                minDuration = duration;
            }
        }
        
        public long getTotalDuration() {
            return totalDuration.get();
        }
        
        public long getCallCount() {
            return callCount.get();
        }
        
        public double getAverageDuration() {
            long count = callCount.get();
            return count > 0 ? (double) totalDuration.get() / count : 0.0;
        }
        
        public long getMaxDuration() {
            return maxDuration;
        }
        
        public long getMinDuration() {
            return minDuration == Long.MAX_VALUE ? 0 : minDuration;
        }
    }
}