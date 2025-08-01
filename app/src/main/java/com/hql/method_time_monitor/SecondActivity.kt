package com.hql.method_time_monitor

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)

        simulateLongRunningTaskAAA(2000) // 模拟2秒的耗时操作
    }

    fun simulateLongRunningTaskAAA(durationMillis: Long) {
        println("开始耗时操作...")
        Thread.sleep(durationMillis) // 模拟耗时，暂停线程指定毫秒数
        println("耗时操作完成！")
    }
}