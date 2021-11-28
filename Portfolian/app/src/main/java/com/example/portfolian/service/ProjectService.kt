package com.example.portfolian.service

import com.example.portfolian.data.Article
import com.example.portfolian.data.WriteProjectResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface ProjectService {
    @FormUrlEncoded
    @POST("projects")
    fun writeProject(
        @Field("userId") userId: String,
        @Field("article") article: Article,
        @Field("ownerStack") ownerStack: String )
    : Call<WriteProjectResponse>
}