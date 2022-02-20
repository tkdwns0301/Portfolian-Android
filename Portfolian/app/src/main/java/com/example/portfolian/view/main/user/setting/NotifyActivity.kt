package com.example.portfolian.view.main.user.setting

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.portfolian.R

class NotifyActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify)

        init()
    }

    private fun init() {
        initToolbar()
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar_NotifySetting)

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}