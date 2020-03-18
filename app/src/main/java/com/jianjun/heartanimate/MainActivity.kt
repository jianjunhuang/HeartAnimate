package com.jianjun.heartanimate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<HeartView>(R.id.heart_view).setOnClickListener {
            (it as HeartView).startAnimate()
        }
    }
}
