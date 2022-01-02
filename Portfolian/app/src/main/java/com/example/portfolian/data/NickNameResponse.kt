package com.example.portfolian.data

import com.google.gson.annotations.SerializedName

class NickNameResponse (
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
    )