package com.example.portfolian.view.main.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.portfolian.R
import com.example.portfolian.data.Article
import com.example.portfolian.data.WriteProject
import com.example.portfolian.data.WriteProjectResponse
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.ProjectService
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.math.roundToInt

class NewProjectActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var projectService: ProjectService

    private lateinit var ll_StackChoice: LinearLayout
    private lateinit var ll_OwnerStackChoice: LinearLayout
    private lateinit var StackView: FlexboxLayout
    private lateinit var ownerStackView: FlexboxLayout
    private lateinit var dl_NewProject: DrawerLayout
    private lateinit var ll_Drawer: LinearLayout
    private lateinit var ll_OwnerDrawer: LinearLayout
    private lateinit var btn_AllNonClick: Button
    private lateinit var btn_OwnerAllNonClick: Button
    private lateinit var btn_Close: ImageButton
    private lateinit var btn_OwnerClose: ImageButton
    private lateinit var checkedChips: MutableList<Chip>
    private lateinit var checkedOwnerChips: MutableList<Chip>
    private lateinit var checkedStackView: FlexboxLayout
    private lateinit var checkedOwnerStackView: FlexboxLayout

    private lateinit var toolbar: Toolbar

    private var myStack: String = ""
    private var myColor: Int = 0

    private lateinit var et_Title: EditText
    private lateinit var et_Topic: EditText
    private lateinit var et_ProjectTime: EditText
    private lateinit var et_Condition: EditText
    private lateinit var et_Progress: EditText
    private lateinit var et_Description: EditText
    private lateinit var et_Capacity: EditText

    private lateinit var ownerStack: ArrayList<String>
    private lateinit var memberStack: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newproject)

        initRetrofit()
        initView()
    }

    private fun initView() {
        initToolbar()
        initDrawer()
        initStackView()
        initStackChoice()
    }

    //레트로핏 설정
    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        projectService = retrofit.create(ProjectService::class.java)
    }

    //새로운프로젝트 툴바 설정
    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar_NewProject)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.toolbar_Save -> {
                    //TODO 저장 버튼을 눌렀을 때 게시물 업로드
                    saveProject()
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

    private fun saveProject() {

        var ownerStack = bigToSmall(checkedOwnerChips[0].text.toString().trim())


        et_Title = findViewById(R.id.et_Title)
        var title = et_Title.text.toString()

        var stackList = mutableListOf<String>()
        for (chip in checkedChips) {
            var stackName = bigToSmall(chip.text.toString().trim())
            stackList.add(stackName)
        }

        et_Topic = findViewById(R.id.et_Topic)
        var subjectDescription = et_Topic.text.toString()

        et_ProjectTime = findViewById(R.id.et_Period)
        var projectTime = et_ProjectTime.text.toString()

        et_Condition = findViewById(R.id.et_Condition)
        var condition = et_Condition.text.toString()

        et_Progress = findViewById(R.id.et_Way)
        var progress = et_Progress.text.toString()

        et_Description = findViewById(R.id.et_Description)
        var description = et_Description.text.toString()

        et_Capacity = findViewById(R.id.et_UserCount)
        var capacity = et_Capacity.text.toString().toInt()

        var article = Article(
            title,
            stackList,
            subjectDescription,
            projectTime,
            condition,
            progress,
            description,
            capacity
        )

        Log.d("test", "$article")

        var textJson = WriteProject("testUser1", article, ownerStack)
        if (!et_Title.text.isNullOrEmpty() || stackList.isNotEmpty() || !et_Topic.text.isNullOrEmpty() || !et_ProjectTime.text.isNullOrEmpty() || !et_Condition.text.isNullOrEmpty() || !et_Progress.text.isNullOrEmpty() || !et_Capacity.text.isNullOrEmpty()) {

            val saveProject = projectService.writeProject(textJson)

            saveProject.enqueue(object : Callback<WriteProjectResponse> {
                override fun onResponse(
                    call: Call<WriteProjectResponse>,
                    response: Response<WriteProjectResponse>
                ) {
                    if (response.isSuccessful) {
                        var code = response.body()!!.code
                        var message = response.body()!!.message
                        var newProjectID = response.body()!!.newProjectID

                        Log.d("saveProject: ", "${code}, ${message}, ${newProjectID}")
                    }
                }

                override fun onFailure(call: Call<WriteProjectResponse>, t: Throwable) {
                    Log.e("saveProject: ", "${t}")
                }
            })
        }
    }

    //기술 선택창 설정
    private fun initDrawer() {
        btn_AllNonClick = findViewById(R.id.btn_AllNonClick)
        btn_OwnerAllNonClick = findViewById(R.id.btn_OwnerAllNonClick)

        btn_AllNonClick.setOnClickListener {
            for (chip in checkedChips) {
                chip.apply {
                    isChecked = false
                }
            }
            checkedStackView.removeAllViews()
        }

        btn_OwnerAllNonClick.setOnClickListener {
            for (chip in checkedOwnerChips) {
                chip.apply {
                    isChecked = false
                }
            }
            checkedOwnerStackView.removeAllViews()
        }

        btn_Close = findViewById(R.id.img_btn_Close)
        btn_Close.setOnClickListener {
            dl_NewProject.closeDrawers()
        }

        btn_OwnerClose = findViewById(R.id.img_btn_OwnerClose)
        btn_OwnerClose.setOnClickListener {
            dl_NewProject.closeDrawers()
        }
    }

    //기술 선택시 이벤트 처리
    private fun initStackChoice() {
        ll_StackChoice = findViewById(R.id.ll_StackChoice)
        ll_OwnerStackChoice = findViewById(R.id.ll_OwnerStackChoice)
        dl_NewProject = findViewById(R.id.dl_NewProject)

        ll_StackChoice.setOnClickListener {
            ll_Drawer = findViewById(R.id.ll_Drawer)
            dl_NewProject.openDrawer(ll_Drawer)
        }

        ll_OwnerStackChoice.setOnClickListener {
            ll_OwnerDrawer = findViewById(R.id.ll_OwnerDrawer)
            dl_NewProject.openDrawer(ll_OwnerDrawer)
        }
    }

    //drawer안에 기술 설정
    private fun initStackView() {
        StackView = findViewById(R.id.fl_Stack)
        ownerStackView = findViewById(R.id.fl_OwnerStack)

        checkedStackView = findViewById(R.id.fbl_CheckedStack)
        checkedOwnerStackView = findViewById(R.id.fbl_OwnerCheckedStack)

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

        StackView.addItems(nameArray)
        ownerStackView.addOwnerItems(nameArray)

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

    private fun FlexboxLayout.addOwnerItems(names: Array<String>) {
        checkedOwnerChips = mutableListOf()

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
                        if (checkedOwnerChips.size < 1) {
                            checkedOwnerChips.add(this)
                            checkedOwnerStackView.addItem(name)
                        } else {
                            this.isChecked = false
                        }
                    } else {
                        val chipIdx = checkedOwnerChips.indexOf(this)
                        checkedOwnerStackView.removeViewAt(chipIdx)
                        checkedOwnerChips.removeAt(chipIdx)

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