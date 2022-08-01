package com.hand.portfolian.view.main.user

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hand.portfolian.databinding.ActivityWebviewBinding

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

        if(git.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "깃 주소가 등록되지 않은 사용자입니다.", Toast.LENGTH_SHORT).show()
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