package com.example.portfolian.view.main.home

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.portfolian.R
import com.example.portfolian.adapter.ProjectAdapter
import com.example.portfolian.data.Project
import com.example.portfolian.data.ReadProjectResponse
import com.example.portfolian.data.RenewalTokenRequest
import com.example.portfolian.data.TokenResponse
import com.example.portfolian.network.GlobalApplication
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
import android.os.Parcelable
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.portfolian.databinding.FragmentHomeBinding
import com.example.portfolian.network.SocketApplication


class HomeFragment : Fragment(R.layout.fragment_home), LifecycleObserver{
    private lateinit var binding: FragmentHomeBinding

    private lateinit var retrofit: Retrofit
    private lateinit var projectService: ProjectService
    private lateinit var tokenService: TokenService

    private lateinit var stackView: FlexboxLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var linearLayout: LinearLayout
    private lateinit var toolbar: Toolbar
    private lateinit var allNonClick: Button
    private lateinit var close: ImageButton
    private lateinit var newProject: ImageButton
    private lateinit var chips: ArrayList<Chip>
    private lateinit var searchView: SearchView
    private lateinit var orderRadioGroup: RadioGroup
    private lateinit var noneProject: TextView

    private lateinit var adapter: ProjectAdapter

    private lateinit var checkedChips: MutableList<Chip>
    private lateinit var nameMap: Map<String, String>

    private var search = ""
    private var radio = "default"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        init()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        return binding.root
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun appOnStop() {
        Log.e("HomeFragment: ", "onResume")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun appOnCreate() {
        Log.e("HomeFragment: ", "onResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun appOnDestroy() {
        Log.e("HomeFragment: ", "onResume")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun appOnPause() {
        Log.e("HomeFragment: ", "onResume")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun appOnResume() {
        Log.e("HomeFragment: ", "onResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun appOnStart() {
        Log.e("HomeFragment: ", "onStart")
    }

    override fun onResume() {
        super.onResume()
        //Log.e("HomeFragment: ", "onResume")
        SocketApplication.getSocket().off("chat:receive")
    }

    private fun init() {
        initRetrofit()
        initView()
    }

    private fun initView() {
        stackView = binding.flStack
        recyclerView = binding.drawerHome.rvProject
        swipe = binding.drawerHome.slSwipe
        drawerLayout = binding.dlHome
        linearLayout = binding.llDrawer
        toolbar = binding.drawerHome.toolbarHome
        allNonClick = binding.btnAllNonClick
        close = binding.imgBtnClose
        newProject = binding.drawerHome.btnNewProject
        searchView = binding.drawerHome.searchView
        orderRadioGroup = binding.drawerHome.rgOrder
        noneProject = binding.drawerHome.tvNoneProject

        initSearchView()
        initRadioGroup()
        initStackView()
        initSwipeRefreshLayout()
        initRecyclerView()
        initToolbar()
        initDrawer()
        initNewProject()
    }

    private fun renewal() {

        val renewalTokenRequest = RenewalTokenRequest(
            "${GlobalApplication.prefs.userId}"
        )

        val renewalService = tokenService.getAccessToken(renewalTokenRequest)

        renewalService.enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 1) {
                        GlobalApplication.prefs.accessToken = response.body()!!.accessToken
                    } else {
                        Log.e("RenewalToken: ", "토큰갱신 오류: ${response.body()!!.message}")
                    }

                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.e("RenewalToken: ", "$t")
            }
        })
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        projectService = retrofit.create(ProjectService::class.java)
        tokenService = retrofit.create(TokenService::class.java)
    }


    private fun initSwipeRefreshLayout() {
        swipe.setOnRefreshListener {
            refresh()
        }
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                search = p0.toString()

                readProject()

                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }
        })
    }

    private fun initRadioGroup() {
        orderRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_Recent -> {
                    radio = "default"
                    readProject()
                }

                R.id.radio_View -> {
                    radio = "view"
                    readProject()
                }
            }

            readProject()
        }
    }

    private fun initRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        readProject()
    }

    private fun initToolbar() {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.toolbar_Filter -> {
                    linearLayout = requireView().findViewById(R.id.ll_Drawer)
                    drawerLayout.openDrawer(linearLayout)

                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
    }

    private fun initDrawer() {
        allNonClick.setOnClickListener {
            for (chip in chips) {
                chip.apply {
                    isChecked = false
                }
            }
        }

        close.setOnClickListener {
            readProject()
            drawerLayout.closeDrawers()
        }
    }

    private fun initNewProject() {
        newProject.setOnClickListener {
            val intent = Intent(activity, NewProjectActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initStackView() {
        chips = ArrayList()

        nameMap = mapOf<String, String>(
            "etc" to "etc",
            "Front-end" to "frontEnd",
            "Back-end" to "backEnd",
            "C#" to "cCsharp",
            "React" to "react",
            "Vue" to "vue",
            "Spring" to "spring",
            "Django" to "django",
            "Javascript" to "javascript",
            "Git" to "git",
            "Typescript" to "typescript",
            "iOS" to "ios",
            "Android" to "android",
            "Angular" to "angular",
            "HTML/CSS" to "htmlCss",
            "Flask" to "flask",
            "Node.js" to "nodeJs",
            "Java" to "java",
            "Go" to "go",
            "Python" to "python",
            "Kotlin" to "kotlin",
            "Swift" to "swift",
            "C/C++" to "cCpp",
            "Design" to "design",
            "Figma" to "figma",
            "Sketch" to "sketch",
            "AdobeXD" to "adobeXD",
            "GCP" to "gcp",
            "Photoshop" to "photoshop",
            "Illustrator" to "illustrator",
            "Firebase" to "firebase",
            "AWS" to "aws"
        )

        stackView.addItem(nameMap)

    }

    private fun FlexboxLayout.addItem(names: Map<String, String>) {
        checkedChips = mutableListOf()


        for ((key, value) in names) {
            val chip = LayoutInflater.from(context).inflate(R.layout.view_chip, null) as Chip

            chip.apply {
                var myColor = 0

                when (key) {
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

                text = "  $key  "
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
                    if (isChecked) {
                        checkedChips.add(this)
                        readProject()
                    } else {
                        val chipIdx = checkedChips.indexOf(this)
                        checkedChips.removeAt(chipIdx)
                        readProject()
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

    private fun readProject() {
        var stackList = mutableListOf<String>()

        for (chip in checkedChips) {
            var stackName = nameMap[chip.text.toString().trim()].toString()
            stackList.add(stackName)
        }

        if (stackList.isNullOrEmpty()) {
            stackList.add("default")
        }

        if (search == "")
            search = "default"

        val callProjects = projectService.readAllProject(
            "Bearer ${GlobalApplication.prefs.accessToken}",
            stackList,
            search,
            radio
        )

        callProjects.enqueue(object : Callback<ReadProjectResponse> {
            override fun onResponse(
                call: Call<ReadProjectResponse>,
                response: Response<ReadProjectResponse>
            ) {
                if (response.isSuccessful) {
                    val projects = response.body()?.articleList

                    if(projects.isNullOrEmpty()) {
                        noneProject.isVisible = true
                        recyclerView.isVisible = false
                    } else {
                        noneProject.isVisible = false
                        recyclerView.isVisible = true
                        setProjectAdapter(projects)
                    }

                }
            }

            override fun onFailure(call: Call<ReadProjectResponse>, t: Throwable) {
                Log.e("HomeFragment: ", "readProjects: $t")
            }
        })

    }

    private fun setProjectAdapter(projects: ArrayList<Project>?) {
        if (projects != null) {
            val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
            adapter = ProjectAdapter(requireContext(), projects, 0)
            recyclerView.adapter = adapter
            recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
            adapter.notifyDataSetChanged()
        }
    }

    private fun refresh() {
        renewal()
        readProject()
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