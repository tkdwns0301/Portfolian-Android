package com.example.portfolian.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailProjectResponse (
    @SerializedName("code")
    var code: Int,
    @SerializedName("projectId")
    var projectId: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("stackList")
    var stackList: List<String>,
    @SerializedName("contents")
    var contents: DetailContent,
    @SerializedName("capacity")
    var capacity: Int,
    @SerializedName("view")
    var view: Int,
    @SerializedName("bookMark")
    var bookMark: Boolean,
    @SerializedName("status")
    var status: Int,
    @SerializedName("leader")
    var leader: LeaderContent
): Parcelable

@Parcelize
data class DetailContent(
    //주제설명
    @SerializedName("subjectDescription")
    var subjectDescription: String,
    //프로젝트 기간
    @SerializedName("projectTime")
    var projectTime: String,
    //모집조건
    @SerializedName("recruitmentCondition")
    var recruitmentCondition: String,
    //진행방식
    @SerializedName("progress")
    var progress: String,
    //프로젝트 상세
    @SerializedName("description")
    var description: String
) : Parcelable

@Parcelize
data class LeaderContent(
    @SerializedName("userId")
    var userId: String,
    @SerializedName("nickName")
    var nickName: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("stack")
    var stack: String,
    @SerializedName("photo")
    var photo: String
) : Parcelable