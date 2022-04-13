package com.example.portfolian.service

import com.example.portfolian.data.*
import retrofit2.Call
import retrofit2.http.*

interface ChatService {
    @Headers("content-type: application/json")
    @POST("chats")
    fun createChat(
        @Header("Authorization") Authorization: String,
        @Body chat: CreateChatRequest
    ): Call<CreateChatResponse>

    @GET("chats")
    fun readChatList(
        @Header("Authorization") Authorization: String,
    ): Call<ReadChatList>

    @GET("chats/{chatRoomId}")
    fun readChat(
        @Header("Authorization") Authorization: String,
        @Path("projectId") projectId: String
    ): Call<ReadChatResponse>
}