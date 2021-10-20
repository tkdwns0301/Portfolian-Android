package com.example.portfilian.login

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.portfilian.R

class LogInActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
    }

    private fun init() {
        initView()
    }

    private fun initView() {
        initGoogle()
        initKakao()
        initGit()

    }

    private fun initGoogle() {
        val btn_Google = findViewById<ImageButton>(R.id.btn_Google)

        btn_Google.setOnClickListener {
            //TODO 구글 로그인 구현
        }
    }

    private fun initKakao() {
        val btn_Kakao = findViewById<ImageButton>(R.id.btn_Kakao)

        btn_Kakao.setOnClickListener {
            //TODO 카카오 로그인 구현
        }
    }

    private fun initGit() {
        val btn_Git = findViewById<ImageButton>(R.id.btn_Git)

        btn_Git.setOnClickListener {
            //TODO 깃 로그인 구현
        }
    }

    private fun main() {

    }
}