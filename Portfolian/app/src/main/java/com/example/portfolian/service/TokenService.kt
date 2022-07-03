package com.example.portfolian.service

import com.example.portfolian.data.*
import retrofit2.Call
import retrofit2.http.*

interface TokenService {
    //38 accessToken 갱신
    @Headers("content-type: application/json")
    @POST("oauth/refresh")
    fun getAccessToken(
        @Body renewalTokenRequest: RenewalTokenRequest
    )
    : Call<TokenResponse>

    @PATCH("users/{userId}/fcm")
    fun sendFCMToken(
        @Header("Authorization") Authorization: String,
        @Path("userId") userId: String,
        @Body fcmToken: SendFCMTokenRequest
    )
    : Call<SendFCMTokenResponse>

    @Headers("content-type: application/json")
    @POST("oauth2/v4/token")
    fun getGoogleAccessToken(
        @Body loginGoogleRequest: LoginGoogleRequest
    ) : Call<LoginGoogleResponse>
}