package com.example.portfolian.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.portfolian.R

class NicknameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname)

        init()
    }

    private fun init() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<NicknameFragment>(R.id.fragment)
        }
    }
}