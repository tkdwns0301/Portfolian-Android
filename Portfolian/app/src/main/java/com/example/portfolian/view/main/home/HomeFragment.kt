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
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.math.roundToInt


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var retrofit: Retrofit
    private lateinit var projectService: ProjectService
    private lateinit var tokenService: TokenService

    private lateinit var StackView: FlexboxLayout
    private lateinit var rv_Project: RecyclerView
    private lateinit var adapter: ProjectAdapter
    private lateinit var sl_Swipe: SwipeRefreshLayout
    private lateinit var dl_Home: DrawerLayout
    private lateinit var ll_Drawer: LinearLayout
    private lateinit var toolbar: Toolbar
    private lateinit var btn_AllNonClick: Button
    private lateinit var btn_Close: ImageButton
    private lateinit var btn_NewProject: ImageButton
    private lateinit var chips: ArrayList<Chip>
    private lateinit var searchView: SearchView
    private lateinit var orderRadioGroup: RadioGroup

    private lateinit var checkedChips: MutableList<Chip>
    private lateinit var nameMap: Map<String, String>


    private var search = ""
    private var radio = "default"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
    }

    private fun init(view: View) {
        initRetrofit()
        initSearchView(view)
        initRadioGroup(view)
        initStackView(view)
        initSwipeRefreshLayout(view)
        initRecyclerView(view)
        initToolbar(view)
        initDrawer(view)
        initNewProject(view)

        //renewal()
    }

    private fun renewal() {
        val renewalTokenRequest = RenewalTokenRequest(
            GlobalApplication.prefs.refreshToken.toString(),
            GlobalApplication.prefs.userId.toString()
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


    private fun initSwipeRefreshLayout(view: View) {
        sl_Swipe = view.findViewById(R.id.sl_Swipe)
        sl_Swipe.setOnRefreshListener {
            refresh()
        }
    }

    private fun initSearchView(view: View) {
        searchView = view.findViewById(R.id.searchView)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
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

    private fun initRadioGroup(view: View) {
        orderRadioGroup = view.findViewById(R.id.rg_Order)

        orderRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.radio_Recent -> radio = "bookMark"

                R.id.radio_View -> radio = "view"
            }

            readProject()
        }
    }

    private fun initRecyclerView(view: View) {
        rv_Project = view.findViewById(R.id.rv_Project)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv_Project.layoutManager = layoutManager
        readProject()
    }

    private fun initToolbar(view: View) {
        toolbar = view.findViewById(R.id.toolbar_home)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.toolbar_Filter -> {
                    dl_Home = view.findViewById(R.id.dl_Home)
                    ll_Drawer = requireView().findViewById(R.id.ll_Drawer)
                    dl_Home.openDrawer(ll_Drawer)

                    true
                }
                R.id.toolbar_Alert -> {
                    Log.d("HomeFragment: ", "Alert Button Click")
                    UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                        override fun onCompleteLogout() {
                            //로그아웃에 성공하면: LoginActivity로 이동
                            Log.d("Logout::, ", "Success")
                        }
                    })
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
    }

    private fun initDrawer(view: View) {
        btn_AllNonClick = view.findViewById(R.id.btn_AllNonClick)

        btn_AllNonClick.setOnClickListener {
            for (chip in chips) {
                chip.apply {
                    isChecked = false
                }
            }
        }

        btn_Close = view.findViewById(R.id.img_btn_Close)
        btn_Close.setOnClickListener {
            readProject()
            dl_Home.closeDrawers()
        }
    }

    private fun initNewProject(view: View) {
        btn_NewProject = view.findViewById(R.id.btn_NewProject)

        btn_NewProject.setOnClickListener {
            val intent = Intent(activity, NewProjectActivity::class.java)
            startActivity(intent)
            //findNavController().navigate(R.id.action_homeFragment_to_newProjectFragment)
        }
    }

    private fun initStackView(view: View) {
        StackView = view.findViewById(R.id.fl_Stack)
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

        StackView.addItem(nameMap)

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

    private fun readProject() {
        //TODO retrofit 으로 데이터 받아와서 표시
        var stackList = mutableListOf<String>()


        for (chip in checkedChips) {
            var stackName = nameMap[chip.text.toString().trim()].toString()
            stackList.add(stackName)
        }

        if(stackList.isNullOrEmpty()) {
            stackList.add("default")
        }

        if(search == "")
            search = "default"

        val callProjects = projectService.readAllProject("Bearer ${GlobalApplication.prefs.accessToken}", stackList, search, radio)

        callProjects.enqueue(object : Callback<ReadProjectResponse> {
            override fun onResponse(
                call: Call<ReadProjectResponse>,
                response: Response<ReadProjectResponse>
            ) {
                if (response.isSuccessful) {
                    val projects = response.body()?.articleList
                    setProjectAdapter(projects)
                }
            }

            override fun onFailure(call: Call<ReadProjectResponse>, t: Throwable) {
                Log.e("HomeFragment: ", "readProjects: $t")
            }
        })

    }

    private fun setProjectAdapter(projects: ArrayList<Project>?) {
        if (projects != null) {
            adapter = ProjectAdapter(requireContext(), projects, 0)
            rv_Project.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun refresh() {
        renewal()
        readProject()
        sl_Swipe.isRefreshing = false
    }

    private fun dpToPx(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        )
            .roundToInt()
}