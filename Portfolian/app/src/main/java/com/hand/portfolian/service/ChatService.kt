package com.hand.portfolian.service

import com.hand.portfolian.data.*
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
        @Path("chatRoomId") chatRoomId: String
    ): Call<ReadChatResponse>

    @PUT("chats/{chatRoomId}")
    fun removeChat (
        @Header("Authorization") Authorization: String,
        @Path("chatRoomId") chatRoomId: String
    ): Call<RemoveChatResponse>
}