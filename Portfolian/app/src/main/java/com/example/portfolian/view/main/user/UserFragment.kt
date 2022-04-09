package com.example.portfolian.view.main.user

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService
import com.bumptech.glide.Glide
import com.example.portfolian.R
import com.example.portfolian.data.UserInfoResponse
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.UserService
import com.example.portfolian.view.main.user.setting.SettingActivity
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class UserFragment : Fragment(R.layout.fragment_user) {
    private lateinit var retrofit: Retrofit
    private lateinit var userInfoService: UserService
    private lateinit var userInfo: UserInfoResponse

    private lateinit var toolbar: Toolbar
    private lateinit var profileModify: Button
    private lateinit var profileImage: CircleImageView
    private lateinit var nickName: TextView
    private lateinit var git: ImageButton
    private lateinit var mail: ImageButton
    private lateinit var description: TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        initRetrofit()
        initToolbar(view)
        initProfileModify(view)
        initUserInfo(view)
        readUserInfo()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        userInfoService = retrofit.create(UserService::class.java)
    }

    private fun initToolbar(view: View) {
        toolbar = view.findViewById(R.id.userToolbar)

        toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.toolbar_Alert -> {
                    //TODO 알림 버튼 눌렀을 때

                    true
                }

                R.id.toolbar_Setting -> {
                    val intent = Intent(activity, SettingActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
    }

    private fun initUserInfo(view: View) {
        profileImage = view.findViewById(R.id.cv_Profile)

    }

    private fun initProfileModify(view: View) {
        profileModify = view.findViewById(R.id.btn_ModifyProfile)

        profileModify.setOnClickListener {
            val intent = Intent(activity, ProfileModifyActivity::class.java)
            Log.e("profile", "${userInfo.photo}")
            intent.putExtra("profile", "${userInfo.photo}")
            startActivity(intent)
        }
    }

    private fun readUserInfo() {
        val callUserInfo = userInfoService.readUserInfo("Bearer ${GlobalApplication.prefs.accessToken}", "${GlobalApplication.prefs.userId}")

        callUserInfo.enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
                if (response.isSuccessful) {
                    userInfo = response.body()!!
                    setUserInfo()
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                Log.e("UserInfoService: ", "$t")
            }
        })
    }

    private fun setUserInfo() {
        nickName = requireActivity().findViewById(R.id.tv_UserName)
        profileImage = requireActivity().findViewById(R.id.cv_Profile)
        git = requireActivity().findViewById(R.id.ib_Git)
        mail = requireActivity().findViewById(R.id.ib_Mail)
        description = requireActivity().findViewById(R.id.tv_UserIntroduce)

        nickName.text = userInfo.nickName
        
        if(userInfo.photo.isEmpty()) {
            profileImage.setImageDrawable(context?.getDrawable(R.drawable.avatar_1_raster))
        } else {
            Glide.with(this)
                .load(userInfo.photo)
                .into(profileImage)
        }

        git.setOnClickListener {
            var clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("git", "${userInfo.github}")
            clipboardManager.setPrimaryClip(clip)

        }

        mail.setOnClickListener {
            var clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("mail", "${userInfo.mail}")
            clipboardManager.setPrimaryClip(clip)
        }

        description.text = userInfo.description
    }

}