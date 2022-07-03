package com.example.portfolian.view.main.chat

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.portfolian.R
import com.example.portfolian.adapter.ChatAdapter
import com.example.portfolian.data.ChatModel
import com.example.portfolian.data.ReadChatResponse
import com.example.portfolian.databinding.ActivityChatroomBinding
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.network.SocketApplication
import com.example.portfolian.service.ChatService
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ChatRoomActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChatroomBinding

    private lateinit var retrofit: Retrofit
    private lateinit var chatService: ChatService

    private lateinit var mSocket: Socket
    private lateinit var send: ImageButton
    private lateinit var chattingText: EditText
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var title: TextView

    private lateinit var mAdapter : ChatAdapter

    private var arrayList = ArrayList<ChatModel>()
    private var oldChatList = ArrayList<ChatModel>()
    private var newChatList = ArrayList<ChatModel>()

    private var chatRoomId = ""
    private var photo = ""
    private var receiver = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatRoomId = intent.getStringExtra("chatRoomId").toString()
        receiver = intent.getStringExtra("receiver").toString()



        initRetrofit()
        //readChat()
    }

    override fun onResume() {
        super.onResume()
        readChat()
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalApplication.prefs.chatTitle = ""
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        chatService = retrofit.create(ChatService::class.java)
    }

    private fun initView() {
        Log.e("initView", "view")
        toolbar = binding.toolbarChat
        toolbarTitle = binding.tvYourName

        send = binding.btnSend
        chattingText = binding.etMessage
        recyclerView = binding.rvChatList
        title = binding.tvTitle

        photo = intent.getStringExtra("photo").toString()
        title.text = intent.getStringExtra("title")


        if(oldChatList.size != 0) {
            arrayList = oldChatList!!

            if(oldChatList[oldChatList.size-1].messageType != "Notice")
                arrayList.add(ChatModel("", "여기까지 읽으셨습니다.", "Notice","", "", ""))
        }

        mAdapter = ChatAdapter(this, arrayList, chatRoomId, photo, receiver)

        recyclerView.adapter = mAdapter
        val lm = LinearLayoutManager(this)
        recyclerView.layoutManager = lm
        recyclerView.setHasFixedSize(true)



        if(newChatList != null) {

            for (newChat in newChatList) {
                mAdapter.addItem(newChat)
            }
        }

        mAdapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(arrayList.size-1)


        initToolbar()
        initSocket()
    }

    private fun initToolbar() {
        toolbarTitle.text = intent.getStringExtra("nickName")
        GlobalApplication.prefs.chatTitle = toolbarTitle.text.toString()
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
            GlobalApplication.prefs.chatTitle = ""
            finish()
        }
    }

    private fun initSocket() {
        mSocket = SocketApplication.getSocket()

        mSocket.on("chat:receive", onNewMessage)

        send.setOnClickListener {
            sendMessage()
            chattingText.setText("")
        }
        val jsonObject = JSONObject()

        jsonObject.put("roomId", "$chatRoomId")
        jsonObject.put("userId", "${GlobalApplication.prefs.userId}")

        mSocket.emit("chat:read", jsonObject)
    }

    private fun readChat() {
        val callChat = chatService.readChat("Bearer ${GlobalApplication.prefs.accessToken}", "$chatRoomId")
        callChat.enqueue(object : Callback<ReadChatResponse> {
            override fun onResponse(
                call: Call<ReadChatResponse>,
                response: Response<ReadChatResponse>
            ) {
                if (response.isSuccessful) {
                    oldChatList = response.body()!!.chatList.oldChatList
                    newChatList = response.body()!!.chatList.newChatList

                    Log.e("oldChatList: ", "$oldChatList")
                    Log.e("newChatList: ", "$newChatList")

                    initView()

                }
            }

            override fun onFailure(call: Call<ReadChatResponse>, t: Throwable) {
                Log.e("moveChat:", "$t")
            }

        })
    }

    private fun sendMessage() {
        val jsonObject = JSONObject()

        val receiver = intent.getStringExtra("receiver").toString()
        jsonObject.put("chatRoomId", "$chatRoomId")
        jsonObject.put("messageContent", "${chattingText.text}")
        jsonObject.put("messageType", "Chat")
        jsonObject.put("sender", "${GlobalApplication.prefs.userId}")
        jsonObject.put("receiver", "$receiver")

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val formatted = current.format(formatter)

        jsonObject.put("date", "$formatted")

        val chat = ChatModel("$chatRoomId", "${chattingText.text}", "", "${GlobalApplication.prefs.userId}", "$receiver", "$formatted")

        mSocket.emit("chat:send", jsonObject)

        mAdapter.addItem(chat)
        mAdapter.notifyDataSetChanged()

        recyclerView.smoothScrollToPosition(arrayList.size-1)
    }

    private var onNewMessage: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val jsonObject = JSONObject(args[0].toString())


            val roomId = jsonObject.get("chatRoomId")
            val message = jsonObject.get("messageContent")
            val messageType = jsonObject.get("messageType")
            val sender = jsonObject.get("sender")
            val receiver = jsonObject.get("sender")
            val date = jsonObject.get("date")

            val chat = ChatModel("$roomId", "$message", "$messageType","$sender", "$receiver", "$date")

            if(roomId.equals(chatRoomId)) {
                mAdapter.addItem(chat)
                mAdapter.notifyDataSetChanged()
                recyclerView.smoothScrollToPosition(arrayList.size - 1)
            }

        }
    }

}

