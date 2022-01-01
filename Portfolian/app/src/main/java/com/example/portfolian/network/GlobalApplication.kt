package com.example.portfolian.network

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "8ab6cd6b8425d701ee369a5b461275a8")
    }
}