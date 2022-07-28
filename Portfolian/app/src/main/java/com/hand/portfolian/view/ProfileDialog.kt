package com.hand.portfolian.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.bumptech.glide.Glide
import com.hand.portfolian.R
import com.hand.portfolian.data.ProfileImageResponse
import com.hand.portfolian.network.GlobalApplication
import com.hand.portfolian.network.RetrofitClient
import com.hand.portfolian.service.UserService
import com.hand.portfolian.view.main.user.ProfileModifyActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ProfileDialog(context: Context) {
    private var mContext = context
    private val dialog = Dialog(mContext)
    private lateinit var retrofit: Retrofit
    private lateinit var userService: UserService

    private lateinit var menu1: TextView
    private lateinit var menu2: TextView

    private lateinit var getResult: ActivityResultLauncher<Intent>


    fun showDialog() {
        initRetrofit()

        dialog.setContentView(R.layout.dialog_profile)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        menu1 = dialog.findViewById(R.id.tv_Menu1)
        menu2 = dialog.findViewById(R.id.tv_Menu2)

        menu1.setOnClickListener {
            setProfileDefault()
        }
        menu2.setOnClickListener {
            val profileModifyActivity = ProfileModifyActivity.getInstance()
            profileModifyActivity!!.initAddPhoto()
            dialog.dismiss()
        }


    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        userService = retrofit.create(UserService::class.java)
    }

    private fun setProfileDefault() {
        val setDefaultImage = userService.modifyDefaultProfile("Bearer ${GlobalApplication.prefs.accessToken}", "${GlobalApplication.prefs.userId}")

        setDefaultImage.enqueue(object: Callback<ProfileImageResponse> {
            override fun onResponse(
                call: Call<ProfileImageResponse>,
                response: Response<ProfileImageResponse>
            ) {
                if(response.isSuccessful) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message
                    val profileURL = response.body()!!.profileURL

                    Log.e("setDefaultImage: ", "$code, $message, $profileURL")

                    val profileModifyActivity = ProfileModifyActivity.getInstance()

                    profileModifyActivity!!.profile

                    if (profileURL.isNullOrEmpty()) {
                        profileModifyActivity!!.profile.setImageDrawable(mContext.getDrawable(R.drawable.avatar_1_raster))
                    } else {
                        Glide.with(mContext)
                            .load(profileURL)
                            .into(profileModifyActivity!!.profile)
                    }

                    Toast.makeText(mContext, "기본이미지로 수정 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProfileImageResponse>, t: Throwable) {
                Toast.makeText(mContext, "프로필 사진을 변경하는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })

        dialog.dismiss()

    }

}