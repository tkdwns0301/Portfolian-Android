package com.example.portfolian.service

import com.example.portfolian.data.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface TokenService {
    //38 accessToken 갱신
    @Headers("content-type: application/json")
    @POST("oauth/refresh")
    fun getAccessToken(
        @Body renewalTokenRequest: RenewalTokenRequest
    )
    : Call<TokenResponse>
}