package com.example.portfolian.view.login

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.portfolian.R
import com.example.portfolian.data.KakaoTokenRequest
import com.example.portfolian.data.OAuthResponse
import com.example.portfolian.databinding.FragmentLoginBinding
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.network.SocketApplication
import com.example.portfolian.service.OAuthService
import com.example.portfolian.view.main.MainActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LogInFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding

    private lateinit var logInService: OAuthService
    private lateinit var retrofit: Retrofit
    private lateinit var kakaoLogin: ImageButton

    private lateinit var loginActivity: LogInActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        loginActivity = context as LogInActivity
    }

    private fun init() {
        initRetrofit()
        initView()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        logInService = retrofit.create(OAuthService::class.java)
    }


    private fun initView() {
        kakaoLogin = binding.btnKakao
        initKakao()
    }

    private fun initKakao() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AccessDenied.toString() -> {
                        Log.e("LogIn Error: ", "접근이 거부 됨(동의 취소)")
                    }
                    error.toString() == InvalidClient.toString() -> {
                        Log.e("LogIn Error: ", "유효하지 않은 앱")
                    }
                    error.toString() == InvalidGrant.toString() -> {
                        Log.e("LogIn Error: ", "인증 수단이 유효하지 않아 인증할 수 없는 상태")
                    }
                    error.toString() == InvalidRequest.toString() -> {
                        Log.e("LogIn Error: ", "요청 파라미터 오류")
                    }
                    error.toString() == InvalidScope.toString() -> {
                        Log.e("LogIn Error: ", "유효하지 않은 scope ID")
                    }
                    error.toString() == Misconfigured.toString() -> {
                        Log.e("LogIn Error: ", "설정이 올바르지 않음(android key hash)")
                    }
                    error.toString() == ServerError.toString() -> {
                        Log.e("LogIn Error: ", "서버 내부 에러")
                    }
                    error.toString() == Unauthorized.toString() -> {
                        Log.e("LogIn Error: ", "앱이 요청 권한이 없음")
                    }
                    else -> {
                        Log.e("LogIn Error: ", "기타 에러: $error")
                    }
                }
            } else if (token != null) {
                tokenToServer(token.accessToken)
                Log.d("token", "${token.accessToken}")
            }
        }
        kakaoLogin.setOnClickListener {
            //TODO Kakao 로그인 실행
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                UserApiClient.instance.loginWithKakaoTalk(requireContext(), callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
            }

        }
    }


    private fun nickname(isNew: Boolean) {
        if(isNew) {
        val intent = Intent(context, NicknameFragment::class.java)
        startActivity(intent)
        } else {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun tokenToServer(idToken: String) {
        val kakaoToken = KakaoTokenRequest(idToken)
        val tokenService = logInService.getToken(kakaoToken)

        tokenService.enqueue(object : Callback<OAuthResponse> {
            override fun onResponse(call: Call<OAuthResponse>, response: Response<OAuthResponse>) {
                if (response.isSuccessful) {
                    val code = response.body()!!.code
                    val isNew = response.body()!!.isNew
                    val accessToken = response.body()!!.accessToken
                    val userId = response.body()!!.userId

                    GlobalApplication.prefs.accessToken = accessToken
                    GlobalApplication.prefs.userId = userId

                    SocketApplication.setSocket()
                    SocketApplication.establishConnection()

                    val mSocket = SocketApplication.mSocket

                    mSocket.on("connection") {
                        nickname(isNew)
                    }

                }
            }

            override fun onFailure(call: Call<OAuthResponse>, t: Throwable) {
                Log.e("LogInService: ", "$t")
            }
        })
    }


}