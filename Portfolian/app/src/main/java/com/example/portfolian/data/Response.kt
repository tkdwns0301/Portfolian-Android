package com.example.portfolian.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

data class Response(val response: String)

// 2 프로젝트 모두 보기
data class ReadProjectResponse(
    @SerializedName("code")
    var code: Int,

    @SerializedName("articleList")
    var articleList: ArrayList<Project>
)

@Parcelize
data class Project(
    @SerializedName("projectId")
    var projectId: String,

    @SerializedName("title")
    var title: String,

    @SerializedName("stackList")
    var stackList: List<String>,

    @SerializedName("description")
    var description: String,

    @SerializedName("capacity")
    var capacity: Int,

    @SerializedName("view")
    var view: Int,

    @SerializedName("bookMark")
    var bookMark: Boolean,

    @SerializedName("status")
    var status: Int,

    @SerializedName("leader")
    var leader: Leader
) : Parcelable

@Parcelize
data class Leader(
    var userId: String,
    var photo: String
) : Parcelable
//------------------------------------

// 3 프로젝트 모집글 보기
@Parcelize
data class DetailProjectResponse(
    @SerializedName("projectId")
    var projectId: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("stackList")
    var stackList: List<String>,
    @SerializedName("contents")
    var contents: DetailContent,
    @SerializedName("capacity")
    var capacity: Int,
    @SerializedName("view")
    var view: Int,
    @SerializedName("bookMark")
    var bookMark: Boolean,
    @SerializedName("status")
    var status: Int,
    @SerializedName("createdAt")
    var createdAt: String,
    @SerializedName("leader")
    var leader: LeaderContent
) : Parcelable

@Parcelize
data class DetailContent(
    //주제설명
    @SerializedName("subjectDescription")
    var subjectDescription: String,
    //프로젝트 기간
    @SerializedName("projectTime")
    var projectTime: String,
    //모집조건
    @SerializedName("recruitmentCondition")
    var recruitmentCondition: String,
    //진행방식
    @SerializedName("progress")
    var progress: String,
    //프로젝트 상세
    @SerializedName("description")
    var description: String
) : Parcelable

@Parcelize
data class LeaderContent(
    @SerializedName("userId")
    var userId: String,
    @SerializedName("nickName")
    var nickName: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("stack")
    var stack: String,
    @SerializedName("photo")
    var photo: String
) : Parcelable
//------------------------------------

// 5 북마크하기/ 취소하기
@Parcelize
data class SetBookmarkResponse(
    @SerializedName("code")
    var code: Int,
) : Parcelable

// 6 프로젝트 모집글 수정하기
@Parcelize
data class ModifyProjectResponse(
    @SerializedName("code")
    var code: Int,
    var message: String
) : Parcelable
//------------------------------------

// 13 나의 정보 보기
@Parcelize
data class UserInfoResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("userId")
    var userId: String,
    @SerializedName("nickName")
    var nickName: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("stackList")
    var stackList: List<String>,
    @SerializedName("photo")
    var photo: String,
    @SerializedName("github")
    var github: String,
    @SerializedName("mail")
    var mail: String

) : Parcelable
//------------------------------------

// 14 나의 정보 수정
@Parcelize
data class ModifyProfileResponse(
    @SerializedName("code")
    var code: Int
) : Parcelable
//------------------------------------

// 26 로그아웃
@Parcelize
data class LogoutResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String
) : Parcelable
//------------------------------------

// 28 회원탈퇴
@Parcelize
data class UnlinkResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("message")
    var message: String
) : Parcelable

// 29 모집글 생성
data class WriteProjectResponse(
    @SerializedName("code")
    var code: Int,

    @SerializedName("message")
    var message: String,

    @SerializedName("newProjectID")
    var newProjectID: String
)
//------------------------------------

// 36 카카오 토큰 보내줄 때
data class OAuthResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("isNew")
    val isNew: Boolean,
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("userId")
    val userId: String
)
//------------------------------------

// 37 첫 로그인 시 닉네임 설정
class NickNameResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)
//------------------------------------

// 38 accessToken 갱신
data class TokenResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("message")
    val message: String
)
//------------------------------------

// 40 채팅방 만들기
data class CreateChatResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("chatRoomId")
    val chatRoomId: String,
    @SerializedName("message")
    val message: String
)
//----------------------------------

// 41 나의 채팅방 목록 조회
@Parcelize
data class ReadChatList(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("chatRoomList")
    val chatRoomList: ArrayList<ChatRoom>
) : Parcelable

@Parcelize
data class ChatRoom(
    @SerializedName("chatRoomId")
    val chatRoomId: String,
    @SerializedName("projectTitle")
    val projectTitle: String,
    @SerializedName("newChatCnt")
    val newChatCnt: Int,
    @SerializedName("newChatContent")
    val newChatContent: String,
    @SerializedName("newChatDate")
    val newChatDate: Date,
    @SerializedName("user")
    val user: User
) : Parcelable

@Parcelize
data class User(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("nickName")
    val nickName: String,
    @SerializedName("photo")
    val photo: String
) : Parcelable
//-------------------------------------

// 42 채팅목록 조회 (채팅 메세지 조회)
@Parcelize
data class ReadChatResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("chatList")
    val chatList: Chat
) : Parcelable

@Parcelize
data class Chat(
    @SerializedName("oldChatList")
    val oldChatList: ArrayList<ChatModel>,
    @SerializedName("newChatList")
    val newChatList: ArrayList<ChatModel>
) : Parcelable
//-------------------------------------

// 43 채팅방 나가기
@Parcelize
data class RemoveChatResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
) : Parcelable
//-------------------------------------
