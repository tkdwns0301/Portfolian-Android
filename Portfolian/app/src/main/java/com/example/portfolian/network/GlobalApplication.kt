package com.example.portfolian.network

import android.app.Application
import com.example.portfolian.MySharedPreferences
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {

    companion object {
        lateinit var prefs: MySharedPreferences
    }


    override fun onCreate() {
        super.onCreate()
        prefs = MySharedPreferences(applicationContext)
        KakaoSdk.init(this, "8ab6cd6b8425d701ee369a5b461275a8")
    }
}