package com.example.portfolian.view.main.chat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import com.example.portfolian.R


class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var newChat: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
    }

    private fun init(view: View) {
        initButton(view)
    }

    private fun initButton(view: View) {
        newChat = view.findViewById(R.id.btn_NewChat)
        newChat.setOnClickListener {
            val intent = Intent(activity, ChatRoomActivity::class.java)
            startActivity(intent)
        }
    }




}