package com.example.portfolian.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.portfolian.R
import com.example.portfolian.data.ProfileImageResponse
import com.example.portfolian.data.ReportRequest
import com.example.portfolian.data.ReportResponse
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.ProjectService
import com.example.portfolian.service.UserService
import com.example.portfolian.view.main.user.ProfileModifyActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

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