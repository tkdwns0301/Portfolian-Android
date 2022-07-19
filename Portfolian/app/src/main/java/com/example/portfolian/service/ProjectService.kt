package com.example.portfolian.service

import com.example.portfolian.data.*
import com.google.android.datatransport.cct.StringMerger
import retrofit2.Call
import retrofit2.http.*

interface ProjectService {
    // 29. 모집글 생성
    @Headers("content-type: application/json")
    @POST("projects")
    fun writeProject(
        @Header("Authorization") Authorization: String,
        @Body write: WriteProjectRequest
    )
    : Call<WriteProjectResponse>

    // 2. 프로젝트 모두 보기
    @GET("projects")
    fun readAllProject(
        @Header("Authorization") Authorization: String,
        @Query("stack") stack: List<String>,
        @Query("keyword") keyword: String,
        @Query("sort") sort: String
    )
    : Call<ReadProjectResponse>

    // 3. 프로젝트 모집글 보기
    @GET("projects/{projectId}")
    fun readDetailProject(
        @Header("Authorization") Authorization: String,
        @Path("projectId") projectId: String
    )
    : Call<DetailProjectResponse>

    // 북마크한 프로젝트 보기
    @GET("users/{userId}/bookMark")
    fun readAllBookmark(
        @Header("Authorization") Authorization: String,
        @Path("userId") userId: String
    )
    : Call<ReadProjectResponse>

    // 5. 북마크하기/ 취소하기
    @Headers("content-type: application/json")
    @POST("users/{userId}/bookMark")
    fun setBookmark(
        @Header ("Authorization") Authorization: String,
        @Path("userId") userId: String,
        @Body set: SetBookmarkRequest
    )
    : Call<SetBookmarkResponse>

    // 6. 프로젝트 모집글 수정하기
    @PUT("projects/{projectId}")
    fun modifyProject(
        @Header ("Authorization") Authorization: String,
        @Path("projectId") projectId: String,
        @Body modify: WriteProjectRequest
    )
    :Call<ModifyProjectResponse>

    // 7. 프로젝트 삭제하기
    @DELETE("projects/{projectId}")
    fun deleteProject(
        @Header ("Authorization") Authorization: String,
        @Path("projectId") projectId: String
    )
    : Call<ModifyProjectResponse>

    // 12. 프로젝트 모집완료
    @PATCH("projects/{projectId}/status")
    fun modifyProjectStatus(
        @Header("Authorization") Authorization: String,
        @Path("projectId") projectId: String,
        @Body modify: ModifyProjectStatusRequest
    )
    : Call<ModifyProjectStatusResponse>

    // 47. 프로젝트 신고하기
    @Headers("content-type: application/json")
    @POST("reports/projects/{projectId}")
    fun reportProject(
        @Header("Authorization") Authorization: String,
        @Path("projectId") projectId: String,
        @Body report: ReportRequest
    )
    : Call<ReportResponse>
}