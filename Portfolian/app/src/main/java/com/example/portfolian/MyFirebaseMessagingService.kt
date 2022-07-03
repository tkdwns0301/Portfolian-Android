package com.example.portfolian

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.portfolian.data.SendFCMTokenRequest
import com.example.portfolian.data.SendFCMTokenResponse
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.TokenService
import com.example.portfolian.view.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if(remoteMessage.data != null) {
            Log.e("data", "${remoteMessage.data}")
        }


        if (remoteMessage.notification!!.title!!.isNotEmpty() && remoteMessage.notification!!.body!!.isNotEmpty()) {
            sendNotification(
                remoteMessage.notification!!.title!!,
                remoteMessage.notification!!.body!!
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val retrofit = RetrofitClient.getInstance()
        val tokenService = retrofit.create(TokenService::class.java)

        val fcmToken = SendFCMTokenRequest(token)
        val sendToken = tokenService.sendFCMToken("Bearer ${GlobalApplication.prefs.accessToken}", "${GlobalApplication.prefs.userId}", fcmToken)

        sendToken.enqueue(object: Callback<SendFCMTokenResponse> {
            override fun onResponse(
                call: Call<SendFCMTokenResponse>,
                response: Response<SendFCMTokenResponse>
            ) {
                if(response.isSuccessful) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message

                    Log.e("sendToken: ", "$code: $message")
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

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = description
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.setShowBadge(true)
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_icon))
            .setSmallIcon(R.mipmap.ic_icon)
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