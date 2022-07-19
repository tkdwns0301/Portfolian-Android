package com.example.portfolian

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.portfolian.adapter.ChatAdapter
import com.example.portfolian.data.*
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.network.SocketApplication
import com.example.portfolian.service.OAuthService
import com.example.portfolian.service.TokenService
import com.example.portfolian.service.UserService
import com.example.portfolian.view.login.LogInActivity
import com.example.portfolian.view.login.LogInFragment
import com.example.portfolian.view.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.GsonBuilder
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import io.socket.emitter.Emitter
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieManager
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class SplashActivity: AppCompatActivity() {
    private lateinit var logInService: OAuthService
    private lateinit var userService: UserService
    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("splash: ", "${GlobalApplication.prefs.userId}")
        initRetrofit()


        if(GlobalApplication.prefs.loginStatus == 1) {
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    toLogIn()
                } else if (tokenInfo != null) {
                    tokenToServer("${AuthApiClient.instance.tokenManagerProvider.manager.getToken()!!.accessToken}")
                }
            }
        } else if(GlobalApplication.prefs.loginStatus == 2) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("727850004794-5clt9m4h33ff0vqprfl104qlm6m4t32e.apps.googleusercontent.com")
                .requestServerAuthCode("727850004794-5clt9m4h33ff0vqprfl104qlm6m4t32e.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)

            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 9001)


        } else {
            toLogIn()
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
            "727850004794-5clt9m4h33ff0vqprfl104qlm6m4t32e.apps.googleusercontent.com",
            "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",
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
                    val code = response.body()!!.code
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



    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        logInService = retrofit.create(OAuthService::class.java)
        userService = retrofit.create(UserService::class.java)
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

                    isBan(isNew)
                }
            }
            override fun onFailure(call: Call<OAuthResponse>, t: Throwable) {
                Log.e("LogInService: ", "$t")
            }
        })
    }

    private fun isBan(isNew: Boolean) {
        val isBanUser = userService.isBanUser("Bearer ${GlobalApplication.prefs.accessToken}", "${GlobalApplication.prefs.userId}")

        isBanUser.enqueue(object: Callback<IsBanUserResponse> {
            override fun onResponse(
                call: Call<IsBanUserResponse>,
                response: Response<IsBanUserResponse>
            ) {
                if(response.isSuccessful) {
                    val isBan = response.body()!!.isBan

                    if(isBan) {
                        Toast.makeText(applicationContext, "신고를 통해 제제된 사용자입니다. 자세한 사항은 문의주세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        SocketApplication.setSocket()
                        SocketApplication.establishConnection()

                        val mSocket = SocketApplication.mSocket

                        mSocket.on("connection") {
                            SocketApplication.sendUserId()
                            toMain()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<IsBanUserResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "로그인 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun toMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    private fun toLogIn() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

}