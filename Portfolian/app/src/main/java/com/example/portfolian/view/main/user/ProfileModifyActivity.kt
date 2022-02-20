package com.example.portfolian.view.main.user

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.example.portfolian.R
import com.example.portfolian.data.ModifyProfileRequest
import com.example.portfolian.data.ModifyProfileResponse
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.UserService
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.*
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest
import kotlin.math.roundToInt

class ProfileModifyActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var userService: UserService

    private lateinit var addPhoto: ImageButton

    private lateinit var toolbar: Toolbar
    private lateinit var profile: CircleImageView
    private lateinit var nickName: EditText
    private lateinit var git: EditText
    private lateinit var mail: EditText
    private lateinit var description: EditText

    private lateinit var stackChoice: LinearLayout
    private lateinit var drawer: DrawerLayout
    private lateinit var drawerLinear : LinearLayout
    private lateinit var close: ImageButton
    private lateinit var allNonClick: Button
    private lateinit var stackView: FlexboxLayout
    private lateinit var checkedStackView: FlexboxLayout
    private lateinit var checkedChips: MutableList<Chip>
    private lateinit var bitmap: Bitmap

    private var myStack = ""
    private var myColor = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profilemodify)

        initView()
    }

    private fun initView() {
        initRetrofit()
        initToolbar()
        initDrawer()
        initStackView()
        initStackChoice()
        setProfile()
        initAddPhoto()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        userService = retrofit.create(UserService::class.java)
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar_Setting)

        toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.toolbar_Save -> {
                    setUserInfo()
                    finish()
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private lateinit var getResult: ActivityResultLauncher<Intent>
    private fun initAddPhoto() {
        addPhoto = findViewById(R.id.ib_AddPhoto)

        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK) {
                var currentImageUri = it.data?.data

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImageUri)
                    var file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val filePath = file.path + "/img.png"
                    /*if(!file.exists()) {
                        file.mkdirs()
                    }*/
                    var out = FileOutputStream(filePath)

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)

                    out.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

        addPhoto.setOnClickListener {
            var writePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            var readPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)

            if(writePermission == PackageManager.PERMISSION_DENIED  || readPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                var state = Environment.getExternalStorageState()
                if(TextUtils.equals(state, Environment.MEDIA_MOUNTED)) {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    getResult.launch(intent)
                }
            }


        }


    }



    private fun setUserInfo() {
        setProfile()

        nickName = findViewById(R.id.et_Nickname)
        var nickNameStr = nickName.text.toString()

        git = findViewById(R.id.et_Git)
        var gitStr = git.text.toString()

        mail = findViewById(R.id.et_Email)
        var mailStr = mail.text.toString()

        description = findViewById(R.id.et_Introduce)
        var descriptionStr = description.text.toString()

        var stackList = mutableListOf<String>()

        for(chip in checkedChips) {
            var stackName = bigToSmall(chip.text.toString().trim())
            stackList.add(stackName)
        }
        var userInfoData = ModifyProfileRequest(
            nickNameStr,
            descriptionStr,
            stackList,
            bitmap,
            gitStr,
            mailStr
        )

        if(!nickNameStr.isNullOrEmpty() || !gitStr.isNullOrEmpty() || mailStr.isNullOrEmpty() || !descriptionStr.isNullOrEmpty() || !stackList.isNullOrEmpty()) {
            val saveProfile = userService.modifyProfile("Bearer ${GlobalApplication.prefs.accessToken}", "${GlobalApplication.prefs.userId}", userInfoData)

            saveProfile.enqueue(object: Callback<ModifyProfileResponse> {
                override fun onResponse(
                    call: Call<ModifyProfileResponse>,
                    response: Response<ModifyProfileResponse>
                ) {
                    if(response.isSuccessful) {
                        var code = response.body()!!.code

                        Log.d("saveProfile: ", "$code")
                    }
                }

                override fun onFailure(call: Call<ModifyProfileResponse>, t: Throwable) {
                    Log.e("saveProfile: ", "$t")
                }
            })
        }
    }


    private fun setProfile() {
        var intent = Intent(this, ProfileModifyActivity::class.java)
        var profileStr = intent.getStringExtra("profile")
        profile = findViewById(R.id.cv_Profile)

        if(profileStr.isNullOrEmpty()){
            profile.setImageDrawable(this.getDrawable(R.drawable.avatar_1_raster))
        } else {
            Glide.with(this)
                .load(profileStr)
                .into(profile)
        }

    }

    //기술 선택창 설정
    private fun initDrawer() {
        allNonClick = findViewById(R.id.btn_AllNonClick)

        allNonClick.setOnClickListener {
            var N = checkedChips.size

            for (i in 0 until N) {
                checkedChips[0].isChecked = false
            }
        }

        close = findViewById(R.id.ib_Close)
        close.setOnClickListener {
            drawer.closeDrawers()
        }
    }

    //기술 선택시 이벤트 처리
    private fun initStackChoice() {
        stackChoice = findViewById(R.id.ll_ProfileStack)
        drawer = findViewById(R.id.dl_ProfileModify)

        stackChoice.setOnClickListener {
            drawerLinear = findViewById(R.id.ll_Drawer)
            drawer.openDrawer(drawerLinear)
        }
    }

    //drawer안에 기술 설정
    private fun initStackView() {
        stackView = findViewById(R.id.fl_StackChoice)

        checkedStackView = findViewById(R.id.fl_Stack)

        val nameArray = arrayOf(
            "frontEnd",
            "backEnd",
            "cCsharp",
            "react",
            "vue",
            "spring",
            "django",
            "javascript",
            "git",
            "typescript",
            "ios",
            "android",
            "angular",
            "htmlCss",
            "flask",
            "nodeJs",
            "java",
            "go",
            "python",
            "kotlin",
            "swift",
            "cCpp",
            "design",
            "figma",
            "sketch",
            "adobeXD",
            "gcp",
            "photoshop",
            "illustrator",
            "firebase",
            "aws",
            "etc"
        )

        stackView.addItems(nameArray)
    }

    //flexbox layout 아이템 동적추가
    private fun FlexboxLayout.addItems(names: Array<String>) {
        checkedChips = mutableListOf()

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

                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        if (checkedChips.size < 7) {
                            checkedChips.add(this)
                            checkedStackView.addItem(name)
                        } else {
                            this.isChecked = false
                        }
                    } else {
                        val chipIdx = checkedChips.indexOf(this)
                        checkedStackView.removeViewAt(chipIdx)
                        checkedChips.removeAt(chipIdx)

                    }
                }
            }

            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
            )

            layoutParams.rightMargin = dpToPx(6)
            addView(chip, layoutParams)
        }


    }


    // 홈화면에 하나씩 동적추가
    private fun FlexboxLayout.addItem(name: String) {
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
            ViewGroup.MarginLayoutParams.WRAP_CONTENT
        )

        layoutParams.rightMargin = dpToPx(6)
        addView(chip, layoutParams)
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