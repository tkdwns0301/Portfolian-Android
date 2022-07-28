
package com.hand.portfolian.network

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

object SocketApplication {
    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket() {
        try {
            mSocket = IO.socket("https://api.portfolian.site:443" )
        } catch (e: URISyntaxException) {
            Log.e("SocketApplication: ", "$e")
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
        Log.e("socket:", "connect!!!")
    }

    @Synchronized
    fun closeConnection() {
        Log.e("socket:", "disconnect!!!")
        mSocket.disconnect()
    }

    @Synchronized
    fun sendUserId() {
        Log.e("sendUserId", "userid")
        val jsonObject = JSONObject()
        jsonObject.put("userId", "${GlobalApplication.prefs.userId}")

        mSocket.emit("auth", jsonObject)
    }
}