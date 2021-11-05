package com.example.portfolian.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.portfolian.R
import com.example.portfolian.data.Project
import com.google.android.flexbox.FlexboxLayout
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.NumberFormatException
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ProjectAdapter (private val context: Context, private val dataSet: ArrayList<Project>, private val state: Int) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_view_home_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = dataSet[position]

        //Title
        holder.title.text = project.title

        //Profile Image
        if(project.profile.isEmpty()) {
            holder.profile.setImageDrawable(context.getDrawable(R.drawable.avatar_1_raster))
        }else {
            //TODO Profile 이미지 가져오기
        }

        //조회수
        holder.view.text = context.getString(R.string.view, NumberFormat.getNumberInstance(Locale.KOREA).format(project?.view))


        //stack

        //stackCount
    }

    override fun getItemCount(): Int = dataSet.size

    private fun moveDetail(position: Int) {
        //TODO 상세보기 화면으로 전환
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container : ConstraintLayout = view.findViewById(R.id.cl_Project)
        val bookmark: ImageButton = view.findViewById(R.id.btn_Bookmark)
        val title: TextView = view.findViewById(R.id.tv_Title)
        val profile: CircleImageView = view.findViewById(R.id.cv_Profile)
        val view: TextView = view.findViewById(R.id.tv_View)
        val stack: FlexboxLayout = view.findViewById(R.id.fbl_Stack)
        val stackCount: TextView = view.findViewById(R.id.tv_StackCount)
    }
}