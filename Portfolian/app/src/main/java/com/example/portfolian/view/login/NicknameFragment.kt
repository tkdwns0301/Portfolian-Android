package com.example.portfolian.view.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.portfolian.R
import com.example.portfolian.data.*
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.OAuthService
import com.example.portfolian.service.TokenService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class NicknameFragment : Fragment(R.layout.fragment_nickname) {
    private lateinit var retrofit: Retrofit
    private lateinit var nickNameService: OAuthService
    private lateinit var tokenService: TokenService

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
        initRetrofit()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        nickNameService = retrofit.create(OAuthService::class.java)
        tokenService = retrofit.create(TokenService::class.java)
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

        renewal()

        if (et_Nickname.text.isNullOrBlank()) {
            Toast.makeText(activity, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
        } else {
            //메인페이지로 이동
            val nickname = NickNameRequest(et_Nickname.text.toString())
            val nickName = nickNameService.setNickName(
                "Bearer ${GlobalApplication.prefs.accessToken}",
                GlobalApplication.prefs.userId,
                nickname
            )

            nickName.enqueue(object : Callback<NickNameResponse> {
                override fun onResponse(
                    call: Call<NickNameResponse>,
                    response: Response<NickNameResponse>
                ) {
                    if (response.body()!!.code == 1) {
                        navController.navigate(R.id.action_nicknameFragment_to_mainActivity)
                        activity?.finish()
                    } else {
                        Log.e("NickName: ", "닉네임설정오류: ${response.body()!!.message}")
                    }

                }

                override fun onFailure(call: Call<NickNameResponse>, t: Throwable) {
                    Log.e("NickName: ", "$t")
                }
            })

        }
    }

    private fun renewal() {
        val renewalTokenRequest = RenewalTokenRequest("${GlobalApplication.prefs.userId}")

        val renewalService = tokenService.getAccessToken(renewalTokenRequest)

        renewalService.enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if(response.isSuccessful) {
                    if(response.body()!!.code == 1) {
                        GlobalApplication.prefs.accessToken = response.body()!!.accessToken
                    }
                    else {
                        Log.e("RenewalToken: ", "토큰갱신 오류: ${response.body()!!.message}")
                    }

                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.e("RenewalToken: ", "$t")
            }
        })
    }
}