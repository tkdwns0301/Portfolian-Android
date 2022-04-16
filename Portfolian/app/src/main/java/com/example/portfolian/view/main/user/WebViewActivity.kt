package com.example.portfolian.view.main.user

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.portfolian.databinding.ActivityWebviewBinding

class WebViewActivity: AppCompatActivity() {
    private lateinit var binding: ActivityWebviewBinding

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        webView = binding.webView

        webView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
        }

        val git = intent.getStringExtra("git")

        Log.e("git", "$git")
        if(git.isNullOrEmpty()) {
            Log.e("webView:", "false")
        } else {
            webView.loadUrl("https://$git")
        }
    }

    override fun onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack()
        }else {
            finish()
        }
    }


}