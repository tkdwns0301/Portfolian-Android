# Portfolian

### 나의 첫 번째 프로젝트 포트폴리안
개발자의 개발 프로젝트 팀 구성을 위한 모바일 어플리케이션

### 🔗Link
[Play Store] (https://play.google.com/store/apps/details?id=com.hand.portfolian)

### Screen Shot
![스토어_1](https://user-images.githubusercontent.com/71424672/182586987-8017f689-ec4c-4cda-a80b-8a9d6fb2ca53.png)
![스토어_2](https://user-images.githubusercontent.com/71424672/182586997-db154bb6-7d65-43fe-8196-e29a8617d156.png)
![스토어_3](https://user-images.githubusercontent.com/71424672/182587007-6bcc9c61-b738-4b19-8fa7-e544c23aaa37.png)
![스토어_4](https://user-images.githubusercontent.com/71424672/182587011-78b86742-e5d7-4e68-bf5b-198987022415.png)

💡Portfolian은 개발자와 디자이너들에게 사이드 프로젝트를 구할 수 있는 창구를 만들고 싶었습니다. 
글 작성을 통해 나의 개발스택과 내가 원하는 개발스택을 가진 사람을 쉽게 찾을 수 있도록 구성하였으며, 북마크를 통해 원하는 프로젝트를 모아볼 수 있고, 채팅을 통해 어플 내에서 쉽게 소통할 수 있습니다.
그리고 나의 프로필에서 깃허브와 이메일, 스택 뱃지와 자기소개란을 활용하여 나를 어필할 수 있습니다.

### 🛠사용 기술 및 라이브러리
- Kotlin, Android
- Google Auth, Kakao Auth, Firebase Cloud Messeging
- Navigation, Retorift, Glide, Markdown, Socket-io, ImagePicker, binding

### 📱기능
- 로그인 페이지
    - Google Auth를 활용한 구글 로그인 (자동 로그인)
    - Kaka Auth를 활용한 카카오 로그인 (자동 로그인)
- 홈
    - 프로젝트 모두 보기 - RecyclerView, Retrofit2
    - 최신순, 조회순 정렬 - Retrofit2, RadioGroup
    - 키워드 검색 및 기술태그 필터 - DrawerLayout, SearchBar
    - 새로운 글 작성 - Retrofit2, Markdown
    - 작성된 글 상세보기 - Retrofit2, Markdown
    - 잘못된 내용의 글 신고 - Retrofit2
- 북마크
    - 북마크한 프로젝트 모아보기 - Retrofit2, RecyclerView
- 채팅
    - 현재 채팅중인 사람들의 목록 모아보기 - Retrofit2
    - 실시간 채팅 - Socket.io
    - 알림 - Firebase Cloud Messeging(FCM)
- 유저 페이지
    - 프로필 사진 수정하기 - Glider, ImagePicker
    - 프로필 수정하기 - Retrofit2, DrawerLayout, FlexboxLayout
    - 유저 깃 보기 - WebView
    - 유저 이메일 복사 - ClipManager
    - 유저 신고하기 - Retrofit2
