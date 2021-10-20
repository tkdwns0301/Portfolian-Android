package com.example.portfolian.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.portfolian.R
import com.example.portfolian.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState == null) {
            supportFragmentManager.commit {
                add<HomeFragment>(R.id.fragment_main)
            }
        }

        init()
    }

    private fun init() {
        bottomNavigation = findViewById(R.id.bn_main)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_Home -> {
                    moveHome()
                    true
                }
                R.id.item_Bookmark -> {
                    moveBookmark()
                    true
                }
                R.id.item_Chat -> {
                    moveChat()
                    true
                }
                R.id.item_User -> {
                    moveUser()
                    true
                }
                else -> false
            }
        }
    }

    private fun moveHome() {
        supportFragmentManager.commit {
            replace<HomeFragment>(R.id.fragment_main)
        }
    }

    private fun moveBookmark() {
        supportFragmentManager.commit {
            replace<BookmarkFragment>(R.id.fragment_main)
        }
    }

    private fun moveChat() {
        supportFragmentManager.commit {
            replace<ChatFragment>(R.id.fragment_main)
        }
    }

    private fun moveUser() {
        supportFragmentManager.commit {
            replace<UserFragment>(R.id.fragment_main)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragment_main, fragment)
        }
    }
}