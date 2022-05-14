package com.example.portfolian.view.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.portfolian.R
import com.example.portfolian.databinding.ActivityMainBinding
import com.example.portfolian.network.SocketApplication
import com.example.portfolian.view.main.bookmark.BookmarkFragment
import com.example.portfolian.view.main.chat.ChatFragment
import com.example.portfolian.view.main.home.HomeFragment
import com.example.portfolian.view.main.user.UserFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.util.Utility

private const val TAG_HOME = "home_Fragment"
private const val TAG_BOOKMARK = "bookmark_Fragment"
private const val TAG_CHAT = "chat_Fragment"
private const val TAG_USER = "user_Fragment"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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
            Log.d(TAG, msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
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
            ft.hide(home)
        }
        if(bookmark != null) {
            ft.remove(bookmark)
        }
        if(chat != null) {
            ft.remove(chat)
        }
        if(user != null) {
            ft.hide(user)
        }

        if(tag == TAG_HOME) {
            if(home != null) {
                ft.show(home)
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
                ft.show(user)
            }
        }

        ft.commitAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()

        SocketApplication.closeConnection()
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}
