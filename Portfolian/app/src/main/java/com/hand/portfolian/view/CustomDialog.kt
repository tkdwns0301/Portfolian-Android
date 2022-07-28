package com.hand.portfolian.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.hand.portfolian.R
import com.hand.portfolian.data.*
import com.hand.portfolian.network.GlobalApplication
import com.hand.portfolian.network.RetrofitClient
import com.hand.portfolian.service.OAuthService
import com.hand.portfolian.service.TokenService
import com.hand.portfolian.view.login.LogInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class CustomDialog(context: Context, flag: Boolean) {
    private var mContext = context
    private lateinit var retrofit: Retrofit
    private lateinit var oauthService: OAuthService
    private lateinit var tokenService: TokenService

    private val dialog1 = Dialog(context)

    private var unlinkFlag = flag

    fun showDialog() {
        initRetrofit()

        dialog1.setContentView(R.layout.dialog_layout)
        dialog1.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog1.setCanceledOnTouchOutside(true)
        dialog1.setCancelable(true)
        dialog1.show()

        if (!unlinkFlag) {
            dialog1.findViewById<TextView>(R.id.tv_Title).text = "로그아웃 하시겠습니까?"
        } else {
            dialog1.findViewById<TextView>(R.id.tv_Title).text = "회원탈퇴 하시겠습니까?"
        }

        dialog1.findViewById<TextView>(R.id.btn_Yes).setOnClickListener {
            if (!unlinkFlag) {
                val logout = oauthService.setLogout("Bearer ${GlobalApplication.prefs.accessToken}")

                logout.enqueue(object : Callback<LogoutResponse> {
                    override fun onResponse(
                        call: Call<LogoutResponse>,
                        response: Response<LogoutResponse>
                    ) {
                        if (response.isSuccessful) {
                            val code = response.body()?.code
                            val message = response.body()?.message

                            Log.d("Logout:: ", "$code")
                            Log.d("Logout:: ", "$message")

                            GlobalApplication.prefs.accessToken = ""
                            GlobalApplication.prefs.userId = ""

                            if(GlobalApplication.prefs.loginStatus == 1) {
                                UserApiClient.instance.logout { error ->
                                    if (error != null) {
                                        Toast.makeText(mContext, "오류로 인해 로그아웃 실패하였습니다.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Log.e("Logout: ", "로그아웃 성공. SDK에서 토큰 삭제됨")
                                    }
                                }
                            } else {
                                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken("727850004794-5clt9m4h33ff0vqprfl104qlm6m4t32e.apps.googleusercontent.com")
                                    .requestServerAuthCode("727850004794-5clt9m4h33ff0vqprfl104qlm6m4t32e.apps.googleusercontent.com")
                                    .requestEmail()
                                    .build()

                                val googleSignInClient = GoogleSignIn.getClient(mContext, gso)

                                googleSignInClient!!.signOut()
                            }

                            sendFCMToken("")

                            GlobalApplication.prefs.loginStatus = 0

                            val intent = Intent(mContext, LogInActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            mContext.startActivity(intent)
                            dialog1.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                        Log.e("Logout:: ", "$t")
                    }
                })

            } else {
                val unlink = oauthService.unLink(
                    "Bearer ${GlobalApplication.prefs.accessToken}",
                    "${GlobalApplication.prefs.userId}"
                )

                unlink.enqueue(object : Callback<UnlinkResponse> {
                    override fun onResponse(
                        call: Call<UnlinkResponse>,
                        response: Response<UnlinkResponse>
                    ) {
                        if (response.isSuccessful) {
                            val code = response.body()!!.code
                            val message = response.body()!!.message

                            GlobalApplication.prefs.accessToken = ""
                            GlobalApplication.prefs.userId = ""


                            if(GlobalApplication.prefs.loginStatus == 1) {
                                UserApiClient.instance.unlink { error ->
                                    if(error != null) {
                                        Toast.makeText(mContext, "오류로 인해 회원탈퇴 실패하였습니다.", Toast.LENGTH_SHORT).show()
                                    }else {
                                        Log.e("unlink: ", "연결끊기 성공. SDK에서 토큰 삭제됨")
                                    }
                                }
                            } else {
                                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken("727850004794-5clt9m4h33ff0vqprfl104qlm6m4t32e.apps.googleusercontent.com")
                                    .requestServerAuthCode("727850004794-5clt9m4h33ff0vqprfl104qlm6m4t32e.apps.googleusercontent.com")
                                    .requestEmail()
                                    .build()

                                val googleSignInClient = GoogleSignIn.getClient(mContext, gso)

                                googleSignInClient!!.revokeAccess()
                            }


                            GlobalApplication.prefs.loginStatus = 0

                            val intent = Intent(mContext, LogInActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            mContext.startActivity(intent)
                            dialog1.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<UnlinkResponse>, t: Throwable) {
                        Log.e("unlink: ", "$t")
                    }
                })
            }
        }

        dialog1.findViewById<TextView>(R.id.btn_No).setOnClickListener {
            dialog1.dismiss()
        }
    }

    private fun sendFCMToken(token: String) {
        val fcmToken = SendFCMTokenRequest(token)
        val sendToken = tokenService.sendFCMToken("Bearer ${GlobalApplication.prefs.accessToken}", "${GlobalApplication.prefs.userId}", fcmToken)

        sendToken.enqueue(object: Callback<SendFCMTokenResponse> {
            override fun onResponse(
                call: Call<SendFCMTokenResponse>,
                response: Response<SendFCMTokenResponse>
            ) {
                if(response.isSuccessful) {
                }
            }

            override fun onFailure(call: Call<SendFCMTokenResponse>, t: Throwable) {
                Log.e("sendToken: ", "$t")
            }
        })

    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        oauthService = retrofit.create(OAuthService::class.java)
        tokenService = retrofit.create(TokenService::class.java)
    }

}
