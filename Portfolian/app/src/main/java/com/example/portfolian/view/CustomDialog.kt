package com.example.portfolian.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.portfolian.R
import com.example.portfolian.data.LogoutResponse
import com.example.portfolian.data.OAuthResponse
import com.example.portfolian.data.UnlinkResponse
import com.example.portfolian.databinding.DialogLayoutBinding
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.OAuthService
import com.example.portfolian.view.login.LogInActivity
import com.kakao.sdk.user.UserApiClient
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.coroutines.coroutineContext

class CustomDialog(context: Context, flag: Boolean) {
    private var mContext = context
    private lateinit var retrofit: Retrofit
    private lateinit var oauthService: OAuthService

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
                            GlobalApplication.prefs.loginStatus = 0

                            UserApiClient.instance.logout { error ->
                                if(error != null) {
                                    Log.e("Logout: ", "로그아웃 실패 SDK에서 토큰 삭제됨 $error")
                                }else {
                                    Log.e("Logout: ", "로그아웃 성공. SDK에서 토큰 삭제됨")
                                }
                            }

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

                            Log.d("unlink: ", "$code")
                            Log.d("unlink: ", "$message")

                            GlobalApplication.prefs.accessToken = ""
                            GlobalApplication.prefs.userId = ""

                            UserApiClient.instance.unlink { error ->
                                if(error != null) {
                                    Log.e("unlink: ", "연결끊기 실패 $error")
                                }else {
                                    Log.e("unlink: ", "연결끊기 성공. SDK에서 토큰 삭제됨")
                                }
                            }

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

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        oauthService = retrofit.create(OAuthService::class.java)
    }

}
