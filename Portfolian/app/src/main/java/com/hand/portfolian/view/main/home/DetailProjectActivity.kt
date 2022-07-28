package com.hand.portfolian.view.main.home

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
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import br.tiagohm.markdownview.MarkdownView
import br.tiagohm.markdownview.css.styles.Github
import com.bumptech.glide.Glide
import com.hand.portfolian.R
import com.hand.portfolian.data.*
import com.hand.portfolian.databinding.ActivityDetailprojectBinding
import com.hand.portfolian.network.GlobalApplication
import com.hand.portfolian.network.RetrofitClient
import com.hand.portfolian.service.ChatService
import com.hand.portfolian.service.ProjectService
import com.hand.portfolian.view.ReportProjectDialog
import com.hand.portfolian.view.main.chat.ChatRoomActivity
import com.hand.portfolian.view.main.user.OtherActivity
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.math.roundToInt

class DetailProjectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailprojectBinding

    private lateinit var retrofit: Retrofit
    private lateinit var bookmarkService: ProjectService
    private lateinit var chatService: ChatService

    private lateinit var detailProject: DetailProjectResponse
    private lateinit var toolbar: Toolbar
    private lateinit var title: TextView
    private lateinit var view: TextView
    private lateinit var userStackView: FlexboxLayout
    private lateinit var ownerStackView: FlexboxLayout
    private lateinit var capacity: TextView
    private lateinit var subjectDescription: TextView
    private lateinit var projectTime: TextView
    private lateinit var recruitmentCondition: TextView
    private lateinit var progress: TextView
    private lateinit var description: MarkdownView
    private lateinit var ownerName: TextView
    private lateinit var bookmark: ToggleButton
    private lateinit var photo: CircleImageView
    private lateinit var createAt: TextView
    private lateinit var userProfile: ConstraintLayout
    private lateinit var dynamic: AppCompatButton

    private lateinit var checkedChips: MutableList<Chip>
    private var myStack: String = ""
    private var myColor: Int = 0
    private var mStyle = Github()

    private var ownerStatusFlag = false
    private lateinit var projectId: String
    private lateinit var userId: String


    private var status = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailprojectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        projectId = intent.getStringExtra("projectId").toString()
        userId = intent.getStringExtra("userId").toString()

        Log.e("projectId", "$projectId")
        if (intent.hasExtra("OwnerStatus")) {
            val status = intent.getIntExtra("OwnerStatus", 0)

            ownerStatusFlag = status == 1
        }

        detailProject = DetailData.detailData!!
        status = detailProject.status

        dynamic = binding.btnDynamic

        initRetrofit()
        initToolbar()
        initView()

    }

    override fun onBackPressed() {
        super.onBackPressed()

        DetailData.detailData = null
    }

    private fun initToolbar() {
        toolbar = binding.toolbarDetailProject

        if (!ownerStatusFlag) {
            toolbar.menu[1].isVisible = false
            toolbar.menu[2].isVisible = false
        } else {
            toolbar.menu[0].isVisible = false
        }
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.toolbar_Report -> {
                    val reportDialog = ReportProjectDialog(this, "$projectId")
                    reportDialog.showDialog()

                    true
                }
                R.id.toolbar_Modify -> {
                    val intent = Intent(this, NewProjectActivity::class.java)
                    intent.putExtra("projectId", "$projectId")

                    startActivity(intent)
                    true
                }
                R.id.toolbar_Delete -> {
                    deleteProject()
                    finish()
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
        toolbar.setNavigationOnClickListener {
            DetailData.detailData = null
            finish()
        }
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        bookmarkService = retrofit.create(ProjectService::class.java)
        chatService = retrofit.create(ChatService::class.java)
    }

    private fun initView() {
        title = binding.tvTitle
        title.text = detailProject.title


        view = binding.tvView
        view.text = "조회 " + "${detailProject.view}"

        userStackView = binding.fblUserStack
        ownerStackView = binding.fblOwnerStack

        initStackView()

        capacity = binding.tvCapacity
        capacity.text = detailProject.capacity.toString()

        subjectDescription = binding.tvSubjectDescription
        subjectDescription.text = detailProject.contents.subjectDescription

        projectTime = binding.tvProjectTime
        projectTime.text = detailProject.contents.projectTime

        recruitmentCondition = binding.tvRecruitmentCondition
        recruitmentCondition.text = detailProject.contents.recruitmentCondition

        progress = binding.tvProgress
        progress.text = detailProject.contents.progress

        description = binding.mdDescription

        description.loadMarkdown("${detailProject.contents.description}")




        ownerName = binding.tvOwnerName
        ownerName.text = detailProject.leader.nickName

        bookmark = binding.toggleBookmark

        if (ownerStatusFlag) {
            bookmark.visibility = View.INVISIBLE
        } else {
            bookmark.isChecked = detailProject.bookMark

            bookmark.setOnCheckedChangeListener { buttonView, isChecked ->
                var bookmarkJson = SetBookmarkRequest(detailProject.projectId, isChecked)
                val setBookmark = bookmarkService.setBookmark(
                    "Bearer ${GlobalApplication.prefs.accessToken}",
                    "${GlobalApplication.prefs.userId}",
                    bookmarkJson
                )

                setBookmark.enqueue(object : Callback<SetBookmarkResponse> {
                    override fun onResponse(
                        callback: Call<SetBookmarkResponse>,
                        response: Response<SetBookmarkResponse>
                    ) {
                        if (response.isSuccessful) {
                        }
                    }

                    override fun onFailure(call: Call<SetBookmarkResponse>, t: Throwable) {
                        Log.e("SetBookmark:: ", "$t")
                    }
                })
            }
        }


        photo = binding.cvOwnerProfile

        if (detailProject.leader.photo.isEmpty()) {
            photo.setImageDrawable(getDrawable(R.drawable.avatar_1_raster))
        } else {
            Glide.with(this)
                .load(detailProject.leader.photo)
                .into(photo)
        }

        createAt = binding.tvCreateAt
        createAt.text = detailProject.createdAt.substring(0, 10)

        userProfile = binding.clProfileAndName

        userProfile.setOnClickListener {
            val intent = Intent(this, OtherActivity::class.java)
            intent.putExtra("userId", "${detailProject.leader.userId}")
            startActivity(intent)
        }

        if (ownerStatusFlag) {
            if (status == 0) {
                dynamic.text = "모집완료"
                dynamic.setTextColor(ContextCompat.getColor(this, R.color.white))
                dynamic.background =
                    ContextCompat.getDrawable(this, R.drawable.background_bottom_button2)
            } else {
                dynamic.text = "모집진행"
                dynamic.setTextColor(ContextCompat.getColor(this, R.color.white))
                dynamic.background =
                    ContextCompat.getDrawable(this, R.drawable.background_bottom_button3)
            }

        } else {
            dynamic.text = "채팅하기"

            if (status == 0) {
                dynamic.setTextColor(ContextCompat.getColor(this, R.color.thema))
                dynamic.background =
                    ContextCompat.getDrawable(this, R.drawable.background_bottom_button)
            } else {
                dynamic.setTextColor(ContextCompat.getColor(this, R.color.white))
                dynamic.background =
                    ContextCompat.getDrawable(this, R.drawable.background_bottom_button3)
                dynamic.isEnabled = false
            }
        }

        dynamic.setOnClickListener {
            if (ownerStatusFlag) {
                if (status == 0) {
                    var statusClass = ModifyProjectStatusRequest(1)

                    val modifyProjectStatus = bookmarkService.modifyProjectStatus(
                        "Bearer ${GlobalApplication.prefs.accessToken}",
                        "${detailProject.projectId}",
                        statusClass
                    )

                    modifyProjectStatus.enqueue(object : Callback<ModifyProjectStatusResponse> {
                        override fun onResponse(
                            call: Call<ModifyProjectStatusResponse>,
                            response: Response<ModifyProjectStatusResponse>
                        ) {
                            if (response.isSuccessful) {
                                val code = response.body()!!.code
                                val message = response.body()!!.message


                                dynamic.text = "모집집행"
                                dynamic.background = ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.background_bottom_button3
                                )
                                status = 1
                            }
                        }

                        override fun onFailure(
                            call: Call<ModifyProjectStatusResponse>,
                            t: Throwable
                        ) {
                            Log.e("modifyStatus: ", "$t")
                        }
                    })
                } else {
                    var statusClass = ModifyProjectStatusRequest(0)

                    val modifyProjectStatus = bookmarkService.modifyProjectStatus(
                        "Bearer ${GlobalApplication.prefs.accessToken}",
                        "${detailProject.projectId}",
                        statusClass
                    )

                    modifyProjectStatus.enqueue(object : Callback<ModifyProjectStatusResponse> {
                        override fun onResponse(
                            call: Call<ModifyProjectStatusResponse>,
                            response: Response<ModifyProjectStatusResponse>
                        ) {
                            if (response.isSuccessful) {
                                val code = response.body()!!.code
                                val message = response.body()!!.message


                                dynamic.text = "모집완료"
                                dynamic.background = ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.background_bottom_button2
                                )
                                status = 0
                            }
                        }

                        override fun onFailure(
                            call: Call<ModifyProjectStatusResponse>,
                            t: Throwable
                        ) {
                            Log.e("modifyStatus: ", "$t")
                        }
                    })
                }
            } else {

                val chatData = CreateChatRequest("$userId", "$projectId")

                val createChat = chatService.createChat(
                    "Bearer ${GlobalApplication.prefs.accessToken}",
                    chatData
                )

                createChat.enqueue(object : Callback<CreateChatResponse> {
                    override fun onResponse(
                        call: Call<CreateChatResponse>,
                        response: Response<CreateChatResponse>
                    ) {
                        if (response.isSuccessful) {
                            val code = response.body()!!.code
                            val message = response.body()!!.message
                            val chatRoomId = response.body()!!.chatRoomId


                            val intent =
                                Intent(this@DetailProjectActivity, ChatRoomActivity::class.java)
                            intent.putExtra("chatRoomId", "$chatRoomId")
                            intent.putExtra("receiver", "${detailProject.leader.userId}")
                            intent.putExtra("photo", "${detailProject.leader.photo}")
                            intent.putExtra("title", "${detailProject.title}")
                            intent.putExtra("nickName", "${detailProject.leader.nickName}")
                            startActivity(intent)
                        }
                    }

                    override fun onFailure(call: Call<CreateChatResponse>, t: Throwable) {
                        Log.e("createChat: ", "$t")
                    }
                })
            }
        }
    }

    private fun deleteProject() {
        val deleteProject = bookmarkService.deleteProject(
            "Bearer ${GlobalApplication.prefs.accessToken}",
            projectId
        )

        deleteProject.enqueue(object : Callback<ModifyProjectResponse> {
            override fun onResponse(
                call: Call<ModifyProjectResponse>,
                response: Response<ModifyProjectResponse>
            ) {
                if (response.isSuccessful) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message

                }
            }

            override fun onFailure(call: Call<ModifyProjectResponse>, t: Throwable) {
                Log.e("deleteProject: ", "$t")
            }
        })
    }

    private fun initStackView() {
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

        userStackView.addItems(detailProject.stackList)
        ownerStackView.addItem(detailProject.leader.stack)
    }

    //flexbox layout 아이템 동적추가
    private fun FlexboxLayout.addItems(names: List<String>) {
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

                isChecked = true
                isClickable = false
            }

            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                120
            )

            layoutParams.rightMargin = 10
            addView(chip, layoutParams)
        }
    }


    private fun FlexboxLayout.addItem(name: String) {
        checkedChips = mutableListOf()

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

            isChecked = true
            isClickable = false
        }

        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.MarginLayoutParams.WRAP_CONTENT,
            120
        )

        layoutParams.rightMargin = 10
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