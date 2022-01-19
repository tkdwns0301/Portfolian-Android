package com.example.portfolian.data

import com.google.gson.annotations.SerializedName

data class Request (val request: String)

// 6 프로젝트 모집글 수정하기
data class ModifyProjectRequest (
    var article: Article,
    var ownerStack: String
    )
//------------------------------------
// 14 나의 정보 수정
data class ModifyProfile (
    var nickName: String,
    var description: String,
    var stack: List<String>,
    var photo: String,
    var github: String,
    var mail: String
        )
//------------------------------------

// 29 모집글 생성
data class WriteProjectRequest (
    var article: Article,
    var ownerStack: String
    )
data class Article(
    var title: String,
    var stackList: List<String>,
    var subjectDescription: String,
    var projectTime: String,
    var condition: String,
    var progress: String,
    var description: String,
    var capacity: Int
)
//------------------------------------

// 36 카카오 토른 보내줄 때
data class KakaoTokenRequest (
    val token: String
    )
//------------------------------------

// 37 첫 로그인 시 닉네임 설정
data class NickNameRequest(
    val nickName: String
    )
//------------------------------------

// 38 accessToken 갱신
data class RenewalTokenRequest(
    val userId: String
    )
//------------------------------------