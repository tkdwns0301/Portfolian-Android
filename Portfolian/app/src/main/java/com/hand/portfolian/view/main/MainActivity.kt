package com.hand.portfolian.view.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.hand.portfolian.R
import com.hand.portfolian.data.SendFCMTokenRequest
import com.hand.portfolian.data.SendFCMTokenResponse
import com.hand.portfolian.databinding.ActivityMainBinding
import com.hand.portfolian.network.GlobalApplication
import com.hand.portfolian.network.RetrofitClient
import com.hand.portfolian.network.SocketApplication
import com.hand.portfolian.service.TokenService
import com.hand.portfolian.view.main.bookmark.BookmarkFragment
import com.hand.portfolian.view.main.chat.ChatFragment
import com.hand.portfolian.view.main.home.HomeFragment
import com.hand.portfolian.view.main.user.UserFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.util.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

private const val TAG_HOME = "home_Fragment"
private const val TAG_BOOKMARK = "bookmark_Fragment"
private const val TAG_CHAT = "chat_Fragment"
private const val TAG_USER = "user_Fragment"

class MainActivity : AppCompatActivity(), LifecycleObserver{
    private lateinit var binding: ActivityMainBinding
    private lateinit var retrofit: Retrofit
    private lateinit var tokenService: TokenService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        SocketApplication.getSocket().off("connection")
        initRetrofit()

        val keyHash = Utility.getKeyHash(this)

        setFragment(TAG_HOME, HomeFragment())

        binding.bnMain.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment-> setFragment(TAG_HOME, HomeFragment())
                R.id.bookmarkFragment -> setFragment(TAG_BOOKMARK, BookmarkFragment())
                R.id.chatFragment -> setFragment(TAG_CHAT, ChatFragment())
                R.id.userFragment -> setFragment(TAG_USER, UserFragment())
            }
            true

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = "Portfolian_Channel"
            val channelName = "Portfolian"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_DEFAULT)
            )
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            sendFCMToken(token)
        })

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onAppBackgrounded() {
        SocketApplication.getSocket().off("chat:receive")
        SocketApplication.getSocket().off("connection")
        SocketApplication.closeConnection()

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onAppForegrounded() {

        if (!SocketApplication.getSocket().connected())
            SocketApplication.establishConnection()

        SocketApplication.getSocket().on("connection") {
            SocketApplication.sendUserId()
        }
    }


    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        tokenService = retrofit.create(TokenService::class.java)
    }

    private fun sendFCMToken(token: String) {
        val fcmToken = SendFCMTokenRequest(token)
        val sendToken = tokenService.sendFCMToken("Bearer ${GlobalApplication.prefs.accessToken}", "${GlobalApplication.prefs.userId}", fcmToken)

        sendToken.enqueue(object: Callback<SendFCMTokenResponse> {
            override fun onResponse(
                call: Call<SendFCMTokenResponse>,
                response: Response<SendFCMTokenResponse>
            ) {
                if(response.isSuccessful) {

                }
            }

            override fun onFailure(call: Call<SendFCMTokenResponse>, t: Throwable) {
                Log.e("sendToken: ", "$t")
            }
        })

    }
    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = manager.beginTransaction()

        if(manager.findFragmentByTag(tag) == null) {
            ft.add(R.id.fg_MainNavContainer, fragment, tag)
        }

        val home = manager.findFragmentByTag(TAG_HOME)
        val bookmark = manager.findFragmentByTag(TAG_BOOKMARK)
        val chat = manager.findFragmentByTag(TAG_CHAT)
        val user = manager.findFragmentByTag(TAG_USER)

        if(home != null) {
            ft.remove(home)
        }
        if(bookmark != null) {
            ft.remove(bookmark)
        }
        if(chat != null) {
            ft.remove(chat)
        }
        if(user != null) {
            ft.remove(user)
        }

        if(tag == TAG_HOME) {
            if(home != null) {
                ft.add(R.id.fg_MainNavContainer, fragment, tag)
            }
        }
        else if(tag == TAG_BOOKMARK) {
            if(bookmark != null) {
                ft.add(R.id.fg_MainNavContainer, fragment, tag)
            }

        }
        else if(tag == TAG_CHAT) {
            if(chat != null) {
                ft.add(R.id.fg_MainNavContainer, fragment, tag)
            }
        }
        else if(tag == TAG_USER) {
            if(user != null) {
                ft.add(R.id.fg_MainNavContainer, fragment, tag)
            }
        }

        ft.commitAllowingStateLoss()
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}
