package com.hql.method_time_monitor

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.hql.methodtimer.runtime.MethodTimer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 配置自动打印：当方法数量达到3个时自动打印
        MethodTimer.configAutoPrint(true, 3)

        // 测试方法耗时统计
        testMethodTiming()

        findViewById<TextView>(R.id.material_hour_tv).setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun testMethodTiming() {
        // 模拟一些耗时操作用于测试
        heavyComputation()
        lightComputation()
        Thread.sleep(50) // 模拟耗时操作
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
}