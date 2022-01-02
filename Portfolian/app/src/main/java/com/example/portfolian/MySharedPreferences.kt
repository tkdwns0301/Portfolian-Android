package com.example.portfolian

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {
    private val prefsFilename = "prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(prefsFilename, 0)

    var accessToken: String?
        get() = prefs.getString("accessToken", "")
        set(value) = prefs.edit().putString("accessToken", value).apply()

    var refreshToken: String?
        get() = prefs.getString("refreshToken", "")
        set(value) = prefs.edit().putString("refreshToken", value).apply()

    var userId: String?
        get() = prefs.getString("userId", "")
        set(value) = prefs.edit().putString("userId", value).apply()
}