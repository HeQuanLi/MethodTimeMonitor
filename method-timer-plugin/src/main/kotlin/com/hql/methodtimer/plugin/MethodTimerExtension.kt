package com.hql.methodtimer.plugin

import org.gradle.api.provider.Property

abstract class MethodTimerExtension {
    abstract val enabled: Property<Boolean>
    abstract val mainThreadOnly: Property<Boolean>
    abstract val minDuration: Property<Long>
    /**
     * 是否对所有依赖（包括三方库）进行插桩。
     * true  = InstrumentationScope.ALL（插桩项目 + 所有依赖库）
     * false = InstrumentationScope.PROJECT（仅插桩项目自身代码，推荐）
     */
    abstract val instrumentationScopeAll: Property<Boolean>

    init {
        enabled.convention(true)
        mainThreadOnly.convention(true)
        minDuration.convention(10L)
        instrumentationScopeAll.convention(false)
    }
}