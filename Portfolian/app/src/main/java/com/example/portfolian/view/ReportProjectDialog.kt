package com.example.portfolian.view

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.example.portfolian.R
import com.example.portfolian.data.ReportRequest
import com.example.portfolian.data.ReportResponse
import com.example.portfolian.network.GlobalApplication
import com.example.portfolian.network.RetrofitClient
import com.example.portfolian.service.ProjectService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ReportProjectDialog(context: Context, projectId: String) {
    private var mContext = context
    private val dialog = Dialog(mContext)
    private val projectId = projectId
    private lateinit var retrofit: Retrofit
    private lateinit var projectService: ProjectService

    private lateinit var report1: TextView
    private lateinit var report2: TextView
    private lateinit var report3: TextView
    private lateinit var report4: TextView
    private lateinit var report5: TextView
    private lateinit var report6: TextView
    private lateinit var report7: TextView


    fun showDialog() {
        initRetrofit()

        dialog.setContentView(R.layout.dialog_report_project)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        report1 = dialog.findViewById(R.id.tv_Report1)
        report2 = dialog.findViewById(R.id.tv_Report2)
        report3 = dialog.findViewById(R.id.tv_Report3)
        report4 = dialog.findViewById(R.id.tv_Report4)
        report5 = dialog.findViewById(R.id.tv_Report5)
        report6 = dialog.findViewById(R.id.tv_Report6)
        report7 = dialog.findViewById(R.id.tv_Report7)

        report1.setOnClickListener {
            sendReport("${report1.text}")
        }
        report2.setOnClickListener {
            sendReport("${report2.text}")
        }
        report3.setOnClickListener {
            sendReport("${report3.text}")
        }
        report4.setOnClickListener {
            sendReport("${report4.text}")
        }
        report5.setOnClickListener {
            sendReport("${report5.text}")
        }
        report6.setOnClickListener {
            sendReport("${report6.text}")
        }
        report7.setOnClickListener {
            sendReport("${report7.text}")
        }

    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        projectService = retrofit.create(ProjectService::class.java)
    }

    private fun sendReport(msg: String) {
        val reason = ReportRequest(msg)
        val report = projectService.reportProject("Bearer ${GlobalApplication.prefs.accessToken}", "$projectId", reason)

        report.enqueue(object: Callback<ReportResponse> {
            override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
                if(response.isSuccessful) {
                    val code = response.body()!!.code
                    val message = response.body()!!.message
                    val reportId = response.body()!!.reportId

                    Log.e("Report Project: ", "$code: $message, $reportId")

                    if(!reportId.isNullOrEmpty()) {
                        Toast.makeText(mContext, "$message: reportId: $reportId", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(mContext, "$message", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                Log.e("Report Project: ", "$t")
                Toast.makeText(mContext, "신고하는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }

        })

        dialog.dismiss()

    }




}