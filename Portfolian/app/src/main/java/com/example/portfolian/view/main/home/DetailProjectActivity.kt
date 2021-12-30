package com.example.portfolian.view.main.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.portfolian.R
import com.example.portfolian.data.DetailContent
import com.example.portfolian.data.DetailProjectResponse
import com.example.portfolian.databinding.FragmentBookmarkBinding
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import org.w3c.dom.Text
import retrofit2.Retrofit
import us.feras.mdv.MarkdownView
import kotlin.math.roundToInt

class DetailProjectActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var detailProject: DetailProjectResponse
    private lateinit var toolbar: Toolbar
    private lateinit var title: TextView
    private lateinit var userView: TextView
    private lateinit var stackView: FlexboxLayout
    private lateinit var capacity: TextView
    private lateinit var subjectDescription: TextView
    private lateinit var projectTime: TextView
    private lateinit var recruitmentCondition: TextView
    private lateinit var progress: TextView
    private lateinit var description: MarkdownView
    private lateinit var ownerName: TextView
    private lateinit var bookmark: ToggleButton

    private lateinit var checkedChips: MutableList<Chip>
    private var myStack: String = ""
    private var myColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailproject_user)

        init()
    }

    private fun init() {
        detailProject = intent.getParcelableExtra("detailProject")!!
        initToolbar()
        initView()
        //initMarkdown()
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar_DetailUserProject)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.toolbar_Sharing -> {
                    //TODO 공유버튼을 눌렸을 때 메뉴표시
                    Log.d("Sharing", "공유버튼을 눌렀습니다.")
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

    private fun initRetrofit() {

    }

    private fun initView() {
        title = findViewById(R.id.tv_UserTitle)
        title.text = detailProject.title

        userView = findViewById(R.id.tv_UserView)
        userView.text = "조회 " + "${detailProject.view}"

        initStackView()
        stackView.addItems(detailProject.stackList)

        capacity = findViewById(R.id.tv_UserCountedInt)
        capacity.text = detailProject.capacity.toString()

        subjectDescription = findViewById(R.id.tv_SubjectDescriptionReceive)
        subjectDescription.text = detailProject.contents.subjectDescription

        projectTime = findViewById(R.id.tv_ProjectTimeReceive)
        projectTime.text = detailProject.contents.projectTime

        recruitmentCondition = findViewById(R.id.tv_RecruitmentConditionReceive)
        recruitmentCondition.text = detailProject.contents.recruitmentCondition

        progress = findViewById(R.id.tv_ProgressReceive)
        progress.text = detailProject.contents.progress

        description = findViewById(R.id.md_Description)
        description.loadMarkdown("${detailProject.contents.description}")

        ownerName = findViewById(R.id.tv_OwnerName)
        ownerName.text = detailProject.leader.nickName

        bookmark = findViewById(R.id.toggle_Bookmark)
        bookmark.isChecked = detailProject.bookMark
    }

    private fun initStackView() {
        stackView = findViewById(R.id.fbl_UserStack)

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
    }

    //flexbox layout 아이템 동적추가
    private fun FlexboxLayout.addItems(names: List<String>) {
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