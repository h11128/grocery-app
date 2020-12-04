package com.jason.grocery.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.jason.grocery.R
import com.jason.grocery.model.SessionManager

class SplashActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({ init() }, 3000L)

    }

    fun init() {
        sessionManager = SessionManager(this)
        val intent = if (sessionManager.isLogin) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        finish()
        startActivity(intent)
    }

}