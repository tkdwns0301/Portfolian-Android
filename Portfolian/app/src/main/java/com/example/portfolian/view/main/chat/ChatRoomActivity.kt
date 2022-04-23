package com.example.portfolian.view.main.chat

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.portfolian.R
import com.example.portfolian.adapter.ChatAdapter
import com.example.portfolian.data.ChatModel
import com.example.portfolian.databinding.ActivityChatroomBinding
import com.example.portfolian.network.SocketApplication
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.util.*


class ChatRoomActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChatroomBinding

    private lateinit var mSocket: Socket
    private lateinit var send: ImageButton
    private lateinit var chattingText: EditText
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipe: SwipeRefreshLayout

    private var arrayList = arrayListOf<ChatModel>()
    private val mAdapter = ChatAdapter(this, arrayList)

    private var chatRoomId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        initRetrofit()
        initView()
    }

    private fun initRetrofit() {

    }

    private fun initView() {
        toolbar = binding.toolbarChat
        send = binding.btnSend
        chattingText = binding.etMessage
        recyclerView = binding.rvChatList
        swipe = binding.slSwipe

        initToolbar()
        initSocket()
    }

    private fun initToolbar() {
        toolbar = binding.toolbarChat

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.chat_Delete -> {
                    true
                }
                R.id.userProfile -> {
                    true
                }

                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initSocket() {
        recyclerView.adapter = mAdapter
        val lm = LinearLayoutManager(this)
        recyclerView.layoutManager = lm
        recyclerView.setHasFixedSize(true)

        mSocket = SocketApplication.getSocket()

        mSocket.on("chat:receive", onNewMessage)

        send.setOnClickListener {
            sendMessage()
            chattingText.setText("")
        }
    }

    private fun sendMessage() {
        val jsonObject = JSONObject()

        chatRoomId = intent.getStringExtra("chatRoomId").toString()

        jsonObject.put("messageContent", "${chattingText.text}")
        jsonObject.put("roomId", "$chatRoomId")

        mSocket.emit("chat:send", jsonObject)

    }

    private var onNewMessage: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val message = args[0]

            val chat = ChatModel("123", "상준", "$message", Date(System.currentTimeMillis()))
            mAdapter.addItem(chat)
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun refresh() {

        swipe.isRefreshing = false
    }

}