package com.hand.portfolian.data

data class Request (val request: String)

// 5 북마크하기/ 취소하기
data class SetBookmarkRequest (
    var projectId: String,
    var bookMarked: Boolean
)
// 6 프로젝트 모집글 수정하기
data class ModifyProjectRequest (
    var article: Article,
    var ownerStack: String
)
//------------------------------------

// 12. 프로젝트 상태 변경
data class ModifyProjectStatusRequest (
    var status: Int
)
//-------------------------------------

// 14 나의 정보 수정
data class ModifyProfileRequest(
    var nickName: String,
    var description: String,
    var stack: List<String>,
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

// 40 채팅방 만들기
data class CreateChatRequest(
    val userId: String,
    val projectId: String
)
//------------------------------------

// 46, 47 사용자 or 프로젝트 신고하기
data class ReportRequest(
    val reason: String
)
//------------------------------------

// 48 FCM 토큰 보내기
data class SendFCMTokenRequest(
    val fcmToken: String
)

// 구글 idToken to accessToken
data class LoginGoogleRequest(
    val grant_type: String,
    val client_id: String,
    val client_secret: String,
    val redirect_uri: String,
    val code: String
)