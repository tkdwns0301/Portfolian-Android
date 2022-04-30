package com.example.portfolian.view.main.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.portfolian.R
import com.example.portfolian.adapter.ChatAdapter
import com.example.portfolian.adapter.ChatListAdapter
import com.example.portfolian.data.ChatRoom
import com.example.portfolian.data.ReadChatList
import com.example.portfolian.databinding.FragmentChatBinding
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.network.SocketApplication
import com.example.portfolian.service.ChatService
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
    private lateinit var swipe: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        init()

        val mSocket = SocketApplication.mSocket

        Log.e("connected", "${mSocket.connected()}")
        return binding.root
    }

    private fun init() {
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
        swipe = binding.slSwipe

        initRecyclerView()
        initToolbar()
        initSwpieRefreshLayout()
    }

    private fun initToolbar() {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.toolbar_Alert -> {
                    SocketApplication.sendUserId()
                    Log.e("버튼 눌렸어요!!!!", "!!!!")
                    true;
                }

                else -> {
                    super.onOptionsItemSelected(it)
                }
            }

        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        readChatList()
    }

    private fun initSwpieRefreshLayout() {
        swipe.setOnRefreshListener {
            refresh()
        }
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

    private fun refresh() {
        readChatList()
        swipe.isRefreshing = false
    }




}