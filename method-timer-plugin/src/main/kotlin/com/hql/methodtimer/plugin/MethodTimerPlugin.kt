package com.hql.methodtimer.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class MethodTimerPlugin : Plugin<Project> {
    
    override fun apply(project: Project) {
        val extension = project.extensions.create("methodTimer", MethodTimerExtension::class.java)
        
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        
        androidComponents.onVariants { variant ->
            if (extension.enabled.get()) {
                variant.instrumentation.transformClassesWith(
                    MethodTimerClassVisitorFactory::class.java,
                    InstrumentationScope.ALL
                ) { params ->
                    params.enabled.set(extension.enabled)
                    params.mainThreadOnly.set(extension.mainThreadOnly)
                    params.minDuration.set(extension.minDuration)
                }
                
                variant.instrumentation.setAsmFramesComputationMode(
                    FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
                )
            }
        }
    }
}