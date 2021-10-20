package com.example.portfilian.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.portfilian.R

class NicknameFragment : Fragment(R.layout.fragment_nickname) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initView()
    }

    private fun initView() {
        initNextButton()
    }

    private fun initNextButton() {
        val btn_Next = requireView().findViewById<ImageButton>(R.id.img_btn_Next)
        btn_Next.setOnClickListener {
            initNickname()
        }
    }

    private fun initNickname() {
        val et_Nickname = requireView().findViewById<EditText>(R.id.et_Nickname)

        if(et_Nickname.text.isNullOrBlank()) {
            Toast.makeText(activity, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            Log.e("NicknameFragment:: ", "Null Or Blank")
        } else {
            //TODO 정상적으로 메인페이지 넘어가기
            Log.d("NicknameFragment:: ", "SUCCESS")
        }
    }
}