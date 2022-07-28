package com.hand.portfolian.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hand.portfolian.R
import com.hand.portfolian.data.ChatModel
import com.hand.portfolian.network.GlobalApplication
import com.hand.portfolian.network.SocketApplication
import com.hand.portfolian.view.main.user.OtherActivity
import de.hdodenhof.circleimageview.CircleImageView
import io.socket.client.Socket
import org.json.JSONObject
import java.util.*

class ChatAdapter(
    val context: Context,
    val arrayList: ArrayList<ChatModel>,
    val roomId: String,
    val photo: String,
    val receiver: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var mSocket: Socket

    fun addItem(item: ChatModel) {
        if (arrayList != null) {
            arrayList.add(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mSocket = SocketApplication.getSocket()
        val view: View

        if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.item_my_chat, parent, false)
            return Holder(view)
        } else if(viewType == 2){
            view = LayoutInflater.from(context).inflate(R.layout.item_your_chat, parent, false)

            return Holder2(view)
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_notice_chat, parent, false)
            return Holder3(view)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (viewHolder is Holder) {
            val message = arrayList[i].messageContent
            viewHolder.chatText?.text = message

            var time = arrayList[i].date.substring(11 until 16)

            viewHolder.chatTime?.text = time

        } else if (viewHolder is Holder2) {
            if(i==1) {
                Glide.with(viewHolder.itemView.context)
                    .load(photo)
                    .into(viewHolder.profile)
            }
            else {
                if(arrayList[i-1].sender == "${GlobalApplication.prefs.userId}") {
                    Glide.with(viewHolder.itemView.context)
                        .load(photo)
                        .into(viewHolder.profile)
                }

            }

            viewHolder.profile.setOnClickListener {
                val intent = Intent(context, OtherActivity::class.java)
                intent.putExtra("userId", "$receiver")
                context.startActivity(intent)
            }

            var time = arrayList[i].date.substring(11 until 16)

            viewHolder.chatText?.text = arrayList[i].messageContent
            viewHolder.chatTime?.text = time

            if (i == arrayList.size - 1) {
                val jsonObject = JSONObject()
                jsonObject.put("chatRoomId", "$roomId")
                jsonObject.put("userId", "${GlobalApplication.prefs.userId}")

                mSocket.emit("chat:read", jsonObject)
            }
        } else if(viewHolder is Holder3){
            viewHolder.noticeText.text = arrayList[i].messageContent
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

    class Holder3(view: View) : RecyclerView.ViewHolder(view) {
        val noticeText = view.findViewById<TextView>(R.id.tv_Notice)
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