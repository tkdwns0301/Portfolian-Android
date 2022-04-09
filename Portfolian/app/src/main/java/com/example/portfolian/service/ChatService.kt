package com.example.portfolian.service

import com.example.portfolian.data.CreateChatRequest
import com.example.portfolian.data.CreateChatResponse
import com.example.portfolian.data.KakaoTokenRequest
import com.example.portfolian.data.OAuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatService {
    @Headers("content-type: application/json")
    @POST("chats")
    fun createChat(
        @Header("Authorization") Authorization: String,
        @Body chat: CreateChatRequest
    ): Call<CreateChatResponse>
}