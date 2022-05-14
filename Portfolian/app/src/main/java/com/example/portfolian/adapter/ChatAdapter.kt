package com.example.portfolian.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.portfolian.R
import com.example.portfolian.data.ChatModel
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.SocketApplication
import de.hdodenhof.circleimageview.CircleImageView
import io.socket.client.Socket
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChatAdapter(
    val context: Context,
    val arrayList: ArrayList<ChatModel>,
    val roomId: String,
    val photo: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var photoFlag = true
    private lateinit var mSocket: Socket

    fun addItem(item: ChatModel) {
        if (arrayList != null) {
            arrayList.add(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mSocket = SocketApplication.getSocket()
        val view: View

        return if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.item_my_chat, parent, false)
            Holder(view)
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_your_chat, parent, false)

            Holder2(view)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (viewHolder is Holder) {
            photoFlag = true

            val message = arrayList[i].messageContent
            viewHolder.chatText?.text = message

            var time = arrayList[i].date.substring(11 until 16)

            viewHolder.chatTime?.text = time

        } else if (viewHolder is Holder2) {

            if (photoFlag) {

                Glide.with(viewHolder.itemView.context)
                    .load(photo)
                    .into(viewHolder.profile)

                photoFlag = false
            }

            var time = arrayList[i].date.substring(11 until 16)

            viewHolder.chatText?.text = arrayList[i].messageContent
            viewHolder.chatTime?.text = time

            if (i == arrayList.size - 1) {
                val jsonObject = JSONObject()

                jsonObject.put("chatRoomId", "$roomId")
                Log.e("chatRoomId: ", "$roomId")
                jsonObject.put("userId", "${GlobalApplication.prefs.userId}")

                mSocket.emit("chat:read", jsonObject)
            }
        }
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val chatText = view.findViewById<TextView>(R.id.tv_MyMessage)
        val chatTime = view.findViewById<TextView>(R.id.tv_MyTime)
    }

    class Holder2(view: View) : RecyclerView.ViewHolder(view) {
        val profile = view.findViewById<CircleImageView>(R.id.cv_YourProfile)
        val chatText = view.findViewById<TextView>(R.id.tv_YourMessage)
        val chatTime = view.findViewById<TextView>(R.id.tv_YourTime)
    }


    override fun getItemViewType(position: Int): Int {
        return if(arrayList[position].messageType == "Notice") {
            3
        } else {
            if (arrayList[position].sender == "${GlobalApplication.prefs.userId}") {
                1
            } else {
                2
            }
        }

    }
}