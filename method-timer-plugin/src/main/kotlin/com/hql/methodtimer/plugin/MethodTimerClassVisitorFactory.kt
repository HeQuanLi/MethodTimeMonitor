package com.hql.methodtimer.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor

abstract class MethodTimerClassVisitorFactory : AsmClassVisitorFactory<MethodTimerClassVisitorFactory.Parameters> {

    interface Parameters : InstrumentationParameters {
        @get:Input
        val enabled: Property<Boolean>
        
        @get:Input
        val mainThreadOnly: Property<Boolean>
        
        @get:Input
        val minDuration: Property<Long>
    }

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return MethodTimerClassVisitor(
            nextClassVisitor,
            classContext.currentClassData,
            parameters.get()
        )
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        // 过滤掉不需要插桩的类
        return !classData.className.startsWith("android.") &&
                !classData.className.startsWith("androidx.") &&
                !classData.className.startsWith("java.") &&
                !classData.className.startsWith("kotlin.") &&
                !classData.className.startsWith("com.hql.methodtimer.") &&
                !classData.className.contains("R$") &&
                !classData.className.endsWith("R")
    }
}