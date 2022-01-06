package com.example.portfolian.view.main.bookmark

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.portfolian.R
import com.example.portfolian.adapter.ProjectAdapter
import com.example.portfolian.data.Project
import com.example.portfolian.data.ReadProjectResponse
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.ProjectService
import com.example.portfolian.service.TokenService
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.math.roundToInt

class BookmarkFragment : Fragment(R.layout.fragment_bookmark) {
    private lateinit var retrofit: Retrofit
    private lateinit var bookmarkService: ProjectService
    private lateinit var tokenService: TokenService

    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var drawer: DrawerLayout
    private lateinit var linearBookmark: LinearLayout
    private lateinit var allNonClick: Button
    private lateinit var close: ImageButton

    private lateinit var stackView: FlexboxLayout
    private lateinit var chips: ArrayList<Chip>
    private lateinit var checkedChips: MutableList<Chip>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
    }

    private fun init(view: View) {
        initRetrofit()
        initStackView(view)
        initSwipeRefreshLayout(view)
        initRecyclerView(view)
        initToolbar(view)
        initDrawer(view)
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        bookmarkService = retrofit.create(ProjectService::class.java)
        tokenService = retrofit.create(TokenService::class.java)
    }

    private fun initSwipeRefreshLayout(view: View) {
        swipe = view.findViewById(R.id.sl_Swipe)
        swipe.setOnRefreshListener {
            refresh()
        }
    }

    private fun initRecyclerView(view: View) {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView = view.findViewById(R.id.rv_Project)

        recyclerView.layoutManager = layoutManager
        readBookmark()
    }

    private fun initToolbar(view: View) {
        toolbar = view.findViewById(R.id.toolbar_Bookmark)

        toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.toolbar_Filter -> {
                    drawer = view.findViewById(R.id.dl_Bookmark)
                    linearBookmark = view.findViewById(R.id.ll_Drawer)
                    drawer.openDrawer(linearBookmark)

                    true
                }
                R.id.toolbar_Alert -> {
                    //TODO 눌렀을 때, 알림창 보이기
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
    }

    private fun initDrawer(view: View) {
        allNonClick = view.findViewById(R.id.btn_AllNonClick)
        close = view.findViewById(R.id.img_btn_Close)

        allNonClick.setOnClickListener {
            for (chip in chips) {
                chip.apply {
                    isChecked = false
                }
            }
        }

        close.setOnClickListener {
            readBookmark()
            drawer.closeDrawers()
        }
    }

    private fun initStackView(view: View) {
        stackView = view.findViewById(R.id.fl_Stack)
        chips = ArrayList()
        val nameArray = arrayOf(
            "ect",
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
            "AWS"
        )

        stackView.addItem(nameArray)

    }

    private fun FlexboxLayout.addItem(names: Array<String>) {
        checkedChips = mutableListOf()

        for (name in names) {
            val chip = LayoutInflater.from(context).inflate(R.layout.view_chip, null) as Chip

            chip.apply {
                var myColor = 0

                when (name) {
                    "Front-end" -> {
                        myColor = ContextCompat.getColor(context, R.color.front_end)
                    }
                    "Back-end" -> {
                        myColor = ContextCompat.getColor(context, R.color.back_end)
                    }
                    "React" -> {
                        myColor = ContextCompat.getColor(context, R.color.react)
                    }
                    "Vue" -> {
                        myColor = ContextCompat.getColor(context, R.color.vue)
                    }
                    "Spring" -> {
                        myColor = ContextCompat.getColor(context, R.color.spring)
                    }
                    "Django" -> {
                        myColor = ContextCompat.getColor(context, R.color.django)
                    }
                    "iOS" -> {
                        myColor = ContextCompat.getColor(context, R.color.ios)
                    }
                    "Typescript" -> {
                        myColor = ContextCompat.getColor(context, R.color.typescript)
                    }
                    "Javascript" -> {
                        myColor = ContextCompat.getColor(context, R.color.javascript)
                    }
                    "Android" -> {
                        myColor = ContextCompat.getColor(context, R.color.android)
                    }
                    "Angular" -> {
                        myColor = ContextCompat.getColor(context, R.color.angular)
                    }
                    "HTML/CSS" -> {
                        myColor = ContextCompat.getColor(context, R.color.html_css)
                    }
                    "Flask" -> {
                        myColor = ContextCompat.getColor(context, R.color.flask)
                    }
                    "Node.js" -> {
                        myColor = ContextCompat.getColor(context, R.color.node)
                    }
                    "Java" -> {
                        myColor = ContextCompat.getColor(context, R.color.java)
                    }
                    "Python" -> {
                        myColor = ContextCompat.getColor(context, R.color.python)
                    }
                    "C#" -> {
                        myColor = ContextCompat.getColor(context, R.color.c_sharp)
                    }
                    "Kotlin" -> {
                        myColor = ContextCompat.getColor(context, R.color.kotlin)
                    }
                    "Swift" -> {
                        myColor = ContextCompat.getColor(context, R.color.swift)
                    }
                    "Go" -> {
                        myColor = ContextCompat.getColor(context, R.color.go)
                    }
                    "C/C++" -> {
                        myColor = ContextCompat.getColor(context, R.color.c_cpp)
                    }
                    "Design" -> {
                        myColor = ContextCompat.getColor(context, R.color.design)
                    }
                    "Figma" -> {
                        myColor = ContextCompat.getColor(context, R.color.figma)
                    }
                    "Sketch" -> {
                        myColor = ContextCompat.getColor(context, R.color.sketch)
                    }
                    "Git" -> {
                        myColor = ContextCompat.getColor(context, R.color.git)
                    }
                    "AdobeXD" -> {
                        myColor = ContextCompat.getColor(context, R.color.adobexd)
                    }
                    "Photoshop" -> {
                        myColor = ContextCompat.getColor(context, R.color.photoShop)
                    }
                    "Illustrator" -> {
                        myColor = ContextCompat.getColor(context, R.color.illustrator)
                    }
                    "Firebase" -> {
                        myColor = ContextCompat.getColor(context, R.color.firebase)
                    }
                    "AWS" -> {
                        myColor = ContextCompat.getColor(context, R.color.aws)
                    }
                    "GCP" -> {
                        myColor = ContextCompat.getColor(context, R.color.gcp)
                    }
                    "ect" -> {
                        myColor = ContextCompat.getColor(context, R.color.ect)
                    }

                }

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

                setOnCheckedChangeListener { buttonView, isChecked ->
                    if(isChecked) {
                        checkedChips.add(this)
                    } else {
                        val chipIdx = checkedChips.indexOf(this)
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
            addView(chip, childCount - 1, layoutParams)
        }
    }

    private fun readBookmark() {
        var stackList = mutableListOf<String>()

        for(chip in checkedChips) {
            var stackName = chip.text.toString().trim()
            stackList.add(stackName)

            val callBookmark = bookmarkService.readAllBookmark()
            callBookmark.enqueue(object: Callback<ReadProjectResponse> {
                override fun onResponse(
                    call: Call<ReadProjectResponse>,
                    response: Response<ReadProjectResponse>
                ) {
                    if(response.isSuccessful) {
                        val bookmarkProjects = response.body()?.articleList
                        setBookmarkAdapter(bookmarkProjects)
                    }
                }

                override fun onFailure(call: Call<ReadProjectResponse>, t: Throwable) {
                    Log.e("callBookmark: ", "$t")
                }
            })
        }
    }

    private fun setBookmarkAdapter(projects: ArrayList<Project>?) {
        if (projects != null) {
            //adapter 연결하기
        }
    }

    private fun refresh() {
        readBookmark()
        swipe.isRefreshing = false
    }

    private fun dpToPx(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        )
            .roundToInt()
}