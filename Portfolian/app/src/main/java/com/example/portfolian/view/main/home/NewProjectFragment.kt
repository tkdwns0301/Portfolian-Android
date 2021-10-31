package com.example.portfolian.view.main.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.portfolian.R
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import kotlin.math.roundToInt

class NewProjectFragment : Fragment(R.layout.fragment_newproject) {
    private lateinit var ll_StackChoice: LinearLayout
    private lateinit var StackView: FlexboxLayout
    private lateinit var dl_NewProject: DrawerLayout
    private lateinit var ll_Drawer: LinearLayout
    private lateinit var btn_AllNonClick: Button
    private lateinit var btn_Close: ImageButton
    private lateinit var chips: ArrayList<Chip>
    private lateinit var checkedChips: MutableList<Chip>
    private var myColor: Int = 0
    private lateinit var checkedStackView: FlexboxLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDrawer(view)
        initStackView(view)
        initStackChoice(view)
    }

    private fun initDrawer(view: View) {
        btn_AllNonClick = view.findViewById(R.id.btn_AllNonClick)

        btn_AllNonClick.setOnClickListener {
            for (chip in chips) {
                chip.apply {
                    isChecked = false
                }
            }
            checkedStackView.removeAllViews()
        }

        btn_Close = view.findViewById(R.id.img_btn_Close)
        btn_Close.setOnClickListener {
            Log.d("Close", "success")
            dl_NewProject.closeDrawers()
        }
    }

    private fun initStackChoice(view: View) {
        ll_StackChoice = view.findViewById(R.id.ll_StackChoice)

        ll_StackChoice.setOnClickListener {
            dl_NewProject = view.findViewById(R.id.dl_NewProject)
            ll_Drawer = view.findViewById(R.id.ll_Drawer)
            dl_NewProject.openDrawer(ll_Drawer)
        }
    }

    private fun initStackView(view: View) {
        StackView = view.findViewById(R.id.fl_Stack)
        checkedStackView = view.findViewById(R.id.fbl_CheckedStack)
        chips = ArrayList()
        val nameArray = arrayOf(
            "Front-end",
            "Back-end",
            "C#",
            "React",
            "Vue",
            "Spring",
            "Django",
            "Javascript",
            "Git",
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
            "Design",
            "Figma",
            "Sketch",
            "AdobeXD",
            "GCP",
            "Photoshop",
            "Illustrator",
            "Firebase",
            "AWS",
            "ect"
        )

        StackView.addItems(nameArray)


    }

    private fun FlexboxLayout.addItems(names: Array<String>) {
        checkedChips = mutableListOf()

        for (name in names) {
            val chip = LayoutInflater.from(context).inflate(R.layout.view_chip, null) as Chip

            chip.apply {
                stackColor(name)

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

                val nonClickTextColor = ContextCompat.getColor(context, R.color.gray1)
                //텍스트
                setTextColor(
                    ColorStateList(
                        arrayOf(
                            intArrayOf(-android.R.attr.state_checked),
                            intArrayOf(android.R.attr.state_checked)
                        ),
                        intArrayOf(nonClickTextColor, Color.BLACK)
                    )

                )

                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        if (checkedChips.size < 7) {
                            checkedChips.add(this)
                            checkedStackView.addItem(name)
                        } else {
                            this.isChecked = false
                        }
                    } else {
                        val chipIdx = checkedChips.indexOf(this)
                        Log.d("idx: ", "$chipIdx")
                        checkedStackView.removeViewAt(chipIdx)
                        checkedChips.removeAt(chipIdx)

                    }
                }
            }

            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
            )

            layoutParams.rightMargin = dpToPx(6)
            chips.add(chip)
            addView(chip, layoutParams)
        }


    }

    private fun FlexboxLayout.addItem(name: String) {
        val chip = LayoutInflater.from(context).inflate(R.layout.view_chip, null) as Chip
        chip.apply {
            stackColor(name)

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
            isChecked = true
            isClickable = false
        }
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.MarginLayoutParams.WRAP_CONTENT,
            ViewGroup.MarginLayoutParams.WRAP_CONTENT
        )

        layoutParams.rightMargin = dpToPx(6)
        addView(chip, layoutParams)
    }


    private fun stackColor(name: String) {
        when (name) {
            "Front-end" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.front_end) }!!
            }
            "Back-end" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.back_end) }!!
            }
            "React" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.react) }!!
            }
            "Vue" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.vue) }!!
            }
            "Spring" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.spring) }!!
            }
            "Django" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.django) }!!
            }
            "iOS" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.ios) }!!
            }
            "Typescript" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.typescript) }!!
            }
            "Javascript" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.javascript) }!!
            }
            "Android" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.android) }!!
            }
            "Angular" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.angular) }!!
            }
            "HTML/CSS" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.html_css) }!!
            }
            "Flask" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.flask) }!!
            }
            "Node.js" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.node) }!!
            }
            "Java" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.java) }!!
            }
            "Python" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.python) }!!
            }
            "C#" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.c_sharp) }!!
            }
            "Kotlin" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.kotlin) }!!
            }
            "Swift" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.swift) }!!
            }
            "Go" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.go) }!!
            }
            "C/C++" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.c_cpp) }!!
            }
            "Design" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.design) }!!
            }
            "Figma" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.figma) }!!
            }
            "Sketch" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.sketch) }!!
            }
            "Git" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.git) }!!
            }
            "AdobeXD" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.adobexd) }!!
            }
            "Photoshop" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.photoShop) }!!
            }
            "Illustrator" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.illustrator) }!!
            }
            "Firebase" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.firebase) }!!
            }
            "AWS" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.aws) }!!
            }
            "GCP" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.gcp) }!!
            }
            "ect" -> {
                myColor = context?.let { ContextCompat.getColor(it, R.color.ect) }!!
            }

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