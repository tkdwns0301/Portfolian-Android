package com.example.portfolian.view.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.portfolian.R
import com.example.portfolian.adapter.ProjectAdapter
import com.example.portfolian.data.Project
import com.example.portfolian.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var mBinding: FragmentHomeBinding? = null
    private val binding get() = mBinding!!
    private lateinit var rv_Project: RecyclerView
    private lateinit var adapter: ProjectAdapter
    private lateinit var sl_Swipe: SwipeRefreshLayout
    private lateinit var testProject: Project

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }


    private fun init() {
        initRecyclerView()
        initSwipeRefreshLayout()

    }

    private fun initSwipeRefreshLayout() {
        sl_Swipe = binding.slSwipe
        sl_Swipe.setOnRefreshListener {
            refresh()
        }
    }
    private fun initRecyclerView() {
        rv_Project = binding.rvProject
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv_Project.layoutManager = layoutManager
        readProject()
    }

    private fun readProject() {
        //TODO retrofit 으로 데이터 받아와서 표시

        //setProjectAdapter()
    }

    private fun setProjectAdapter(projects: ArrayList<Project>?){
        if(projects != null) {
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