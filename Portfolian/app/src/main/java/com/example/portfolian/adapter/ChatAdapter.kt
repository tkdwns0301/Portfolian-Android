package com.example.portfolian.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.portfolian.R
import com.example.portfolian.data.ChatModel
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.SocketApplication
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ChatAdapter (val context: Context, val arrayList: ArrayList<ChatModel>, val roomId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun addItem(item: ChatModel) {
        if(arrayList != null ) {
            arrayList.add(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val view: View

        return if(viewType == 1) {
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
        if(viewHolder is Holder) {
            val message = arrayList[i].message
            viewHolder.chatText?.text = message

            var time = arrayList[i].date
            val formatter = DateTimeFormatter.ofPattern("HH : mm")
            val date = time.format(formatter)

            viewHolder.chatTime?.text = date
        }
        else if(viewHolder is Holder2) {
            viewHolder.chatText?.text = arrayList[i].message
            viewHolder.chatTime?.text = arrayList[i].date.toString()
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatText = itemView.findViewById<TextView>(R.id.tv_MyMessage)
        val chatTime = itemView.findViewById<TextView>(R.id.tv_MyTime)
    }

    inner class Holder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatText = itemView.findViewById<TextView>(R.id.tv_YourMessage)
        val chatTime = itemView.findViewById<TextView>(R.id.tv_YourTime)
    }


    override fun getItemViewType(position: Int): Int {
        return if(arrayList[position].sender == "${GlobalApplication.prefs.userId}") {
            1
        } else {
            2
        }
    }
}