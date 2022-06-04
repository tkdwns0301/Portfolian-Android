package com.example.portfolian

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.portfolian.adapter.ChatAdapter
import com.example.portfolian.data.ChatModel
import com.example.portfolian.data.KakaoTokenRequest
import com.example.portfolian.data.OAuthResponse
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.network.SocketApplication
import com.example.portfolian.service.OAuthService
import com.example.portfolian.view.login.LogInActivity
import com.example.portfolian.view.login.LogInFragment
import com.example.portfolian.view.main.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import io.socket.emitter.Emitter
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.time.LocalDateTime

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

                    val mSocket = SocketApplication.mSocket

                    mSocket.on("connection") {
                        SocketApplication.sendUserId()
                        //mSocket.on("chat:receive", onNewMessage)
                        toMain()
                    }
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
        finish()
    }

    private fun toLogIn() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

//    private var onNewMessage: Emitter.Listener = Emitter.Listener { args ->
//        runOnUiThread {
//            Log.e("receive:", "zksdjflzkjdsf")
//            var builder = NotificationCompat.Builder(this,)
//                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
//                .setContentTitle("알림 제목")
//                .setContentText("알림 내용")
//
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channelId = "Portfolian_Channel"
//                val channelName ="Portfolian"
//                val descriptionText = "포트폴리안 알림을 위한 채널입니다."
//                val importance = NotificationManager.IMPORTANCE_DEFAULT
//                val channel = NotificationChannel(channelId, channelName, importance).apply {
//                    description = descriptionText
//                }
//
//                val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                notificationManager.createNotificationChannel(channel)
//
//                notificationManager.notify(1002 , builder.build())
//            }
//
//        }
//    }
}