package com.example.portfolian.view.main.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.portfolian.R
import com.example.portfolian.adapter.ChatAdapter
import com.example.portfolian.adapter.ChatListAdapter
import com.example.portfolian.data.ChatRoom
import com.example.portfolian.data.ReadChatList
import com.example.portfolian.databinding.FragmentChatBinding
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.ChatService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class ChatFragment : Fragment(R.layout.fragment_chat) {
    private lateinit var binding: FragmentChatBinding

    private lateinit var retrofit: Retrofit
    private lateinit var chatService: ChatService

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.inflate(layoutInflater)

        init(view)
    }

    private fun init(view: View) {
        initRetrofit()
        initView(view)
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        chatService = retrofit.create(ChatService::class.java)
    }

    private fun initView(view: View) {
        initRecyclerView(view)
    }

    private fun initRecyclerView(view: View) {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView = view.findViewById(R.id.rv_ChatList)

        recyclerView.layoutManager = layoutManager
        readChatList()
    }


    private fun readChatList() {
        val callChatList = chatService.readChatList("Bearer ${GlobalApplication.prefs.accessToken}")

        callChatList.enqueue(object: Callback<ReadChatList> {
            override fun onResponse(call: Call<ReadChatList>, response: Response<ReadChatList>) {
                if(response.isSuccessful) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message
                    val chatRoomList = response.body()!!.chatRoomList

                    Log.d("callChatList: ", "$code, $message")
                    setChatListAdapter(chatRoomList)
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
            adapter.notifyDataSetChanged()
        }
    }






}