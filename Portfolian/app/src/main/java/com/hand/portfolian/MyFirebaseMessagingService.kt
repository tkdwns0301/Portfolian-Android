package com.hand.portfolian

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.hand.portfolian.data.SendFCMTokenRequest
import com.hand.portfolian.data.SendFCMTokenResponse
import com.hand.portfolian.network.GlobalApplication
import com.hand.portfolian.network.RetrofitClient
import com.hand.portfolian.service.TokenService
import com.hand.portfolian.view.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.notification!!.title!!.isNotEmpty() && remoteMessage.notification!!.body!!.isNotEmpty()) {

            if (!remoteMessage.notification!!.title.equals(GlobalApplication.prefs.chatTitle)) {
                sendNotification(
                    remoteMessage.notification!!.title!!,
                    remoteMessage.notification!!.body!!
                )
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val retrofit = RetrofitClient.getInstance()
        val tokenService = retrofit.create(TokenService::class.java)

        val fcmToken = SendFCMTokenRequest(token)
        val sendToken = tokenService.sendFCMToken(
            "Bearer ${GlobalApplication.prefs.accessToken}",
            "${GlobalApplication.prefs.userId}",
            fcmToken
        )

        sendToken.enqueue(object : Callback<SendFCMTokenResponse> {
            override fun onResponse(
                call: Call<SendFCMTokenResponse>,
                response: Response<SendFCMTokenResponse>
            ) {
                if (response.isSuccessful) {

                }
            }

            override fun onFailure(call: Call<SendFCMTokenResponse>, t: Throwable) {
                Log.e("sendToken: ", "$t")
            }
        })


    }


    private fun sendNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val channelId = "portforlianNotification"
        val channelName = "portfolianChannel"
        val description = "This is Portfolian Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = description
            channel.enableVibration(true)
            channel.setShowBadge(true)
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_noti_icon2)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {

        private const val TAG = "PortfolianFCMService"
    }
}