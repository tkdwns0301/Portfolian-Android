package com.example.portfolian

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.portfolian.data.KakaoTokenRequest
import com.example.portfolian.data.OAuthResponse
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.network.SocketApplication
import com.example.portfolian.service.OAuthService
import com.example.portfolian.view.login.LogInActivity
import com.example.portfolian.view.login.LogInFragment
import com.example.portfolian.view.main.MainActivity
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SplashActivity: AppCompatActivity() {
    private lateinit var logInService: OAuthService
    private lateinit var retrofit: Retrofit


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRetrofit()

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if(error != null) {
                toLogIn()
            }
            else if(tokenInfo != null) {
                tokenToServer("${AuthApiClient.instance.tokenManagerProvider.manager.getToken()!!.accessToken}")


            }

        }

        /*Handler().postDelayed({
            intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_TIME_OUT)*/

    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        logInService = retrofit.create(OAuthService::class.java)
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

                    toMain()
                }
            }
            override fun onFailure(call: Call<OAuthResponse>, t: Throwable) {
                Log.e("LogInService: ", "$t")
            }
        })
    }

    private fun toMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    private fun toLogIn() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }
}