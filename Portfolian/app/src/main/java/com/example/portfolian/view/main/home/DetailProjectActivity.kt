package com.example.portfolian.view.main.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.portfolian.R
import com.example.portfolian.data.DetailContent
import retrofit2.Retrofit
import us.feras.mdv.MarkdownView

class DetailProjectActivity: AppCompatActivity() {
    private lateinit var markdown: MarkdownView
    private lateinit var retrofit: Retrofit
    private lateinit var detailProject: DetailContent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailproject_user)

        init()
    }

    private fun init() {
        detailProject = intent.getParcelableExtra("detailProject")!!
        initMarkdown()
    }

    private fun initRetrofit() {

    }

    private fun initView() {
        initMarkdown()
    }

    private fun initMarkdown() {
        markdown = findViewById(R.id.markdownView)
        markdown.loadMarkdown("${detailProject.description}")
    }
}