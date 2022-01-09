package com.example.portfolian.network

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class SocketApplication {
    companion object {
        private lateinit var socket: Socket
        fun get(): Socket {
            try {
                socket = IO.socket("http://3.36.84.11:3001/")
            } catch (e: URISyntaxException) {
                Log.e("SocketApplication: ", "$e")
            }

            return socket
        }
    }
}