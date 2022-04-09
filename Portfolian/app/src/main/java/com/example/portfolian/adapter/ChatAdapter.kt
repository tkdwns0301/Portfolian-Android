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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ChatAdapter (val context: Context, val arrayList: ArrayList<ChatModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            Holder2(view)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if(viewHolder is Holder) {
            val message = arrayList[i].message.split("\"")[3]
            viewHolder.chatText?.text = message

            var time = arrayList[i].date
            val formatter = SimpleDateFormat("HH : mm")
            val date = formatter.format(time)
            //val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH : mm")
            //val date: LocalDate = LocalDate.parse(time, formatter)

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
        return 1
    }
}