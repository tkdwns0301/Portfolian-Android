package com.example.portfolian.service

import com.example.portfolian.data.*
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

    @PATCH("/users/{userId}/profile/default")
    fun modifyDefaultProfile(
        @Header("Authorization") Authorization: String,
        @Path("userId") userId: String
    ) : Call<ProfileImageResponse>

    @Multipart
    @PATCH("/users/{userId}/profile")
    fun modifyCustomProfile(
        @Header("Authorization") Authorization: String,
        @Path("userId") userId: String,
        @Part photo: MultipartBody.Part
    ) : Call<ProfileImageResponse>
}