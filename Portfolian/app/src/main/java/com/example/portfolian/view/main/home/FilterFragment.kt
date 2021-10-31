package com.example.portfolian.view.main.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.portfolian.R
import com.example.portfolian.view.main.MainActivity
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import kotlin.math.roundToInt


class FilterFragment : Fragment(R.layout.fragment_filter) {
    private lateinit var StackView: FlexboxLayout
    private lateinit var btn_Close: ImageButton
    private lateinit var chips: ArrayList<Chip>
    private lateinit var btn_AllNonClick: Button





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        chips = ArrayList()

        initStackView(view)
        initAllNonClick(view)
        initTopView(view)
    }

    private fun initTopView(view: View) {
        btn_Close = view.findViewById(R.id.btn_Close)
        btn_Close.setOnClickListener {

        }


    }

    private fun initAllNonClick(view: View) {
        btn_AllNonClick = view.findViewById(R.id.btn_AllNonClick)

        btn_AllNonClick.setOnClickListener {
            for(chip in chips) {
                chip.apply {
                    isChecked = false
                }
            }
        }
    }
    private fun initStackView(view: View) {
        StackView = view.findViewById(R.id.fl_Stack)

        val nameArray = arrayOf(
            "ect",
            "Front-end",
            "Back-end",
            "React",
            "Vue",
            "Spring",
            "Django",
            "Javascript",
            "Typescript",
            "iOS",
            "Android",
            "Angular",
            "HTML/CSS",
            "Flask",
            "Node.js",
            "Java",
            "Go",
            "Python",
            "Kotlin",
            "Swift",
            "C/C++",
            "C#",
            "Design",
            "Figma",
            "Sketch",
            "AdobeXD",
            "Photoshop",
            "Illustrator",
            "Firebase",
            "AWS",
            "GCP",
            "Git"
        )

        StackView.addItem(nameArray)

    }

    private fun FlexboxLayout.addItem(names: Array<String>) {
        for (name in names) {
            val chip = LayoutInflater.from(context).inflate(R.layout.view_chip, null) as Chip

            chip.apply {
                var myColor = 0

                when (name) {
                    "Front-end" -> {
                        myColor = ContextCompat.getColor(context, R.color.front_end)
                    }
                    "Back-end" -> {
                        myColor = ContextCompat.getColor(context, R.color.back_end)
                    }
                    "React" -> {
                        myColor = ContextCompat.getColor(context, R.color.react)
                    }
                    "Vue" -> {
                        myColor = ContextCompat.getColor(context, R.color.vue)
                    }
                    "Spring" -> {
                        myColor = ContextCompat.getColor(context, R.color.spring)
                    }
                    "Django" -> {
                        myColor = ContextCompat.getColor(context, R.color.django)
                    }
                    "iOS" -> {
                        myColor = ContextCompat.getColor(context, R.color.ios)
                    }
                    "Typescript" -> {
                        myColor = ContextCompat.getColor(context, R.color.typescript)
                    }
                    "Javascript" -> {
                        myColor = ContextCompat.getColor(context, R.color.javascript)
                    }
                    "Android" -> {
                        myColor = ContextCompat.getColor(context, R.color.android)
                    }
                    "Angular" -> {
                        myColor = ContextCompat.getColor(context, R.color.angular)
                    }
                    "HTML/CSS" -> {
                        myColor = ContextCompat.getColor(context, R.color.html_css)
                    }
                    "Flask" -> {
                        myColor = ContextCompat.getColor(context, R.color.flask)
                    }
                    "Node.js" -> {
                        myColor = ContextCompat.getColor(context, R.color.node)
                    }
                    "Java" -> {
                        myColor = ContextCompat.getColor(context, R.color.java)
                    }
                    "Python" -> {
                        myColor = ContextCompat.getColor(context, R.color.python)
                    }
                    "C#" -> {
                        myColor = ContextCompat.getColor(context, R.color.c_sharp)
                    }
                    "Kotlin" -> {
                        myColor = ContextCompat.getColor(context, R.color.kotlin)
                    }
                    "Swift" -> {
                        myColor = ContextCompat.getColor(context, R.color.swift)
                    }
                    "Go" -> {
                        myColor = ContextCompat.getColor(context, R.color.go)
                    }
                    "C/C++" -> {
                        myColor = ContextCompat.getColor(context, R.color.c_cpp)
                    }
                    "Design" -> {
                        myColor = ContextCompat.getColor(context, R.color.design)
                    }
                    "Figma" -> {
                        myColor = ContextCompat.getColor(context, R.color.figma)
                    }
                    "Sketch" -> {
                        myColor = ContextCompat.getColor(context, R.color.sketch)
                    }
                    "Git" -> {
                        myColor = ContextCompat.getColor(context, R.color.git)
                    }
                    "AdobeXD" -> {
                        myColor = ContextCompat.getColor(context, R.color.adobexd)
                    }
                    "Photoshop" -> {
                        myColor = ContextCompat.getColor(context, R.color.photoShop)
                    }
                    "Illustrator" -> {
                        myColor = ContextCompat.getColor(context, R.color.illustrator)
                    }
                    "Firebase" -> {
                        myColor = ContextCompat.getColor(context, R.color.firebase)
                    }
                    "AWS" -> {
                        myColor = ContextCompat.getColor(context, R.color.aws)
                    }
                    "GCP" -> {
                        myColor = ContextCompat.getColor(context, R.color.gcp)
                    }
                    "ect" -> {
                        myColor = ContextCompat.getColor(context, R.color.ect)
                    }

                }

                text = "  $name  "
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)

                val nonClickColor = ContextCompat.getColor(context, R.color.nonClick_tag)
                chipBackgroundColor = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_checked)
                    ),
                    intArrayOf(nonClickColor, myColor)
                )

                val nonCLickTextColor = ContextCompat.getColor(context, R.color.gray1)
                //텍스트
                setTextColor(
                    ColorStateList(
                        arrayOf(
                            intArrayOf(-android.R.attr.state_checked),
                            intArrayOf(android.R.attr.state_checked)
                        ),
                        intArrayOf(nonCLickTextColor, Color.BLACK)
                    )

                )
            }

            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
            )

            layoutParams.rightMargin = dpToPx(6)
            chips.add(chip)
            addView(chip, childCount - 1, layoutParams)
        }


    }

    private fun dpToPx(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        )
            .roundToInt()
}


