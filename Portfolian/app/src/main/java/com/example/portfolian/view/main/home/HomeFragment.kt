package com.example.portfolian.view.main.home

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.portfolian.R
import com.example.portfolian.adapter.ProjectAdapter
import com.example.portfolian.data.Project
import com.google.android.material.chip.Chip

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var rv_Project: RecyclerView
    private lateinit var adapter: ProjectAdapter
    private lateinit var sl_Swipe: SwipeRefreshLayout
    private lateinit var dl_Main: DrawerLayout
    private lateinit var cl_Drawer: LinearLayout
    private lateinit var toolbar: Toolbar
    private lateinit var btn_AllClick: Button
    private lateinit var btn_Close: ImageButton
    private lateinit var btn_NewProject: ImageButton


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSwipeRefreshLayout(view)
        initRecyclerView(view)
        initToolbar(view)
        initDrawer(view)
        initNewProject(view)
        initChip(view)
    }


    private fun initSwipeRefreshLayout(view: View) {
        sl_Swipe = view.findViewById(R.id.sl_Swipe)
        sl_Swipe.setOnRefreshListener {
            refresh()
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
                    Log.d("HomeFragment: ", "Filter Button Click")
                    dl_Main = view.findViewById(R.id.dl_Main)
                    cl_Drawer = requireView().findViewById(R.id.ll_Drawer)
                    dl_Main.openDrawer(cl_Drawer)


                    true
                }
                R.id.toolbar_Alert -> {
                    Log.d("HomeFragment: ", "Alert Button Click")
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
    }

    private fun initDrawer(view: View) {
        btn_AllClick = view.findViewById(R.id.btn_AllClick)

        btn_AllClick.setOnClickListener {
            Log.d("click: ", "success")
        }

        btn_Close = view.findViewById(R.id.img_btn_Close)
        btn_Close.setOnClickListener {
            Log.d("Close", "success")
            dl_Main.closeDrawers()
        }
    }

    private fun initNewProject(view: View) {
        btn_NewProject = view.findViewById(R.id.btn_NewProject)

        btn_NewProject.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_newProjectFragment)
        }
    }

    private fun initChip(view: View) {


    }


    private fun readProject() {
        //TODO retrofit 으로 데이터 받아와서 표시

        //setProjectAdapter()
    }

    private fun setProjectAdapter(projects: ArrayList<Project>?) {
        if (projects != null) {
            adapter = ProjectAdapter(requireContext(), projects, 0)
            rv_Project.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun refresh() {
        readProject()
        sl_Swipe.isRefreshing = false
    }


}