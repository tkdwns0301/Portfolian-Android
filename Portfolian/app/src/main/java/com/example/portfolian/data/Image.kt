package com.example.portfolian.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    @SerializedName("id")
    var id: Int = -1,
    @SerializedName("path")
    val path: String
) : Parcelable