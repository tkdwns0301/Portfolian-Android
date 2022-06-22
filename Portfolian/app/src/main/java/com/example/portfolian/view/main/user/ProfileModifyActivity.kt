package com.example.portfolian.view.main.user

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.portfolian.R
import com.example.portfolian.data.ModifyProfileRequest
import com.example.portfolian.data.ModifyProfileResponse
import com.example.portfolian.data.ProfileImageResponse
import com.example.portfolian.data.UserInfoResponse
import com.example.portfolian.databinding.ActivityProfilemodifyBinding
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.UserService
import com.example.portfolian.view.ProfileDialog
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.*
import java.lang.Exception
import kotlin.math.roundToInt

class ProfileModifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfilemodifyBinding

    private lateinit var retrofit: Retrofit
    private lateinit var userService: UserService

    private lateinit var addPhoto: TextView

    private lateinit var toolbar: Toolbar
    lateinit var profile: CircleImageView
    private lateinit var nickName: EditText
    private lateinit var git: EditText
    private lateinit var mail: EditText
    private lateinit var description: EditText

    private lateinit var stackChoice: LinearLayout
    private lateinit var drawer: DrawerLayout
    private lateinit var drawerLinear: LinearLayout
    private lateinit var close: ImageButton
    private lateinit var allNonClick: Button
    private lateinit var stackView: FlexboxLayout
    private lateinit var checkedStackView: FlexboxLayout
    private lateinit var checkedChips: MutableList<Chip>

    private lateinit var bitmap: Bitmap
    private lateinit var filePath: String

    private var myStack = ""
    private var myColor = 0

    var customProfileFlag = false
    lateinit var getResult: ActivityResultLauncher<Intent>


    private lateinit var checkedStack: List<String>

    init {
        instance = this
    }

    companion object {
        private var instance: ProfileModifyActivity? = null
        fun getInstance(): ProfileModifyActivity? {
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfilemodifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                filePath = getRealPathFromURI(it.data?.data!!)
                setProfileCustom()
            }

        }

        init()
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val buildName = Build.MANUFACTURER
        if(buildName.equals("Xiaomi")) {
            return uri.path.toString()
        }

        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        var cursor = contentResolver.query(uri, proj, null, null, null)
        if(cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }

        return cursor.getString(columnIndex)

    }

    private fun init() {
        initRetrofit()
        initView()
    }

    private fun initView() {
        nickName = binding.drawerProfileModify.etNickname
        git = binding.drawerProfileModify.etGit
        mail = binding.drawerProfileModify.etEmail
        description = binding.drawerProfileModify.etIntroduce
        profile = binding.drawerProfileModify.cvProfile
        toolbar = binding.drawerProfileModify.toolbarSetting
        addPhoto = binding.drawerProfileModify.tvModifyProfile

        addPhoto.setOnClickListener {
            val profileDialog = ProfileDialog(this)
            profileDialog.showDialog()
        }
        allNonClick = binding.btnAllNonClick
        close = binding.ibClose

        initToolbar()
        initUserInfo()

    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        userService = retrofit.create(UserService::class.java)
    }

    private fun initToolbar() {

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
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


    fun initAddPhoto() {


        var writePermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        var readPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1
            )
        } else {
            var state = Environment.getExternalStorageState()
            if (TextUtils.equals(state, Environment.MEDIA_MOUNTED)) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                getResult.launch(intent)
            }

        }


    }

    private fun initUserInfo() {
        val callUserInfo = userService.readUserInfo(
            "Bearer ${GlobalApplication.prefs.accessToken}",
            "${GlobalApplication.prefs.userId}"
        )

        callUserInfo.enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("callUserInfo: ", "renewal")
                    val userInfo = response.body()!!

                    nickName.setText(userInfo.nickName)
                    if (userInfo.github.isNotEmpty()) {
                        git.setText(userInfo.github.substring(15, userInfo.github.length))
                    }
                    mail.setText(userInfo.mail)
                    description.setText(userInfo.description)
                    checkedStack = userInfo.stackList

                    initDrawer()
                    initStackView()
                    initStackChoice()

                    var photo = intent.getStringExtra("profile")

                    if (photo.isNullOrEmpty()) {
                        profile.setImageDrawable(applicationContext.getDrawable(R.drawable.avatar_1_raster))
                    } else {
                        Glide.with(applicationContext)
                            .load(photo)
                            .into(profile)
                    }
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                Log.e("UserInfoService: ", "$t")
            }
        })
    }

    private fun setProfileCustom() {
        val file = File(filePath)
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", "photo", requestFile)

        val setCustomImage = userService.modifyCustomProfile(
            "Bearer ${GlobalApplication.prefs.accessToken}",
            "${GlobalApplication.prefs.userId}",
            body
        )

        setCustomImage.enqueue(object : Callback<ProfileImageResponse> {
            override fun onResponse(
                call: Call<ProfileImageResponse>,
                response: Response<ProfileImageResponse>
            ) {
                if (response.isSuccessful) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message
                    val profileURL = response.body()!!.profileURL

                    Log.e("setProfileCustom: ", "$code, $message, $profileURL")


                    if (profileURL.isNullOrEmpty()) {
                        profile.setImageDrawable(getDrawable(R.drawable.avatar_1_raster))
                    } else {
                        Glide.with(applicationContext)
                            .load(profileURL)
                            .into(profile)
                    }

                    Toast.makeText(applicationContext, "프로필 사진 수정 완료되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ProfileImageResponse>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "프로필 사진을 변경하는 도중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun setUserInfo() {

        var nickNameStr = nickName.text.toString()
        var gitStr = "www.github.com/" + git.text.toString()
        var mailStr = mail.text.toString()
        var descriptionStr = description.text.toString()

        var stackList = mutableListOf<String>()

        for (chip in checkedChips) {
            var stackName = bigToSmall(chip.text.toString().trim())
            stackList.add(stackName)
        }

        val modify = ModifyProfileRequest(nickNameStr, descriptionStr, stackList, gitStr, mailStr)

        val saveProfile = userService.modifyProfile(
            "Bearer ${GlobalApplication.prefs.accessToken}",
            "${GlobalApplication.prefs.userId}",
            modify
        )

        saveProfile.enqueue(object : Callback<ModifyProfileResponse> {
            override fun onResponse(
                call: Call<ModifyProfileResponse>,
                response: Response<ModifyProfileResponse>
            ) {
                if (response.isSuccessful) {
                    var code = response.body()!!.code

                    Toast.makeText(applicationContext, "나의 정보 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ModifyProfileResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "나의 정보를 수정하는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })

    }

    //기술 선택창 설정
    private fun initDrawer() {

        allNonClick.setOnClickListener {
            var N = checkedChips.size

            for (i in 0 until N) {
                checkedChips[0].isChecked = false
            }
        }

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

                for (stack in checkedStack) {
                    if (stack == name) {
                        isChecked = true
                        checkedChips.add(this)
                        checkedStackView.addItem(name)
                    }
                }

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