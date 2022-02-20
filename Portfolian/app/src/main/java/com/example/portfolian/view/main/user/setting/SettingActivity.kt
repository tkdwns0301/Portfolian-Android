package com.example.portfolian.view.main.user.setting

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.portfolian.R

class SettingActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var notify: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        init()
    }

    private fun init() {
        initToolbar()
        initNotifySetting()
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar_Setting)

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initNotifySetting() {
        notify = findViewById(R.id.ll_Notify)

        notify.setOnClickListener {
            val intent = Intent(this, NotifyActivity::class.java)
            startActivity(intent)
        }
    }
}