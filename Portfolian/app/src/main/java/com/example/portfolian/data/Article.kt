package com.example.portfolian.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Article(
    @SerializedName("title")
    var title: String,

    @SerializedName("stackList")
    var stackList: List<String>,

    @SerializedName("subjectDescription")
    var subjectDescription: String,

    @SerializedName("projectTime")
    var projectTime: String,

    @SerializedName("condition")
    var condition: String,

    @SerializedName("progress")
    var progress: String,

    @SerializedName("description")
    var description: String,

    @SerializedName("capacity")
    var capacity: Int
)