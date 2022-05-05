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
import org.json.JSONObject
import java.time.format.DateTimeFormatter
import java.util.*

class ChatAdapter(
    val context: Context,
    val arrayList: ArrayList<ChatModel>,
    val roomId: String,
    val photo: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var photoFlag = true
    private var svFlag = false

    fun addItem(item: ChatModel) {
        if (arrayList != null) {
            arrayList.add(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View

        return if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.item_my_chat, parent, false)
            Holder(view)
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_your_chat, parent, false)

            val jsonObject = JSONObject()
            jsonObject.put("userId", "${GlobalApplication.prefs.userId}")
            jsonObject.put("roomId", "$roomId")
            SocketApplication.mSocket.emit("chat:read", jsonObject)

            Holder2(view)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (viewHolder is Holder) {
            photoFlag = false
            svFlag = true

            Log.e("photoFlag: ", "$photoFlag")

            val message = arrayList[i].message
            viewHolder.chatText?.text = message

            var time = arrayList[i].date
            val formatter = DateTimeFormatter.ofPattern("HH : mm")
            val date = time.format(formatter)

            viewHolder.chatTime?.text = date
        } else if (viewHolder is Holder2) {
            if(photoFlag) {
                photoFlag= !photoFlag
            }

            if(svFlag && !photoFlag) {
                photoFlag = true
            } else if(!svFlag && photoFlag) {
                photoFlag = false
            }

            svFlag = false

            Log.e("photoFlag: ", "$photoFlag")

            if (photoFlag) {
                Glide.with(viewHolder.itemView.context)
                    .load(photo)
                    .into(viewHolder.profile)
            }

            var time = arrayList[i].date
            val formatter = DateTimeFormatter.ofPattern("HH : mm")
            val date = time.format(formatter)
            viewHolder.chatText?.text = arrayList[i].message
            viewHolder.chatTime?.text = date
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
        return if (arrayList[position].sender == "${GlobalApplication.prefs.userId}") {
            1
        } else {
            2
        }
    }
}