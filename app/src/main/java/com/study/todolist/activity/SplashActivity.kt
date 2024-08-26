package com.study.todolist.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.study.todolist.MainActivity
import com.study.todolist.R

class SplashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed(Runnable { // Handler 함수 사용, SplashActivity의 딜레이 시간 지정
            startActivity(Intent(this, MainActivity::class.java)) // 딜레이 후 이동할 Activity 지정 (MainActivity)
            finish() // 현재 Activity 종료, Splash는 이동한 다음 더 이상 사용되지 않음
        }, 2000) // 2초 딜레이
    }
}