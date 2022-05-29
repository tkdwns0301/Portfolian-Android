package com.example.portfolian.view.main.user.setting

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.portfolian.R
import com.example.portfolian.databinding.ActivitySettingBinding
import com.example.portfolian.view.CustomDialog

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    private lateinit var toolbar: Toolbar
    private lateinit var notify: LinearLayout
    private lateinit var logout: TextView
    private lateinit var unlink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        toolbar = binding.toolbarSetting
        notify = binding.llNotify
        logout = binding.tvLogout
        unlink = binding.tvUnlink

        initToolbar()
        initNotifySetting()
        initLogout()
        initUnlink()
    }

    private fun initToolbar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initNotifySetting() {
        notify.setOnClickListener {
            val intent = Intent(this, NotifyActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initLogout() {
        logout.setOnClickListener {
            val customDialog = CustomDialog(this, false)
            customDialog.showDialog()
        }
    }

    private fun initUnlink() {
        unlink.setOnClickListener {
            val customDialog = CustomDialog(this, true)
            customDialog.showDialog()
        }
    }
}