package com.hand.portfolian.network

import android.app.Application
import com.hand.portfolian.MySharedPreferences
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