package com.hql.methodtimer.plugin

import org.gradle.api.provider.Property

abstract class MethodTimerExtension {
    abstract val enabled: Property<Boolean>
    abstract val mainThreadOnly: Property<Boolean>
    abstract val minDuration: Property<Long>
    
    init {
        enabled.convention(true)
        mainThreadOnly.convention(true)
        minDuration.convention(10L) // 默认只记录超过10ms的方法
    }
}