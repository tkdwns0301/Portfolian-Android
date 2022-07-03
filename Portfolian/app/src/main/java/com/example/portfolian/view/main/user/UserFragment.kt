package com.example.portfolian.view.main.user

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.bumptech.glide.Glide
import com.example.portfolian.R
import com.example.portfolian.data.UserInfoResponse
import com.example.portfolian.databinding.FragmentUserBinding
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.network.SocketApplication
import com.example.portfolian.service.UserService
import com.example.portfolian.view.main.user.setting.SettingActivity
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.math.roundToInt

class UserFragment : Fragment(R.layout.fragment_user) {
    private lateinit var binding: FragmentUserBinding

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
    private lateinit var stack: FlexboxLayout

    private var myStack = ""
    private var myColor = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        SocketApplication.getSocket().off("chat:receive")
        initView()
    }

    private fun init() {
        initRetrofit()
        initView()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        userInfoService = retrofit.create(UserService::class.java)
    }

    private fun initView() {
        toolbar = binding.userToolbar
        profileModify = binding.btnModifyProfile
        profileImage = binding.cvProfile
        nickName = binding.tvUserName
        git = binding.ibGit
        mail = binding.ibMail
        description = binding.tvUserIntroduce
        stack = binding.fblProfileStack

        initToolbar()
        initProfileModify()
        readUserInfo()
    }

    private fun initToolbar() {
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

    private fun initProfileModify() {
        profileModify.setOnClickListener {
            val intent = Intent(activity, ProfileModifyActivity::class.java)
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
        nickName.text = userInfo.nickName

        if(userInfo.photo.isEmpty()) {
            profileImage.setImageDrawable(context?.getDrawable(R.drawable.avatar_1_raster))
        } else {
            Glide.with(this)
                .load(userInfo.photo)
                .into(profileImage)
        }

        git.setOnClickListener {
            val intent = Intent(context, WebViewActivity::class.java)
            Log.e("git", "${userInfo.github}")
            intent.putExtra("git", "www.github.com/${userInfo.github}")
            startActivity(intent)
        }

        mail.setOnClickListener {
            var clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("mail", "${userInfo.mail}")
            clipboardManager.setPrimaryClip(clip)
        }

        description.text = userInfo.description

        stack.removeAllViews()
        stack.addItems(userInfo.stackList)

    }

    // 홈화면에 하나씩 동적추가
    private fun FlexboxLayout.addItems(names: List<String>) {

        for(name in names) {
            val chip = LayoutInflater.from(context).inflate(R.layout.view_chip, null) as Chip
            chip.apply {
                stackColor(name)

                text = " $myStack "
                textSize = 12f
                textAlignment = View.TEXT_ALIGNMENT_CENTER

                val nonClickColor = ContextCompat.getColor(context, R.color.nonClick_tag)
                chipBackgroundColor = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_checked)
                    ),
                    intArrayOf(nonClickColor, myColor)
                )

                val nonCLickTextColor = ContextCompat.getColor(context, R.color.gray1)
                //텍스트
                setTextColor(
                    ColorStateList(
                        arrayOf(
                            intArrayOf(-android.R.attr.state_checked),
                            intArrayOf(android.R.attr.state_checked)
                        ),
                        intArrayOf(nonCLickTextColor, Color.BLACK)
                    )
                )
                isChecked = true
                isClickable = false
            }
            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                120
            )

            layoutParams.rightMargin = dpToPx(6)
            addView(chip, layoutParams)
        }
    }

    private fun stackColor(name: String) {
        when (name) {
            "frontEnd" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.front_end)
                myStack = "Front-end"
            }
            "backEnd" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.back_end)
                myStack = "Back-end"
            }
            "react" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.react)
                myStack = "React"
            }
            "vue" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.vue)
                myStack = "Vue"
            }
            "spring" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.spring)
                myStack = "Spring"
            }
            "Spring" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.spring)
                myStack = "Spring"
            }
            "django" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.django)
                myStack = "Django"
            }
            "ios" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.ios)
                myStack = "iOS"
            }
            "typescript" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.typescript)
                myStack = "Typescript"
            }
            "javascript" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.javascript)
                myStack = "Javascript"
            }
            "android" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.android)
                myStack = "Android"
            }
            "angular" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.angular)
                myStack = "Angular"
            }
            "htmlCss" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.html_css)
                myStack = "HTML/CSS"
            }
            "flask" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.flask)
                myStack = "Flask"
            }
            "nodeJs" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.node)
                myStack = "Node.js"
            }
            "java" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.java)
                myStack = "Java"
            }
            "python" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.python)
                myStack = "Python"
            }
            "cCsharp" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.c_sharp)
                myStack = "C#"
            }
            "kotlin" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.kotlin)
                myStack = "Kotlin"
            }
            "swift" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.swift)
                myStack = "Swift"
            }
            "go" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.go)
                myStack = "Go"
            }
            "cCpp" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.c_cpp)
                myStack = "C/C++"
            }
            "design" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.design)
                myStack = "Design"
            }
            "figma" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.figma)
                myStack = "Figma"
            }
            "sketch" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.sketch)
                myStack = "Sketch"
            }
            "git" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.git)
                myStack = "Git"
            }
            "adobeXD" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.adobexd)
                myStack = "AdobeXD"
            }
            "photoshop" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.photoShop)
                myStack = "Photoshop"
            }
            "illustrator" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.illustrator)
                myStack = "Illustrator"
            }
            "firebase" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.firebase)
                myStack = "Firebase"
            }
            "aws" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.aws)
                myStack = "AWS"
            }
            "gcp" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.gcp)
                myStack = "GCP"
            }
            "etc" -> {
                myColor = ContextCompat.getColor(requireContext(), R.color.ect)
                myStack = "etc"
            }
        }
    }

    private fun dpToPx(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).roundToInt()


    private fun bigToSmall(big: String): String {
        when (big) {
            "Front-end" -> return "frontEnd"
            "Back-end" -> return "backEnd"
            "C#" -> return "cCsharp"
            "React" -> return "react"
            "Vue" -> return "vue"
            "Spring" -> return "spring"
            "Django" -> return "django"
            "Javascript" -> return "javascript"
            "Git" -> return "git"
            "Typescript" -> return "typescript"
            "iOS" -> return "ios"
            "Android" -> return "android"
            "Angular" -> return "angular"
            "HTML/CSS" -> return "htmlCss"
            "Flask" -> return "flask"
            "Node.js" -> return "nodeJs"
            "Java" -> return "java"
            "Go" -> return "go"
            "Python" -> return "python"
            "Kotlin" -> return "kotlin"
            "Swift" -> return "swift"
            "C/C++" -> return "cCpp"
            "Design" -> return "design"
            "Figma" -> return "figma"
            "Sketch" -> return "sketch"
            "AdobeXD" -> return "adobeXD"
            "GCP" -> return "gcp"
            "Photoshop" -> return "photoshop"
            "Illustrator" -> return "illustrator"
            "Firebase" -> return "firebase"
            "AWS" -> return "aws"
            "etc" -> return "etc"

        }

        return ""
    }

}