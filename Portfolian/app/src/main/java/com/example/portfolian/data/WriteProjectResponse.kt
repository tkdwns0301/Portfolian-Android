package com.example.portfolian.data

import com.google.gson.annotations.SerializedName

data class WriteProjectResponse(
    @SerializedName("code")
    var code: Int,

    @SerializedName("message")
    var message: String,

    @SerializedName("newProjectID")
    var newProjectID: String
)