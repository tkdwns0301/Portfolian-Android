package com.example.portfolian.view.main.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.portfolian.R

class SettingActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        initView()
    }

    private fun initView() {
        initToolbar()
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar_Setting)

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }


}