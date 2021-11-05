package com.example.portfolian.view.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.portfolian.R

class LogInFragment  : Fragment(R.layout.fragment_login) {
    private lateinit var navController: NavController
    private lateinit var btn_Google: ImageButton
    private lateinit var btn_Kakao: ImageButton
    private lateinit var btn_Git: ImageButton


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        init()
    }

    private fun init() {
        initView()
    }

    private fun initView() {
        initGoogle()
        initKako()
        initGit()
    }

    private fun initGoogle() {
        btn_Google = requireView().findViewById(R.id.btn_Google)
        btn_Google.setOnClickListener {
            //TODO google 로그인 실행
            Log.d("LogInFragment:: ", "btn_Google: Clicked")
            nickname()
        }
    }

    private fun initKako() {
        btn_Kakao = requireView().findViewById(R.id.btn_Kakao)
        btn_Kakao.setOnClickListener {
            //TODO Kakao 로그인 실행
            Log.d("LogInFragment:: ", "btn_Kakao: Clicked")
        }
    }

    private fun initGit() {
        btn_Git= requireView().findViewById(R.id.btn_Git)
        btn_Git.setOnClickListener {
            //TODO Git 로그인 실행
            Log.d("LogInFragment:: ", "btn_Git: Clicked")
        }
    }

    private fun nickname() {
        navController.navigate(R.id.action_logInFragment_to_nicknameFragment)
    }
}