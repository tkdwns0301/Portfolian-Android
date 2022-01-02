package com.example.portfolian.data

import com.google.gson.annotations.SerializedName


data class OauthResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("isNew")
    val isNew: Boolean,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("userId")
    val userId: String
)