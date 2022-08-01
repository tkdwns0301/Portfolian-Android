package com.hand.portfolian.service

import com.hand.portfolian.data.*
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

    @PATCH("/users/{userId}/info")
    fun modifyProfile(
        @Header("Authorization") Authorization: String,
        @Path("userId") userId: String,
        @Body modify: ModifyProfileRequest
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

    @GET("/users/{userId}/isBan")
    fun isBanUser(
        @Header("Authorization") Authorization: String,
        @Path("userId") userId: String
    ) : Call<IsBanUserResponse>
}