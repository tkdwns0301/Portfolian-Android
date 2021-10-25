package com.example.portfolian.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ReadProjectResponse(
    @SerializedName("code")
    var code: Int,

    @SerializedName("projectList")
    var projectList: ArrayList<Project>
)

@Parcelize
data class Project(
    @SerializedName("projectIdx")
    var projectIdx: Long,

    @SerializedName("title")
    var title: String,

    @SerializedName("stackList")
    var stackList: List<String>,

    @SerializedName("description")
    var description: String,

    @SerializedName("capacity")
    var capacity: Int,

    @SerializedName("view")
    var view: Int,

    @SerializedName("bookMark")
    var bookMark: Boolean,

    @SerializedName("status")
    var status: Int,

    @SerializedName("Profile")
    var profile: ArrayList<Image> = arrayListOf()
) : Parcelable