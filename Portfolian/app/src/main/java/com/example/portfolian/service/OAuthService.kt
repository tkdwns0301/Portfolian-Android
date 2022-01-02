package com.example.portfolian.service

import com.example.portfolian.data.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface OAuthService {
    @Headers("content-type: application/json")
    @POST("oauth/kakao/access")
    fun getToken(
        @Body token: KakaoTokenRequest
    )
    : Call<OauthResponse>

    @Headers("content-type: application/json")
    @PATCH("users/{userId}/nickName")
    fun setNickName(
        @Header("Authorization") Authorization: String?,
        @Path("userId") userId: String?,
        @Body nickName: NickNameRequest
    )
    : Call<NickNameResponse>

}