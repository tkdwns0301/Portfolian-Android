package com.example.portfolian.service

import com.example.portfolian.data.ModifyProfileResponse
import com.example.portfolian.data.ReportRequest
import com.example.portfolian.data.ReportResponse
import com.example.portfolian.data.UserInfoResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    @GET("/users/{userId}/info")
    fun readUserInfo(
        @Header("Authorization") Authorization: String,
        @Path("userId") userId: String
    )
    : Call<UserInfoResponse>

    @Multipart
    @PATCH("/users/{userId}/info")
    fun modifyProfile(
        @Header("Authorization") Authorization: String,
        @Path("userId") userId: String,
        @Part ("nickName") nickName: String,
        @Part ("description") description: String,
        @Part ("stack") stack: List<String>,
        @Part photo: MultipartBody.Part,
        @Part ("github") github: String,
        @Part ("mail") mail: String
    )
    : Call<ModifyProfileResponse>

    @Headers("content-type: application/json")
    @POST("/reports/users/{userId}")
    fun reportUser(
        @Header("Authorization") Authorization: String,
        @Path("userId") userId: String,
        @Body reason: ReportRequest
    )
    : Call<ReportResponse>
}