package com.example.portfolian.service

import com.example.portfolian.data.Article
import com.example.portfolian.data.ReadProjectResponse
import com.example.portfolian.data.WriteProjectResponse
import retrofit2.Call
import retrofit2.http.*

interface ProjectService {
    @FormUrlEncoded
    @POST("projects")
    fun writeProject(
        @Field("userId") userId: String,
        @Field("article") article: Article,
        @Field("ownerStack") ownerStack: String
    )
    : Call<WriteProjectResponse>

    @GET("projects")
    fun readAllProject(
        @Query("stack") stack: String,
        @Query("keyword") keyword: String,
        @Query("sort") sort: String
    )
    : Call<ReadProjectResponse>
}