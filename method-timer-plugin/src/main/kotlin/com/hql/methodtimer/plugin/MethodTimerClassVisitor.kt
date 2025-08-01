package com.hql.methodtimer.plugin

import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class MethodTimerClassVisitor(
    nextClassVisitor: ClassVisitor,
    private val classData: ClassData,
    private val parameters: MethodTimerClassVisitorFactory.Parameters
) : ClassVisitor(Opcodes.ASM9, nextClassVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        
        // 跳过构造函数、静态块和abstract方法
        if (name == "<init>" || name == "<clinit>" || (access and Opcodes.ACC_ABSTRACT) != 0) {
            return methodVisitor
        }
        
        return MethodTimerMethodVisitor(
            methodVisitor,
            access,
            name ?: "unknown",
            descriptor ?: "()V",
            classData.className,
            parameters
        )
    }
}

class MethodTimerMethodVisitor(
    methodVisitor: MethodVisitor,
    access: Int,
    private val methodName: String,
    descriptor: String,
    private val className: String,
    private val parameters: MethodTimerClassVisitorFactory.Parameters
) : AdviceAdapter(ASM9, methodVisitor, access, methodName, descriptor) {

    private var startTimeVar = -1

    override fun onMethodEnter() {
        super.onMethodEnter()
        
        // 直接记录开始时间，主线程检查移到recordMethod内部
        recordStartTime()
    }

    private fun recordStartTime() {
        // 调用 System.nanoTime() 获取开始时间
        mv.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/System",
            "nanoTime",
            "()J",
            false
        )
        
        // 将开始时间存储到局部变量中
        startTimeVar = newLocal(Type.LONG_TYPE)
        mv.visitVarInsn(LSTORE, startTimeVar)
    }

    override fun onMethodExit(opcode: Int) {
        if (startTimeVar != -1) {
            // 获取结束时间
            mv.visitMethodInsn(
                INVOKESTATIC,
                "java/lang/System",
                "nanoTime",
                "()J",
                false
            )
            
            // 加载开始时间
            mv.visitVarInsn(LLOAD, startTimeVar)
            
            // 计算耗时 (结束时间 - 开始时间)
            mv.visitInsn(LSUB)
            
            // 转换为毫秒 (纳秒 / 1000000)
            mv.visitLdcInsn(1000000L)
            mv.visitInsn(LDIV)
            
            // 将耗时存储到临时变量
            val durationVar = newLocal(Type.LONG_TYPE)
            mv.visitVarInsn(LSTORE, durationVar)
            
            // 准备参数调用新的recordMethod方法
            mv.visitLdcInsn("$className.$methodName") // 方法全名
            mv.visitVarInsn(LLOAD, durationVar) // 耗时
            mv.visitLdcInsn(parameters.minDuration.get()) // 最小记录时长
            mv.visitInsn(if (parameters.mainThreadOnly.get()) ICONST_1 else ICONST_0) // mainThreadOnly
            
            // 调用新的recordMethod方法
            mv.visitMethodInsn(
                INVOKESTATIC,
                "com/hql/methodtimer/runtime/MethodTimer",
                "recordMethod",
                "(Ljava/lang/String;JJZ)V",
                false
            )
        }
        
        super.onMethodExit(opcode)
    }
}