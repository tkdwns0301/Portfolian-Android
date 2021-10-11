package com.example.portfolian.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.portfolian.R

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
    }

    private fun init() {
        initView()
    }

    private fun initView() {
        val btnGit = findViewById<AppCompatButton>(R.id.btnGit)

        btnGit.setOnClickListener {
            nickname()
        }
    }

    private fun nickname() {
        val intent = Intent(this, NicknameActivity::class.java)
        startActivity(intent)
        finish()
    }
}