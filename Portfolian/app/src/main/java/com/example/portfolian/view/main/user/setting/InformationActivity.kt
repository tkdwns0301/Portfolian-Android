package com.example.portfolian.view.main.user.setting

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.example.portfolian.databinding.ActivityInformationBinding

class InformationActivity: AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var binding: ActivityInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webView

        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.domStorageEnabled = true

        webView.loadUrl("https://amused-sing-8d6.notion.site/881828b07adf45209ff70770180731d8")
    }

}