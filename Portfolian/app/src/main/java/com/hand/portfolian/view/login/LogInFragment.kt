package com.hand.portfolian.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.hand.portfolian.R
import com.hand.portfolian.data.*
import com.hand.portfolian.databinding.FragmentLoginBinding
import com.hand.portfolian.network.GlobalApplication
import com.hand.portfolian.network.RetrofitClient
import com.hand.portfolian.network.SocketApplication
import com.hand.portfolian.service.OAuthService
import com.hand.portfolian.service.TokenService
import com.hand.portfolian.service.UserService
import com.hand.portfolian.view.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.user.UserApiClient
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieManager
import java.util.concurrent.TimeUnit

class LogInFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var logInService: OAuthService
    private lateinit var userService: UserService
    private lateinit var retrofit: Retrofit
    private lateinit var kakaoLogin: ImageButton
    private lateinit var navController: NavController
    private lateinit var googleLogin: ConstraintLayout


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
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
        userService = retrofit.create(UserService::class.java)
    }


    private fun initView() {
        kakaoLogin = binding.btnKakao
        googleLogin = binding.clGoogle
        initKakao()
        initGoogle()
    }

    private fun initKakao() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AccessDenied.toString() -> {
                        Toast.makeText(requireContext(), "로그인 하는 도중 오류가 발생했습니다. (오류코드: 1)", Toast.LENGTH_SHORT).show()
                        Log.e("LogIn Error: ", "접근이 거부 됨(동의 취소)")
                    }
                    error.toString() == InvalidClient.toString() -> {
                        Toast.makeText(requireContext(), "로그인 하는 도중 오류가 발생했습니다. (오류코드: 2)", Toast.LENGTH_SHORT).show()
                        Log.e("LogIn Error: ", "유효하지 않은 앱")
                    }
                    error.toString() == InvalidGrant.toString() -> {
                        Toast.makeText(requireContext(), "로그인 하는 도중 오류가 발생했습니다. (오류코드: 3)", Toast.LENGTH_SHORT).show()
                        Log.e("LogIn Error: ", "인증 수단이 유효하지 않아 인증할 수 없는 상태")
                    }
                    error.toString() == InvalidRequest.toString() -> {
                        Toast.makeText(requireContext(), "로그인 하는 도중 오류가 발생했습니다. (오류코드: 4)", Toast.LENGTH_SHORT).show()
                        Log.e("LogIn Error: ", "요청 파라미터 오류")
                    }
                    error.toString() == InvalidScope.toString() -> {
                        Toast.makeText(requireContext(), "로그인 하는 도중 오류가 발생했습니다. (오류코드: 5)", Toast.LENGTH_SHORT).show()
                        Log.e("LogIn Error: ", "유효하지 않은 scope ID")
                    }
                    error.toString() == Misconfigured.toString() -> {
                        Toast.makeText(requireContext(), "로그인 하는 도중 오류가 발생했습니다. (오류코드: 6)", Toast.LENGTH_SHORT).show()
                        Log.e("LogIn Error: ", "설정이 올바르지 않음(android key hash)")
                    }
                    error.toString() == ServerError.toString() -> {
                        Toast.makeText(requireContext(), "로그인 하는 도중 오류가 발생했습니다. (오류코드: 7)", Toast.LENGTH_SHORT).show()
                        Log.e("LogIn Error: ", "서버 내부 에러")
                    }
                    error.toString() == Unauthorized.toString() -> {
                        Toast.makeText(requireContext(), "로그인 하는 도중 오류가 발생했습니다. (오류코드: 8)", Toast.LENGTH_SHORT).show()
                        Log.e("LogIn Error: ", "앱이 요청 권한이 없음")
                    }
                    else -> {
                        Toast.makeText(requireContext(), "로그인 하는 도중 오류가 발생했습니다. (오류코드: 9)", Toast.LENGTH_SHORT).show()
                        Log.e("LogIn Error: ", "기타 에러: $error")
                    }
                }
            } else if (token != null) {
                tokenToServer(token.accessToken)
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

    private fun initGoogle() {
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(R.string.google_client_id.toString())
            .requestServerAuthCode(R.string.google_client_id.toString())
            .requestEmail()
            .build()

        var googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        googleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 9001)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 9001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)!!
                getGoogleAccessToken(account.serverAuthCode!!)

            } catch (e: ApiException) {
                Log.e("account", "$e")
            }

        }
    }


    private fun getGoogleAccessToken(authCode: String) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val gson = GsonBuilder().setLenient().create()

        val client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(CookieManager()))
            .addInterceptor(interceptor)
            .connectTimeout(20000L, TimeUnit.SECONDS)
            .build()

        val instance = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        val googleLoginService = instance.create(TokenService::class.java)

        val loginGoogleRequest = LoginGoogleRequest(
            "authorization_code",
            R.string.google_client_id.toString(),
            R.string.google_clent_secret.toString(),
            "",
            authCode
        )

        val googleAccessTokenService = googleLoginService.getGoogleAccessToken(loginGoogleRequest)

        googleAccessTokenService.enqueue(object : Callback<LoginGoogleResponse> {
            override fun onResponse(
                call: Call<LoginGoogleResponse>,
                response: Response<LoginGoogleResponse>
            ) {
                if (response.isSuccessful) {
                    val accessToken = response.body()!!.access_token
                    googleTokenToServer(accessToken)
                }
            }

            override fun onFailure(call: Call<LoginGoogleResponse>, t: Throwable) {
                Log.e("accessToken: ", "$t")
            }
        })


    }

    private fun googleTokenToServer(token: String) {
        val googleToken = KakaoTokenRequest(token)
        val tokenService = logInService.getGoogleToken(googleToken)

        tokenService.enqueue(object: Callback<OAuthResponse> {
            override fun onResponse(call: Call<OAuthResponse>, response: Response<OAuthResponse>) {
                if(response.isSuccessful) {
                    val isNew = response.body()!!.isNew
                    val accessToken = response.body()!!.accessToken
                    val userId = response.body()!!.userId

                    GlobalApplication.prefs.accessToken = accessToken
                    GlobalApplication.prefs.userId = userId
                    GlobalApplication.prefs.loginStatus = 2

                    isBan(isNew)
                }
            }

            override fun onFailure(call: Call<OAuthResponse>, t: Throwable) {
                Log.e("googleTokenToServer: ", "$t")
            }
        })
    }


    private fun nickname(isNew: Boolean) {
        if (isNew) {
            activity?.runOnUiThread {
                navController.navigate(R.id.action_logInFragment_to_nicknameFragment)
            }

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
                    val isNew = response.body()!!.isNew
                    val accessToken = response.body()!!.accessToken
                    val userId = response.body()!!.userId

                    GlobalApplication.prefs.accessToken = accessToken
                    GlobalApplication.prefs.userId = userId
                    GlobalApplication.prefs.loginStatus = 1

                    isBan(isNew)
                }
            }

            override fun onFailure(call: Call<OAuthResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "로그인 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isBan(isNew: Boolean) {
        val isBanUser = userService.isBanUser(
            "Bearer ${GlobalApplication.prefs.accessToken}",
            "${GlobalApplication.prefs.userId}"
        )

        isBanUser.enqueue(object : Callback<IsBanUserResponse> {
            override fun onResponse(
                call: Call<IsBanUserResponse>,
                response: Response<IsBanUserResponse>
            ) {
                if (response.isSuccessful) {
                    val isBan = response.body()!!.isBan

                    if (isBan) {
                        Toast.makeText(
                            requireContext(),
                            "신고를 통해 제제된 사용자입니다. 자세한 사항은 문의주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        SocketApplication.setSocket()
                        SocketApplication.establishConnection()

                        val mSocket = SocketApplication.mSocket



                        mSocket.on("connection") {
                            nickname(isNew)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<IsBanUserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "로그인 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }


}