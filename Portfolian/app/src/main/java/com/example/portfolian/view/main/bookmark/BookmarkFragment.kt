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

class BookmarkFragment : Fragment(R.layout.fragment_bookmark) {
    private lateinit var retrofit: Retrofit
    private lateinit var bookmarkService: ProjectService
    private lateinit var tokenService: TokenService

    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar

    private lateinit var adapter: ProjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
    }

    private fun init(view: View) {
        initRetrofit()
        initSwipeRefreshLayout(view)
        initRecyclerView(view)
        initToolbar(view)

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
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView = view.findViewById(R.id.rv_Project)

        recyclerView.layoutManager = layoutManager
        readBookmark()
    }

    private fun initToolbar(view: View) {
        toolbar = view.findViewById(R.id.toolbar_Bookmark)

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
        val callBookmark = bookmarkService.readAllBookmark("Bearer ${GlobalApplication.prefs.accessToken}", "${GlobalApplication.prefs.userId}")

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