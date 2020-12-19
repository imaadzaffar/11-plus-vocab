package com.zafaris.elevenplusvocab.ui

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import gr.net.maroulis.library.EasySplashScreen
import com.zafaris.elevenplusvocab.ui.main.MainActivity
import com.zafaris.elevenplusvocab.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.statusBarColor = getColor(R.color.colorBlueStatus)
        supportActionBar?.hide()

        Handler().postDelayed({
            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)

    }
}