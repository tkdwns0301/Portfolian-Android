package com.example.portfolian.service

import com.example.portfolian.data.Article
import com.example.portfolian.data.ReadProjectResponse
import com.example.portfolian.data.WriteProjectResponse
import com.example.portfolian.data.test
import retrofit2.Call
import retrofit2.http.*

interface ProjectService {
    @Headers("content-type: application/json")
    @POST("projects")
    fun writeProject(
        @Body jsonParm: test
    )
    : Call<WriteProjectResponse>

    @GET("projects")
    fun readAllProject(
        @Query("stack") stack: List<String>,
        @Query("keyword") keyword: String,
        @Query("sort") sort: String
    )
    : Call<ReadProjectResponse>
}