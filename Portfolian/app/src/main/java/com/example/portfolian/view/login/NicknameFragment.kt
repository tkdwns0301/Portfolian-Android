package com.example.portfolian.view.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.portfolian.R

class NicknameFragment : Fragment(R.layout.fragment_nickname) {
    private lateinit var navController: NavController
    private lateinit var btn_Next: ImageButton
    private lateinit var et_Nickname: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        init()
    }

    private fun init() {
        initView()
    }

    private fun initView() {
        initNextButton()
    }

    private fun initNextButton() {
        btn_Next = requireView().findViewById(R.id.img_btn_Next)
        btn_Next.setOnClickListener {
            initNickname()
        }
    }

    private fun initNickname() {
        et_Nickname = requireView().findViewById(R.id.et_Nickname)

        if(et_Nickname.text.isNullOrBlank()) {
            Toast.makeText(activity, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            Log.e("NicknameFragment:: ", "Null Or Blank")
        } else {
            //TODO 정상적으로 메인페이지 넘어가기
            Log.d("NicknameFragment:: ", "SUCCESS")
            navController.navigate(R.id.action_nicknameFragment_to_mainActivity)
            activity?.finish()
        }
    }
}