package com.hand.portfolian.view.main.user

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
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.hand.portfolian.R
import com.hand.portfolian.data.UserInfoResponse
import com.hand.portfolian.databinding.ActivityOtherBinding
import com.hand.portfolian.network.GlobalApplication
import com.hand.portfolian.network.RetrofitClient
import com.hand.portfolian.service.UserService
import com.hand.portfolian.view.ReportUserDialog
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.math.roundToInt

class OtherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtherBinding

    private lateinit var retrofit: Retrofit
    private lateinit var otherInfoService: UserService
    private lateinit var otherInfo: UserInfoResponse
    private lateinit var otherId: String

    private lateinit var toolbar: Toolbar
    private lateinit var profile: CircleImageView
    private lateinit var nickName: TextView
    private lateinit var git: ImageButton
    private lateinit var mail: ImageButton
    private lateinit var modifyProfile: AppCompatButton
    private lateinit var stack: FlexboxLayout
    private lateinit var description: TextView

    private var myColor: Int = 0
    private var myStack: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOtherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        initRetrofit()
        initView()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        otherInfoService = retrofit.create(UserService::class.java)
    }

    private fun initView() {
        toolbar = binding.userToolbar

        profile = binding.cvProfile
        nickName = binding.tvUserName
        git = binding.ibGit
        mail = binding.ibMail

        modifyProfile = binding.btnModifyProfile
        modifyProfile.isVisible = false

        stack = binding.fblProfileStack
        description = binding.tvUserIntroduce

        initToolbar()
        readOtherInfo()
    }

    private fun initToolbar() {

        toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.toolbar_Report -> {
                    val reportDialog = ReportUserDialog(this, "$otherId")
                    reportDialog.showDialog()
                    true
                }
                else -> {
                    true
                }
            }
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun readOtherInfo() {
        otherId = intent.getStringExtra("userId").toString()

        val callOtherInfo = otherInfoService.readUserInfo(
            "Bearer ${GlobalApplication.prefs.accessToken}",
            "$otherId"
        )

        callOtherInfo.enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
                if (response.isSuccessful) {
                    otherInfo = response.body()!!
                    setOtherInfo()
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                Log.e("callOtherInfo:", "$t")
            }
        })
    }

    private fun setOtherInfo() {
        nickName.text = otherInfo.nickName

        if (otherInfo.photo.isEmpty()) {
            profile.setImageDrawable(getDrawable(R.drawable.avatar_1_raster))
        } else {
            Glide.with(this)
                .load(otherInfo.photo)
                .into(profile)
        }

        git.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("git", "www.github.com/${otherInfo.github}")
            startActivity(intent)
        }

        mail.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("mail", "${otherInfo.mail}")
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(this, "메일주소가 복사되었습니다.", Toast.LENGTH_SHORT).show()
        }

        description.text = otherInfo.description

        stack.removeAllViews()
        stack.addItems(otherInfo.stackList)
    }

    private fun FlexboxLayout.addItems(names: List<String>) {
        for (name in names) {
            val chip = LayoutInflater.from(context).inflate(R.layout.view_chip, null) as Chip

            chip.apply {
                stackColor(name)

                text = "  $myStack  "
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)

                val nonClickColor = ContextCompat.getColor(context, R.color.nonClick_tag)

                chipBackgroundColor = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_checked)
                    ),
                    intArrayOf(nonClickColor, myColor)
                )

                val nonClickTextColor = ContextCompat.getColor(context, R.color.gray1)
                //텍스트
                setTextColor(
                    ColorStateList(
                        arrayOf(
                            intArrayOf(-android.R.attr.state_checked),
                            intArrayOf(android.R.attr.state_checked)
                        ),
                        intArrayOf(nonClickTextColor, Color.BLACK)
                    )

                )

                isChecked = true
                isClickable = false
            }

            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
            )

            layoutParams.rightMargin = dpToPx(6)
            addView(chip, layoutParams)
        }
    }

    private fun stackColor(name: String) {
        when (name) {
            "frontEnd" -> {
                myColor = ContextCompat.getColor(this, R.color.front_end)
                myStack = "Front-end"
            }
            "backEnd" -> {
                myColor = ContextCompat.getColor(this, R.color.back_end)
                myStack = "Back-end"
            }
            "react" -> {
                myColor = ContextCompat.getColor(this, R.color.react)
                myStack = "React"
            }
            "vue" -> {
                myColor = ContextCompat.getColor(this, R.color.vue)
                myStack = "Vue"
            }
            "spring" -> {
                myColor = ContextCompat.getColor(this, R.color.spring)
                myStack = "Spring"
            }
            "Spring" -> {
                myColor = ContextCompat.getColor(this, R.color.spring)
                myStack = "Spring"
            }
            "django" -> {
                myColor = ContextCompat.getColor(this, R.color.django)
                myStack = "Django"
            }
            "ios" -> {
                myColor = ContextCompat.getColor(this, R.color.ios)
                myStack = "iOS"
            }
            "typescript" -> {
                myColor = ContextCompat.getColor(this, R.color.typescript)
                myStack = "Typescript"
            }
            "javascript" -> {
                myColor = ContextCompat.getColor(this, R.color.javascript)
                myStack = "Javascript"
            }
            "android" -> {
                myColor = ContextCompat.getColor(this, R.color.android)
                myStack = "Android"
            }
            "angular" -> {
                myColor = ContextCompat.getColor(this, R.color.angular)
                myStack = "Angular"
            }
            "htmlCss" -> {
                myColor = ContextCompat.getColor(this, R.color.html_css)
                myStack = "HTML/CSS"
            }
            "flask" -> {
                myColor = ContextCompat.getColor(this, R.color.flask)
                myStack = "Flask"
            }
            "nodeJs" -> {
                myColor = ContextCompat.getColor(this, R.color.node)
                myStack = "Node.js"
            }
            "java" -> {
                myColor = ContextCompat.getColor(this, R.color.java)
                myStack = "Java"
            }
            "python" -> {
                myColor = ContextCompat.getColor(this, R.color.python)
                myStack = "Python"
            }
            "cCsharp" -> {
                myColor = ContextCompat.getColor(this, R.color.c_sharp)
                myStack = "C#"
            }
            "kotlin" -> {
                myColor = ContextCompat.getColor(this, R.color.kotlin)
                myStack = "Kotlin"
            }
            "swift" -> {
                myColor = ContextCompat.getColor(this, R.color.swift)
                myStack = "Swift"
            }
            "go" -> {
                myColor = ContextCompat.getColor(this, R.color.go)
                myStack = "Go"
            }
            "cCpp" -> {
                myColor = ContextCompat.getColor(this, R.color.c_cpp)
                myStack = "C/C++"
            }
            "design" -> {
                myColor = ContextCompat.getColor(this, R.color.design)
                myStack = "Design"
            }
            "figma" -> {
                myColor = ContextCompat.getColor(this, R.color.figma)
                myStack = "Figma"
            }
            "sketch" -> {
                myColor = ContextCompat.getColor(this, R.color.sketch)
                myStack = "Sketch"
            }
            "git" -> {
                myColor = ContextCompat.getColor(this, R.color.git)
                myStack = "Git"
            }
            "adobeXD" -> {
                myColor = ContextCompat.getColor(this, R.color.adobexd)
                myStack = "AdobeXD"
            }
            "photoshop" -> {
                myColor = ContextCompat.getColor(this, R.color.photoShop)
                myStack = "Photoshop"
            }
            "illustrator" -> {
                myColor = ContextCompat.getColor(this, R.color.illustrator)
                myStack = "Illustrator"
            }
            "firebase" -> {
                myColor = ContextCompat.getColor(this, R.color.firebase)
                myStack = "Firebase"
            }
            "aws" -> {
                myColor = ContextCompat.getColor(this, R.color.aws)
                myStack = "AWS"
            }
            "gcp" -> {
                myColor = ContextCompat.getColor(this, R.color.gcp)
                myStack = "GCP"
            }
            "etc" -> {
                myColor = ContextCompat.getColor(this, R.color.ect)
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