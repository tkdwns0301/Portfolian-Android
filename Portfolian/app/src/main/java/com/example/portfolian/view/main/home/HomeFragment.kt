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
import android.widget.ToggleButton
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
    private lateinit var btn_AllNonClick: Button
    private lateinit var btn_Close: ImageButton
    private lateinit var btn_NewProject: ImageButton
    private lateinit var btn_Front: ToggleButton
    private lateinit var btn_Back: ToggleButton
    private lateinit var btn_React: ToggleButton
    private lateinit var btn_Vue: ToggleButton
    private lateinit var btn_Spring: ToggleButton
    private lateinit var btn_Django: ToggleButton
    private lateinit var btn_iOS: ToggleButton
    private lateinit var btn_Typescript: ToggleButton
    private lateinit var btn_Javascript: ToggleButton
    private lateinit var btn_Android: ToggleButton
    private lateinit var btn_Angular: ToggleButton
    private lateinit var btn_HTML: ToggleButton
    private lateinit var btn_Flask: ToggleButton
    private lateinit var btn_Node: ToggleButton
    private lateinit var btn_Java: ToggleButton
    private lateinit var btn_Python: ToggleButton
    private lateinit var btn_Csharp: ToggleButton
    private lateinit var btn_Kotlin: ToggleButton
    private lateinit var btn_Swift: ToggleButton
    private lateinit var btn_Go: ToggleButton
    private lateinit var btn_C: ToggleButton
    private lateinit var btn_Design: ToggleButton
    private lateinit var btn_Figma: ToggleButton
    private lateinit var btn_Sketch: ToggleButton
    private lateinit var btn_Git: ToggleButton
    private lateinit var btn_Adobe: ToggleButton
    private lateinit var btn_Photoshop: ToggleButton
    private lateinit var btn_Illustrator: ToggleButton
    private lateinit var btn_Firebase: ToggleButton
    private lateinit var btn_Aws: ToggleButton
    private lateinit var btn_Gcp: ToggleButton
    private lateinit var btn_Ect: ToggleButton

    private lateinit var btnArray: ArrayList<ToggleButton>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSwipeRefreshLayout(view)
        initRecyclerView(view)
        initToolbar(view)
        initDrawer(view)
        initNewProject(view)
        initToggleButton(view)
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
        btn_AllNonClick = view.findViewById(R.id.btn_AllNonClick)

        btn_AllClick.setOnClickListener {
            for (i in btnArray) {
                if (!i.isChecked) {
                    i.isChecked = true
                }
            }
        }

        btn_AllNonClick.setOnClickListener {
            for (i in btnArray) {
                if(i.isChecked) {
                    i.isChecked = false
                }
            }
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

    private fun initToggleButton(view: View) {
        btn_Front = view.findViewById(R.id.btn_Front)
        btn_Back = view.findViewById(R.id.btn_Back)
        btn_React = view.findViewById(R.id.btn_React)
        btn_Vue = view.findViewById(R.id.btn_Vue)
        btn_Spring = view.findViewById(R.id.btn_Spring)
        btn_Django = view.findViewById(R.id.btn_Django)
        btn_iOS = view.findViewById(R.id.btn_iOS)
        btn_Typescript = view.findViewById(R.id.btn_Typescript)
        btn_Javascript = view.findViewById(R.id.btn_Javascript)
        btn_Android = view.findViewById(R.id.btn_Android)
        btn_Angular = view.findViewById(R.id.btn_Angular)
        btn_HTML = view.findViewById(R.id.btn_HTML_CSS)
        btn_Flask = view.findViewById(R.id.btn_Flask)
        btn_Node = view.findViewById(R.id.btn_Node_js)
        btn_Java = view.findViewById(R.id.btn_Java)
        btn_Python = view.findViewById(R.id.btn_Python)
        btn_Csharp = view.findViewById(R.id.btn_Csharp)
        btn_Kotlin = view.findViewById(R.id.btn_Kotlin)
        btn_Swift = view.findViewById(R.id.btn_Swift)
        btn_Go = view.findViewById(R.id.btn_Go)
        btn_C = view.findViewById(R.id.btn_C_Cpp)
        btn_Design = view.findViewById(R.id.btn_Design)
        btn_Figma = view.findViewById(R.id.btn_Figma)
        btn_Sketch = view.findViewById(R.id.btn_Sketch)
        btn_Git = view.findViewById(R.id.btn_Git)
        btn_Adobe = view.findViewById(R.id.btn_AdobeXD)
        btn_Photoshop = view.findViewById(R.id.btn_Photoshop)
        btn_Illustrator = view.findViewById(R.id.btn_illustrator)
        btn_Firebase = view.findViewById(R.id.btn_Firebase)
        btn_Aws = view.findViewById(R.id.btn_AWS)
        btn_Gcp = view.findViewById(R.id.btn_GCP)
        btn_Ect = view.findViewById(R.id.btn_ect)


        btnArray = arrayListOf(
            btn_Front,
            btn_Back,
            btn_React,
            btn_Vue,
            btn_Spring,
            btn_Django,
            btn_iOS,
            btn_Typescript,
            btn_Javascript,
            btn_Android,
            btn_Angular,
            btn_HTML,
            btn_Flask,
            btn_Node,
            btn_Java,
            btn_Python,
            btn_Csharp,
            btn_Kotlin,
            btn_Swift,
            btn_Go,
            btn_C,
            btn_Design,
            btn_Figma,
            btn_Sketch,
            btn_Git,
            btn_Adobe,
            btn_Photoshop,
            btn_Illustrator,
            btn_Firebase,
            btn_Aws,
            btn_Gcp,
            btn_Ect
        )

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