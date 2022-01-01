package com.example.portfolian.service

import com.example.portfolian.data.KakaoToken
import com.example.portfolian.data.OauthResponse
import com.example.portfolian.data.WriteProject
import com.example.portfolian.data.WriteProjectResponse
import com.kakao.sdk.auth.model.OAuthToken
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OauthService {
    @Headers("content-type: application/json")
    @POST("oauth/kakao/access")
    fun getToken(
        @Body token: KakaoToken
    )
    : Call<OauthResponse>
}