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
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.SocketApplication
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.time.LocalDateTime


class ChatRoomActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChatroomBinding

    private lateinit var mSocket: Socket
    private lateinit var send: ImageButton
    private lateinit var chattingText: EditText
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var title: TextView

    private var arrayList = arrayListOf<ChatModel>()
    private lateinit var mAdapter : ChatAdapter

    private var chatRoomId = ""
    private var photo = ""

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
        toolbarTitle = binding.tvYourName

        send = binding.btnSend
        chattingText = binding.etMessage
        recyclerView = binding.rvChatList
        title = binding.tvTitle

        photo = intent.getStringExtra("photo").toString()
        chatRoomId = intent.getStringExtra("chatRoomId").toString()
        title.text = intent.getStringExtra("title")

        mAdapter = ChatAdapter(this, arrayList, chatRoomId, photo)

        initToolbar()
        initSocket()

    }

    private fun initToolbar() {
        toolbarTitle.text = intent.getStringExtra("nickName")

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

        val receiver = intent.getStringExtra("receiver").toString()
        jsonObject.put("messageContent", "${chattingText.text}")
        jsonObject.put("roomId", "$chatRoomId")
        jsonObject.put("sender", "${GlobalApplication.prefs.userId}")
        jsonObject.put("receiver", "$receiver")
        jsonObject.put("date", "${LocalDateTime.now()}")

        val chat = ChatModel("${chattingText.text}", "$chatRoomId", "${GlobalApplication.prefs.userId}", "$receiver", LocalDateTime.now())

        mSocket.emit("chat:send", jsonObject)

        mAdapter.addItem(chat)
        mAdapter.notifyDataSetChanged()

        recyclerView.smoothScrollToPosition(arrayList.size-1)
    }

    private var onNewMessage: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val jsonObject = JSONObject(args[0].toString())

            val message = jsonObject.get("messageContent")
            val roomId = jsonObject.get("roomId")
            val sender = jsonObject.get("sender")
            val receiver = jsonObject.get("sender")
            val date = LocalDateTime.parse("${jsonObject.get("date")}")

            val chat = ChatModel("$message", "$roomId", "$sender", "$receiver", date)

            mAdapter.addItem(chat)
            mAdapter.notifyDataSetChanged()
            recyclerView.smoothScrollToPosition(arrayList.size-1)

        }
    }

}