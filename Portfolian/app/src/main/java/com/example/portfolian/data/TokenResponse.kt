package com.example.portfolian.data

import com.google.gson.annotations.SerializedName

data class TokenResponse (
    @SerializedName("code")
    val code: Int,
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("message")
    val message: String
    )