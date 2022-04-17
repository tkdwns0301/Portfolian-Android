package com.example.portfolian.view.main.bookmark

import android.app.FragmentManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.portfolian.R
import com.example.portfolian.adapter.ProjectAdapter
import com.example.portfolian.data.Project
import com.example.portfolian.data.ReadProjectResponse
import com.example.portfolian.databinding.FragmentBookmarkBinding
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.ProjectService
import com.example.portfolian.service.TokenService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class BookmarkFragment : Fragment(R.layout.fragment_bookmark) {
    private lateinit var binding: FragmentBookmarkBinding

    private lateinit var retrofit: Retrofit
    private lateinit var bookmarkService: ProjectService
    private lateinit var tokenService: TokenService

    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar

    private lateinit var adapter: ProjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }


    private fun init() {
        initRetrofit()
        initView()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        bookmarkService = retrofit.create(ProjectService::class.java)
        tokenService = retrofit.create(TokenService::class.java)
    }

    private fun initView() {
        swipe = binding.slSwipe
        recyclerView = binding.rvProject
        toolbar = binding.toolbarBookmark

        initSwipeRefreshLayout()
        initRecyclerView()
        initToolbar()
    }

    private fun initSwipeRefreshLayout() {
        swipe.setOnRefreshListener {
            refresh()
        }
    }

    private fun initRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        recyclerView.layoutManager = layoutManager
        readBookmark()
    }

    private fun initToolbar() {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.toolbar_Alert -> {
                    //TODO 눌렀을 때, 알림창 보이기
                    readBookmark()
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
    }


    private fun readBookmark() {
        val callBookmark = bookmarkService.readAllBookmark(
            "Bearer ${GlobalApplication.prefs.accessToken}",
            "${GlobalApplication.prefs.userId}"
        )

        callBookmark.enqueue(object : Callback<ReadProjectResponse> {
            override fun onResponse(
                call: Call<ReadProjectResponse>,
                response: Response<ReadProjectResponse>
            ) {
                if (response.isSuccessful) {
                    val bookmarkProjects = response.body()?.articleList
                    setBookmarkAdapter(bookmarkProjects)
                }
            }

            override fun onFailure(call: Call<ReadProjectResponse>, t: Throwable) {
                Log.e("callBookmark: ", "$t")
            }
        })
    }

    private fun setBookmarkAdapter(projects: ArrayList<Project>?) {
        if (projects != null) {
            //adapter 연결하기
            adapter = ProjectAdapter(requireContext(), projects, 0)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun refresh() {
        readBookmark()
        swipe.isRefreshing = false
    }
}