package com.example.portfolian.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.portfolian.R
import com.example.portfolian.data.ChatListData
import com.example.portfolian.data.ChatRoom
import com.example.portfolian.data.ReadChatResponse
import com.example.portfolian.data.RemoveChatResponse
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.ChatService
import com.example.portfolian.view.main.chat.ChatRoomActivity
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat

class ChatListAdapter(
    private val context: Context,
    private val dataSet: ArrayList<ChatRoom>,
    private val state: Int
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {
    private lateinit var retrofit: Retrofit
    private lateinit var chatService: ChatService


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_chat_item, parent, false)
        initRetrofit()
        return ViewHolder(view)
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        chatService = retrofit.create(ChatService::class.java)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatRoom = dataSet[position]
        if (chatRoom.user.photo.isEmpty()) {
            holder.photo.setImageDrawable(context.getDrawable(R.drawable.avatar_1_raster))
        } else {
            Glide.with(holder.itemView.context)
                .load(chatRoom.user.photo)
                .into(holder.photo)
        }

        holder.nickname.text = "${chatRoom.user.nickName}"
        holder.lastMessage.text = "${chatRoom.newChatContent}"
        holder.title.text = "${chatRoom.projectTitle}"

        if (chatRoom.newChatCnt == 0) {
            holder.badgeLayout.isVisible = false
        } else {
            holder.badgeLayout.isVisible = true
            holder.badge.text = "${chatRoom.newChatCnt}"
        }

        holder.badge.text = "${chatRoom.newChatCnt}"

        val formatter = SimpleDateFormat("MM월 dd일")
        val date = formatter.format(chatRoom.newChatDate)
        holder.date.text = date.toString()

        holder.container.setOnClickListener {
            moveChat(chatRoom.chatRoomId, chatRoom.user.userId, chatRoom.user.photo, chatRoom.projectTitle, chatRoom.user.nickName)
        }

        holder.remove.setOnClickListener {
            removeData(position, chatRoom.chatRoomId)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    private fun moveChat(chatRoomId: String, receiver: String, photo: String, title: String, nickName: String) {
        val callChat = chatService.readChat("Bearer ${GlobalApplication.prefs.accessToken}", "$chatRoomId")
        callChat.enqueue(object : Callback<ReadChatResponse> {
            override fun onResponse(
                call: Call<ReadChatResponse>,
                response: Response<ReadChatResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("ChatList: ", "${response.body()!!.chatList}")
                    ChatListData.oldChatList = response.body()!!.chatList.oldChatList
                    ChatListData.newChatList = response.body()!!.chatList.newChatList

                    Log.e("oldChatList: ", "${ChatListData.oldChatList}")
                    Log.e("newChatList: ", "${ChatListData.newChatList}")

                    val intent = Intent(context, ChatRoomActivity::class.java)
                    intent.putExtra("chatRoomId", "$chatRoomId")
                    intent.putExtra("receiver", "$receiver")
                    intent.putExtra("photo", "$photo")
                    intent.putExtra("title", "$title")
                    intent.putExtra("nickName", "$nickName")
                    context.startActivity(intent)

                    Log.e("moveChat: ", "success")
                }
            }

            override fun onFailure(call: Call<ReadChatResponse>, t: Throwable) {
                Log.e("moveChat:", "$t")
            }

        })
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: ConstraintLayout = view.findViewById(R.id.cl_ChatItem)
        val photo: CircleImageView = view.findViewById(R.id.cv_ChatProfile)
        val nickname: TextView = view.findViewById(R.id.tv_ChatNickname)
        val lastMessage: TextView = view.findViewById(R.id.tv_LastMessage)
        val date: TextView = view.findViewById(R.id.tv_ChatDate)
        val title: TextView = view.findViewById(R.id.tv_Title)
        val badge: TextView = view.findViewById(R.id.tv_Badge)
        val badgeLayout: ConstraintLayout = view.findViewById(R.id.cl_Badge)
        val remove: TextView = view.findViewById(R.id.tv_Remove)
    }

    private fun removeData(position: Int, chatRoomId: String) {
        Log.e("position: ", "$position")
        val removeChat = chatService.removeChat("Bearer ${GlobalApplication.prefs.accessToken}", "$chatRoomId")
        removeChat.enqueue(object : Callback<RemoveChatResponse> {
            override fun onResponse(
                call: Call<RemoveChatResponse>,
                response: Response<RemoveChatResponse>
            ) {
                if (response.isSuccessful) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message
                    notifyDataSetChanged()
                    Log.e("RemoveChat: ", "$code: $message")
                }
            }

            override fun onFailure(call: Call<RemoveChatResponse>, t: Throwable) {
                Log.e("moveChat:", "$t")
            }

        })
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }
}