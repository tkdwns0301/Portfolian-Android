package com.example.portfolian.view.main.chat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.portfolian.R
import com.example.portfolian.network.SocketApplication
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter

class ChatRoomActivity: AppCompatActivity() {
    private lateinit var mSocket: Socket;

    private lateinit var send: Button


    val gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        init()
    }

    private fun init() {
        initSocket()
        initSend()
    }

    private fun initSocket() {
        mSocket = SocketApplication.get()
        mSocket.connect()

        mSocket.on(Socket.EVENT_CONNECT, onConnect)



    }

    private fun initSend() {
        send = findViewById(R.id.btn_Send)

        send.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val content = findViewById<EditText>(R.id.et_Send).text.toString()
        val jsonData = gson.toJson(content)

    }

    var onConnect = Emitter.Listener {
        //val sendData = DetailData("홍길동")

    }

}