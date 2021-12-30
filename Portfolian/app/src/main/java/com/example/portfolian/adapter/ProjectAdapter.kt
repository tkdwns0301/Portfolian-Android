package com.example.portfolian.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.portfolian.R
import com.example.portfolian.data.DetailProjectResponse
import com.example.portfolian.data.Project
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.ProjectService
import com.example.portfolian.view.main.home.DetailProjectActivity
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class ProjectAdapter(
    private val context: Context,
    private val dataSet: ArrayList<Project>,
    private val state: Int
) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {
    private lateinit var retrofit: Retrofit
    private lateinit var projectService: ProjectService

    private lateinit var stackList: MutableList<Chip>
    private var myColor: Int = 0
    private var myStack: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_view_home_item, parent, false)
        initRetrofit()
        return ViewHolder(view)
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        projectService = retrofit.create(ProjectService::class.java)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dataSetReverse = dataSet.reversed()
        val project = dataSetReverse[position]

        //Profile Image
        if (project.leader.photo.isEmpty()) {
            holder.photo.setImageDrawable(context.getDrawable(R.drawable.avatar_1_raster))
        } else {
            holder.photo.setImageDrawable(context.getDrawable(R.drawable.avatar_1_raster))
            /*Glide.with(holder.itemView.context)
                .load(project.leader.photo)
                .into(holder.photo)*/
        }

        //Title
        holder.title.text = project.title
        Log.d("title", "${project.title}")

        //조회수
        holder.view.text = context.getString(
            R.string.view,
            NumberFormat.getNumberInstance(Locale.KOREA).format(project.view)
        )

        //stack
        holder.stack.removeAllViews()
        holder.stack.addItems(project.stackList)

        //stackCount
        if(project.stackList.size > 3)
            holder.stackCount.text = "+" + "${(project.stackList.size-3)}"
        else
            holder.stackCount.text = ""

        //bookmark
        holder.bookmark.isChecked = project.bookMark
        Log.d("bookmark", "${project.bookMark}")

        holder.container.setOnClickListener {
            moveDetail(project.projectId)
        }

    }

    //flexbox layout 아이템 동적추가
    private fun FlexboxLayout.addItems(names: List<String>) {
        var i = 0
        for (name in names) {
            val chip = LayoutInflater.from(context).inflate(R.layout.view_chip, null) as Chip

            chip.apply {
                stackColor(name)
                text = " $myStack "
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

            layoutParams.rightMargin = 12
            addView(chip, layoutParams)

            i++
            if (i == 3) break
        }
    }


    private fun stackColor(name: String) {
        when (name) {
            "frontEnd" -> {
                myColor = ContextCompat.getColor(context, R.color.front_end)
                myStack = "Front-end"
            }
            "backEnd" -> {
                myColor = ContextCompat.getColor(context, R.color.back_end)
                myStack = "Back-end"
            }
            "react" -> {
                myColor = ContextCompat.getColor(context, R.color.react)
                myStack = "React"
            }
            "vue" -> {
                myColor = ContextCompat.getColor(context, R.color.vue)
                myStack = "Vue"
            }
            "spring" -> {
                myColor = ContextCompat.getColor(context, R.color.spring)
                myStack = "Spring"
            }
            "Spring" -> {
                myColor = ContextCompat.getColor(context, R.color.spring)
                myStack = "Spring"
            }
            "django" -> {
                myColor = ContextCompat.getColor(context, R.color.django)
                myStack = "Django"
            }
            "ios" -> {
                myColor = ContextCompat.getColor(context, R.color.ios)
                myStack = "iOS"
            }
            "typescript" -> {
                myColor = ContextCompat.getColor(context, R.color.typescript)
                myStack = "Typescript"
            }
            "javascript" -> {
                myColor = ContextCompat.getColor(context, R.color.javascript)
                myStack = "Javascript"
            }
            "android" -> {
                myColor = ContextCompat.getColor(context, R.color.android)
                myStack = "Android"
            }
            "angular" -> {
                myColor = ContextCompat.getColor(context, R.color.angular)
                myStack = "Angular"
            }
            "htmlCss" -> {
                myColor = ContextCompat.getColor(context, R.color.html_css)
                myStack = "HTML/CSS"
            }
            "flask" -> {
                myColor = ContextCompat.getColor(context, R.color.flask)
                myStack = "Flask"
            }
            "nodeJs" -> {
                myColor = ContextCompat.getColor(context, R.color.node)
                myStack = "Node.js"
            }
            "java" -> {
                myColor = ContextCompat.getColor(context, R.color.java)
                myStack = "Java"
            }
            "python" -> {
                myColor = ContextCompat.getColor(context, R.color.python)
                myStack = "Python"
            }
            "cCsharp" -> {
                myColor = ContextCompat.getColor(context, R.color.c_sharp)
                myStack = "C#"
            }
            "kotlin" -> {
                myColor = ContextCompat.getColor(context, R.color.kotlin)
                myStack = "Kotlin"
            }
            "swift" -> {
                myColor = ContextCompat.getColor(context, R.color.swift)
                myStack = "Swift"
            }
            "go" -> {
                myColor = ContextCompat.getColor(context, R.color.go)
                myStack = "Go"
            }
            "cCpp" -> {
                myColor = ContextCompat.getColor(context, R.color.c_cpp)
                myStack = "C/C++"
            }
            "design" -> {
                myColor = ContextCompat.getColor(context, R.color.design)
                myStack = "Design"
            }
            "figma" -> {
                myColor = ContextCompat.getColor(context, R.color.figma)
                myStack = "Figma"
            }
            "sketch" -> {
                myColor = ContextCompat.getColor(context, R.color.sketch)
                myStack = "Sketch"
            }
            "git" -> {
                myColor = ContextCompat.getColor(context, R.color.git)
                myStack = "Git"
            }
            "adobeXD" -> {
                myColor = ContextCompat.getColor(context, R.color.adobexd)
                myStack = "AdobeXD"
            }
            "photoshop" -> {
                myColor = ContextCompat.getColor(context, R.color.photoShop)
                myStack = "Photoshop"
            }
            "illustrator" -> {
                myColor = ContextCompat.getColor(context, R.color.illustrator)
                myStack = "Illustrator"
            }
            "firebase" -> {
                myColor = ContextCompat.getColor(context, R.color.firebase)
                myStack = "Firebase"
            }
            "aws" -> {
                myColor = ContextCompat.getColor(context, R.color.aws)
                myStack = "AWS"
            }
            "gcp" -> {
                myColor = ContextCompat.getColor(context, R.color.gcp)
                myStack = "GCP"
            }
            "etc" -> {
                myColor = ContextCompat.getColor(context, R.color.ect)
                myStack = "etc"
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

    private fun moveDetail(projectId: String) {
        val callDetailProject = projectService.readDetailProject(projectId)
        callDetailProject.enqueue(object: Callback<DetailProjectResponse> {
            override fun onResponse(call: Call<DetailProjectResponse>, response: Response<DetailProjectResponse>) {
                if(response.isSuccessful) {
                    val detailProject = response.body()!!

                    val intent = Intent(context, DetailProjectActivity::class.java)
                    intent.putExtra("detailProject", detailProject)
                    context.startActivity(intent)
                }
            }

            override fun onFailure(call: Call<DetailProjectResponse>, t: Throwable) {
                Log.e("moveDetail: ", "$t")
            }
        })


    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: ConstraintLayout = view.findViewById(R.id.cl_Project)
        val bookmark: ToggleButton = view.findViewById(R.id.btn_Bookmark)
        val title: TextView = view.findViewById(R.id.tv_Title)
        val photo: CircleImageView = view.findViewById(R.id.cv_Profile)
        val view: TextView = view.findViewById(R.id.tv_View)
        val stack: FlexboxLayout = view.findViewById(R.id.fbl_Stack)
        val stackCount: TextView = view.findViewById(R.id.tv_StackCount)
    }
}