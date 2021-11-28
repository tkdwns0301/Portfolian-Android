package com.example.portfolian.view.main.home

import android.content.Context
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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.portfolian.R
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.ProjectService
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import retrofit2.Retrofit
import kotlin.math.roundToInt

class NewProjectActivity :AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var projectService: ProjectService

    private lateinit var ll_StackChoice: LinearLayout
    private lateinit var StackView: FlexboxLayout
    private lateinit var dl_NewProject: DrawerLayout
    private lateinit var ll_Drawer: LinearLayout
    private lateinit var btn_AllNonClick: Button
    private lateinit var btn_Close: ImageButton
    private lateinit var chips: ArrayList<Chip>
    private lateinit var checkedChips: MutableList<Chip>
    private var myColor: Int = 0
    private lateinit var checkedStackView: FlexboxLayout
    private lateinit var toolbar: Toolbar
    private lateinit var et_Title: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newproject)

        initRetrofit()
        initToolbar()
        initDrawer()
        initStackView()
        initStackChoice()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        projectService = retrofit.create(ProjectService::class.java)
    }
    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar_NewProject)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.toolbar_Save -> {
                    //TODO 저장 버튼을 눌렀을 때 게시물 업로드
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

    private fun initDrawer() {
        btn_AllNonClick = findViewById(R.id.btn_AllNonClick)

        btn_AllNonClick.setOnClickListener {
            for (chip in chips) {
                chip.apply {
                    isChecked = false
                }
            }
            checkedStackView.removeAllViews()
        }

        btn_Close = findViewById(R.id.img_btn_Close)
        btn_Close.setOnClickListener {
            Log.d("Close", "success")
            dl_NewProject.closeDrawers()
        }
    }

    private fun initStackChoice() {
        ll_StackChoice = findViewById(R.id.ll_StackChoice)

        ll_StackChoice.setOnClickListener {
            dl_NewProject = findViewById(R.id.dl_NewProject)
            ll_Drawer = findViewById(R.id.ll_Drawer)
            dl_NewProject.openDrawer(ll_Drawer)
        }
    }

    private fun initStackView() {
        StackView = findViewById(R.id.fl_Stack)
        checkedStackView = findViewById(R.id.fbl_CheckedStack)
        chips = ArrayList()
        val nameArray = arrayOf(
            "Front-end",
            "Back-end",
            "C#",
            "React",
            "Vue",
            "Spring",
            "Django",
            "Javascript",
            "Git",
            "Typescript",
            "iOS",
            "Android",
            "Angular",
            "HTML/CSS",
            "Flask",
            "Node.js",
            "Java",
            "Go",
            "Python",
            "Kotlin",
            "Swift",
            "C/C++",
            "Design",
            "Figma",
            "Sketch",
            "AdobeXD",
            "GCP",
            "Photoshop",
            "Illustrator",
            "Firebase",
            "AWS",
            "ect"
        )

        StackView.addItems(nameArray)


    }

    private fun FlexboxLayout.addItems(names: Array<String>) {
        checkedChips = mutableListOf()

        for (name in names) {
            val chip = LayoutInflater.from(context).inflate(R.layout.view_chip, null) as Chip

            chip.apply {
                stackColor(name)

                text = "  $name  "
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
                        Log.d("idx: ", "$chipIdx")
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
            chips.add(chip)
            addView(chip, layoutParams)
        }


    }

    private fun FlexboxLayout.addItem(name: String) {
        val chip = LayoutInflater.from(context).inflate(R.layout.view_chip, null) as Chip
        chip.apply {
            stackColor(name)

            text = "  $name  "
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
            "Front-end" -> {
                myColor = ContextCompat.getColor(this, R.color.front_end)
            }
            "Back-end" -> {
                myColor = ContextCompat.getColor(this, R.color.back_end)
            }
            "React" -> {
                myColor = ContextCompat.getColor(this, R.color.react)
            }
            "Vue" -> {
                myColor = ContextCompat.getColor(this, R.color.vue)
            }
            "Spring" -> {
                myColor = ContextCompat.getColor(this, R.color.spring)
            }
            "Django" -> {
                myColor = ContextCompat.getColor(this, R.color.django)
            }
            "iOS" -> {
                myColor = ContextCompat.getColor(this, R.color.ios)
            }
            "Typescript" -> {
                myColor = ContextCompat.getColor(this, R.color.typescript)
            }
            "Javascript" -> {
                myColor = ContextCompat.getColor(this, R.color.javascript)
            }
            "Android" -> {
                myColor = ContextCompat.getColor(this, R.color.android)
            }
            "Angular" -> {
                myColor = ContextCompat.getColor(this, R.color.angular)
            }
            "HTML/CSS" -> {
                myColor = ContextCompat.getColor(this, R.color.html_css)
            }
            "Flask" -> {
                myColor = ContextCompat.getColor(this, R.color.flask)
            }
            "Node.js" -> {
                myColor = ContextCompat.getColor(this, R.color.node)
            }
            "Java" -> {
                myColor = ContextCompat.getColor(this, R.color.java)
            }
            "Python" -> {
                myColor = ContextCompat.getColor(this, R.color.python)
            }
            "C#" -> {
                myColor = ContextCompat.getColor(this, R.color.c_sharp)
            }
            "Kotlin" -> {
                myColor = ContextCompat.getColor(this, R.color.kotlin)
            }
            "Swift" -> {
                myColor = ContextCompat.getColor(this, R.color.swift)
            }
            "Go" -> {
                myColor = ContextCompat.getColor(this, R.color.go)
            }
            "C/C++" -> {
                myColor = ContextCompat.getColor(this, R.color.c_cpp)
            }
            "Design" -> {
                myColor = ContextCompat.getColor(this, R.color.design)
            }
            "Figma" -> {
                myColor = ContextCompat.getColor(this, R.color.figma)
            }
            "Sketch" -> {
                myColor = ContextCompat.getColor(this, R.color.sketch)
            }
            "Git" -> {
                myColor = ContextCompat.getColor(this, R.color.git)
            }
            "AdobeXD" -> {
                myColor = ContextCompat.getColor(this, R.color.adobexd)
            }
            "Photoshop" -> {
                myColor = ContextCompat.getColor(this, R.color.photoShop)
            }
            "Illustrator" -> {
                myColor = ContextCompat.getColor(this, R.color.illustrator)
            }
            "Firebase" -> {
                myColor = ContextCompat.getColor(this, R.color.firebase)
            }
            "AWS" -> {
                myColor = ContextCompat.getColor(this, R.color.aws)
            }
            "GCP" -> {
                myColor = ContextCompat.getColor(this, R.color.gcp)
            }
            "ect" -> {
                myColor = ContextCompat.getColor(this, R.color.ect)
            }

        }
    }

    private fun dpToPx(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        )
            .roundToInt()
}