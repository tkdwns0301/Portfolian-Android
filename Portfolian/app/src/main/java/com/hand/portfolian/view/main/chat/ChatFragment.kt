package com.hand.portfolian.view.main.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hand.portfolian.R
import com.hand.portfolian.adapter.ChatListAdapter
import com.hand.portfolian.adapter.SwipeHelperCallback
import com.hand.portfolian.data.ChatRoom
import com.hand.portfolian.data.ReadChatList
import com.hand.portfolian.databinding.FragmentChatBinding
import com.hand.portfolian.network.GlobalApplication
import com.hand.portfolian.network.RetrofitClient
import com.hand.portfolian.network.SocketApplication
import com.hand.portfolian.service.ChatService
import io.socket.client.Socket
import io.socket.emitter.Emitter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var binding: FragmentChatBinding
    private lateinit var retrofit: Retrofit
    private lateinit var chatService: ChatService

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatListAdapter
    private lateinit var noneChat: TextView

    private lateinit var mSocket: Socket

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        init()


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        SocketApplication.getSocket().on("chat:receive", onNewMessage)
        readChatList()
    }

    private fun init() {
        mSocket = SocketApplication.getSocket()

        initRetrofit()
        initView()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        chatService = retrofit.create(ChatService::class.java)
    }

    private fun initView() {
        toolbar = binding.toolbarChatList
        recyclerView = binding.rvChatList
        noneChat = binding.tvNoneChat

        initRecyclerView()
    }



    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        readChatList()
    }





    private fun readChatList() {
        val callChatList = chatService.readChatList("Bearer ${GlobalApplication.prefs.accessToken}")

        callChatList.enqueue(object: Callback<ReadChatList> {
            override fun onResponse(call: Call<ReadChatList>, response: Response<ReadChatList>) {
                if(response.isSuccessful) {
                    val chatRoomList = response.body()!!.chatRoomList

                    if(chatRoomList.isNullOrEmpty()) {
                        noneChat.isVisible = true
                        recyclerView.isVisible = false
                    } else {
                        noneChat.isVisible = false
                        recyclerView.isVisible = true
                        setChatListAdapter(chatRoomList)
                    }
                }
            }

            override fun onFailure(call: Call<ReadChatList>, t: Throwable) {
                Log.e("callChatList: ", "$t")
            }
        })
    }

    private fun setChatListAdapter(chatRoomList: ArrayList<ChatRoom>) {
        if(chatRoomList != null) {
            adapter = ChatListAdapter(requireContext(), chatRoomList, 0)
            recyclerView.adapter = adapter

            val swipeHelperCallback = SwipeHelperCallback(adapter).apply {
                setClamp((resources.displayMetrics.widthPixels.toFloat() / 4))
            }
            ItemTouchHelper(swipeHelperCallback).attachToRecyclerView(recyclerView)

            recyclerView.setOnTouchListener {_, _ ->
                swipeHelperCallback.removePreviousClamp(recyclerView)
                false
            }

            adapter.notifyDataSetChanged()
        }
    }

    private var onNewMessage: Emitter.Listener = Emitter.Listener { args ->
        readChatList()
    }

}