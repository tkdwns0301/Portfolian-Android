package com.example.portfolian.view.main.home

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import br.tiagohm.markdownview.MarkdownView
import com.example.portfolian.R
import com.example.portfolian.data.*
import com.example.portfolian.databinding.ActivityNewprojectBinding
import com.example.portfolian.databinding.DrawerNewProjectBinding
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.ProjectService
import com.example.portfolian.view.MarkdownDialog
import com.example.portfolian.view.main.MainActivity
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.math.roundToInt

class NewProjectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewprojectBinding

    private lateinit var retrofit: Retrofit
    private lateinit var projectService: ProjectService

    private lateinit var stackChoice: LinearLayout
    private lateinit var ownerStackChoice: LinearLayout
    private lateinit var stackView: FlexboxLayout
    private lateinit var ownerStackView: FlexboxLayout
    private lateinit var newProject: DrawerLayout
    private lateinit var linearDrawer: LinearLayout
    private lateinit var linearOwnerDrawer: LinearLayout
    private lateinit var allNonClick: Button
    private lateinit var ownerAllNonClick: Button
    private lateinit var close: ImageButton
    private lateinit var ownerClose: ImageButton
    private lateinit var checkedChips: MutableList<Chip>
    private lateinit var checkedOwnerChips: MutableList<Chip>
    private lateinit var checkedStackView: FlexboxLayout
    private lateinit var checkedOwnerStackView: FlexboxLayout

    private lateinit var toolbar: Toolbar

    private var myStack: String = ""
    private var myColor: Int = 0

    private lateinit var title: EditText
    private lateinit var subjectDescription: EditText
    private lateinit var projectTime: EditText
    private lateinit var condition: EditText
    private lateinit var progress: EditText
    private lateinit var description: EditText
    private lateinit var capacity: EditText
    private lateinit var markdown: MarkdownView
    private lateinit var markdownEx: TextView

    private lateinit var ownerStack: ArrayList<String>
    private lateinit var memberStack: ArrayList<String>

    private var isModify = false
    private lateinit var projectId: String
    private lateinit var detailData: DetailProjectResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewprojectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRetrofit()
        initView()
    }

    private fun initView() {
        if (DetailData.detailData != null) {
            isModify = true
            projectId = intent.getStringExtra("projectId").toString()
            detailData = DetailData.detailData!!
        }

        title = binding.drawerLayout.etTitle
        subjectDescription = binding.drawerLayout.etSubjectDescription
        subjectDescription = binding.drawerLayout.etSubjectDescription
        projectTime = binding.drawerLayout.etProjectTime
        condition = binding.drawerLayout.etCondition
        progress = binding.drawerLayout.etProgress
        description = binding.drawerLayout.etDescription
        capacity = binding.drawerLayout.etCapacity
        markdown = binding.drawerLayout.mdMarkdown
        markdownEx = binding.drawerLayout.tvMarkdownEx

        markdown.isVisible = false
        description.addTextChangedListener {
            if (description.text.isEmpty()) {
                markdown.isVisible = false
            } else {
                markdown.isVisible = true
                markdown.loadMarkdown("${description.text}")
            }
        }

        markdownEx.setOnClickListener {
            val markdownDialog = MarkdownDialog(this)
            markdownDialog.showDialog()
        }


        initToolbar()
        initDrawer()
        initStackView()
        initStackChoice()

        setView()
    }

    private fun setView() {
        if (isModify) {
            title.setText(detailData.title)
            subjectDescription.setText(detailData.contents.subjectDescription)
            projectTime.setText(detailData.contents.projectTime)
            condition.setText(detailData.contents.recruitmentCondition)
            progress.setText(detailData.contents.progress)
            description.setText(detailData.contents.description)
            capacity.setText("${detailData.capacity}")
        }
    }

    //레트로핏 설정
    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        projectService = retrofit.create(ProjectService::class.java)
    }

    //새로운프로젝트 툴바 설정
    private fun initToolbar() {
        toolbar = binding.drawerLayout.toolbarNewProject

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.toolbar_Save -> {
                    saveProject()
                    finish()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
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


        var titleStr = title.text.toString()

        var stackList = mutableListOf<String>()
        for (chip in checkedChips) {
            var stackName = bigToSmall(chip.text.toString().trim())
            stackList.add(stackName)
        }

        var subjectDescriptionStr = subjectDescription.text.toString()

        var projectTimeStr = projectTime.text.toString()

        var conditionStr = condition.text.toString()

        var progressStr = progress.text.toString()

        var descriptionStr = description.text.toString()

        var capacityStr = capacity.text.toString().toInt()

        var article = Article(
            titleStr,
            stackList,
            subjectDescriptionStr,
            projectTimeStr,
            conditionStr,
            progressStr,
            descriptionStr,
            capacityStr
        )

        var writeRequest = WriteProjectRequest(article, ownerStack)

        if (!title.text.isNullOrEmpty() || stackList.isNotEmpty() || !subjectDescription.text.isNullOrEmpty() || !projectTime.text.isNullOrEmpty() || !condition.text.isNullOrEmpty() || !progress.text.isNullOrEmpty() || !capacity.text.isNullOrEmpty()) {

            if (isModify) {
                val modifyProject = projectService.modifyProject(
                    "Bearer ${GlobalApplication.prefs.accessToken}",
                    projectId,
                    writeRequest
                )

                modifyProject.enqueue(object : Callback<ModifyProjectResponse> {
                    override fun onResponse(
                        call: Call<ModifyProjectResponse>,
                        response: Response<ModifyProjectResponse>
                    ) {
                        if (response.isSuccessful) {
                            var code = response.body()!!.code
                            var message = response.body()!!.message

                            Log.d("modifyProject: ", "$code, $message")
                        }
                    }

                    override fun onFailure(call: Call<ModifyProjectResponse>, t: Throwable) {
                        Log.e("modifyProject: ", "$t")
                    }
                })
            } else {
                val saveProject = projectService.writeProject(
                    "Bearer ${GlobalApplication.prefs.accessToken}",
                    writeRequest
                )

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
    }

    //기술 선택창 설정
    private fun initDrawer() {
        allNonClick = binding.btnAllNonClick
        ownerAllNonClick = binding.btnOwnerAllNonClick

        allNonClick.setOnClickListener {
            for (chip in checkedChips) {
                chip.apply {
                    isChecked = false
                }
            }

            checkedStackView.removeAllViews()
        }

        ownerAllNonClick.setOnClickListener {
            for (chip in checkedOwnerChips) {
                chip.apply {
                    isChecked = false
                }
            }
            checkedOwnerStackView.removeAllViews()
        }

        close = binding.imgBtnClose
        close.setOnClickListener {
            newProject.closeDrawers()
        }

        ownerClose = binding.imgBtnOwnerClose

        ownerClose.setOnClickListener {
            newProject.closeDrawers()
        }
    }

    //기술 선택시 이벤트 처리
    private fun initStackChoice() {
        stackChoice = binding.drawerLayout.llStackChoice
        ownerStackChoice = binding.drawerLayout.llOwnerStackChoice

        newProject = binding.root

        stackChoice.setOnClickListener {
            linearDrawer = binding.llDrawer
            newProject.openDrawer(linearDrawer)
        }

        ownerStackChoice.setOnClickListener {
            linearOwnerDrawer = binding.llOwnerDrawer
            newProject.openDrawer(linearOwnerDrawer)
        }
    }

    //drawer안에 기술 설정
    private fun initStackView() {
        stackView = binding.flStack
        ownerStackView = binding.flOwnerStack

        checkedStackView = binding.drawerLayout.fblCheckedStack
        checkedOwnerStackView = binding.drawerLayout.fblOwnerCheckedStack

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
        ownerStackView.addOwnerItems(nameArray)
    }

    //flexbox layout 아이템 동적추가
    private fun FlexboxLayout.addItems(names: Array<String>) {
        checkedChips = mutableListOf()

        for (name in names) {
            val chip = LayoutInflater.from(context).inflate(R.layout.view_chip, null) as Chip

            chip.apply {
                stackColor(name)

                text = "$myStack"
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

                if (isModify) {
                    val checkedStack = detailData.stackList
                    for (stack in checkedStack) {
                        if (stack == name) {
                            isChecked = true
                            checkedChips.add(this)
                            checkedStackView.addItem(name)
                        }
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
                120
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

                text = "$myStack"
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

                if (isModify) {
                    val checkedStack = detailData.leader.stack
                    if (checkedStack == name) {
                        isChecked = true
                        checkedOwnerChips.add(this)
                        checkedOwnerStackView.addItem(name)
                    }
                }

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
                120
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

            text = "$myStack"
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