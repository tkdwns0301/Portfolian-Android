package com.hand.portfolian.service

import com.hand.portfolian.data.*
import retrofit2.Call
import retrofit2.http.*

interface OAuthService {
    @Headers("content-type: application/json")
    @POST("oauth/kakao/access")
    fun getToken(
        @Body token: KakaoTokenRequest
    )
    : Call<OAuthResponse>

    @Headers("content-type: application/json")
    @POST("oauth/google/access")
    fun getGoogleToken(
        @Body token: KakaoTokenRequest
    )
    : Call<OAuthResponse>

    @Headers("content-type: application/json")
    @PATCH("users/{userId}/nickName")
    fun setNickName(
        @Header("Authorization") Authorization: String,
        @Path("userId") userId: String?,
        @Body nickName: NickNameRequest
    )
    : Call<NickNameResponse>

    @Headers("content-type: application/json")
    @PATCH("oauth/logout")
    fun setLogout(
        @Header("Authorization") Authorization: String
    )
    : Call<LogoutResponse>

    @DELETE("users/{userId}")
    fun unLink(
        @Header("Authorization") Authorization: String,
        @Path("userId") userId: String
    )
    : Call<UnlinkResponse>




}