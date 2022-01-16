package com.example.portfolian.view.main.user

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.example.portfolian.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment(R.layout.fragment_user) {
    private lateinit var toolbar: Toolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        initToolbar(view)
    }

    private fun initToolbar(view: View) {
        toolbar = view.findViewById(R.id.userToolbar)

        toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.toolbar_Alert -> {
                    //TODO 알림 버튼 눌렀을 때

                    true
                }

                R.id.toolbar_Setting -> {
                    val intent = Intent(activity, SettingActivity::class.java)
                    startActivity(intent)

                    true
                }

                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }
    }
}