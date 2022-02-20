package com.example.portfolian.service

import com.example.portfolian.data.ModifyProfileRequest
import com.example.portfolian.data.ModifyProfileResponse
import com.example.portfolian.data.UserInfoResponse
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    @GET("/users/{userId}/info")
    fun readUserInfo(
        @Header("Authorization") Authorization: String,
        @Path("userId") userId: String
    )
    : Call<UserInfoResponse>

    @Headers("content-type: application/json")
    @PATCH("/users/{userId}/info")
    fun modifyProfile(
        @Header("Authorization") Authorization: String,
        @Path("userId") userId: String,
        @Body userInfoData: ModifyProfileRequest
    )
    : Call<ModifyProfileResponse>
}