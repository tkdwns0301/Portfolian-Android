package com.example.portfolian.view.main.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.portfolian.R
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.internal.ViewUtils.dpToPx
import kotlin.math.roundToInt


class NewProjectActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var ll_StackChoice: LinearLayout
    private lateinit var dl_NewProject: DrawerLayout
    private lateinit var ll_Drawer: LinearLayout
    private lateinit var flexContainer: FlexboxLayout

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
    private lateinit var btnCheckedArray: MutableSet<ToggleButton>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newproject)

        init()
    }

    private fun init() {
        flexContainer = findViewById(R.id.fbl_StackContainer)

        initToolbar()
        initStackChoice()
        initDrawer()
        initToggleButton()

    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar_NewProject)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.toolbar_Save -> {
                    //TODO 저장 버튼을 눌렀을 때 게시물 업로드

                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initDrawer() {
        btn_AllNonClick = findViewById(R.id.btn_New_AllNonClick)
        btn_Close = findViewById(R.id.btn_New_Close)

        btn_AllNonClick.setOnClickListener {
            for (i in btnArray) {
                if (!i.isChecked) {
                    i.isChecked = false
                }
            }
        }

        btn_Close.setOnClickListener {
            addItemFlexbox()
            dl_NewProject.closeDrawers()
        }


    }

    private fun initStackChoice() {
        ll_StackChoice = findViewById(R.id.ll_StackChoice)

        ll_StackChoice.setOnClickListener {
            dl_NewProject = findViewById(R.id.dl_NewProject)
            ll_Drawer = findViewById(R.id.ll_New_Drawer)
            dl_NewProject.openDrawer(ll_Drawer)
        }
    }

    private fun initToggleButton() {
        //btn_Front = findViewById(R.id.btn_New_Front)
        btn_Back = findViewById(R.id.btn_New_Back)
        btn_React = findViewById(R.id.btn_New_React)
        btn_Vue = findViewById(R.id.btn_New_Vue)
        btn_Spring = findViewById(R.id.btn_New_Spring)
        btn_Django = findViewById(R.id.btn_New_Django)
        btn_iOS = findViewById(R.id.btn_New_iOS)
        btn_Typescript = findViewById(R.id.btn_New_Typescript)
        btn_Javascript = findViewById(R.id.btn_New_Javascript)
        btn_Android = findViewById(R.id.btn_New_Android)
        btn_Angular = findViewById(R.id.btn_New_Angular)
        btn_HTML = findViewById(R.id.btn_New_HTML_CSS)
        btn_Flask = findViewById(R.id.btn_New_Flask)
        btn_Node = findViewById(R.id.btn_New_Node_js)
        btn_Java = findViewById(R.id.btn_New_Java)
        btn_Python = findViewById(R.id.btn_New_Python)
        btn_Csharp = findViewById(R.id.btn_New_Csharp)
        btn_Kotlin = findViewById(R.id.btn_New_Kotlin)
        btn_Swift = findViewById(R.id.btn_New_Swift)
        btn_Go = findViewById(R.id.btn_New_Go)
        btn_C = findViewById(R.id.btn_New_C_Cpp)
        btn_Design = findViewById(R.id.btn_New_Design)
        btn_Figma = findViewById(R.id.btn_New_Figma)
        btn_Sketch = findViewById(R.id.btn_New_Sketch)
        btn_Git = findViewById(R.id.btn_New_Git)
        btn_Adobe = findViewById(R.id.btn_New_AdobeXD)
        btn_Photoshop = findViewById(R.id.btn_New_Photoshop)
        btn_Illustrator = findViewById(R.id.btn_New_illustrator)
        btn_Firebase = findViewById(R.id.btn_New_Firebase)
        btn_Aws = findViewById(R.id.btn_New_AWS)
        btn_Gcp = findViewById(R.id.btn_New_GCP)
        btn_Ect = findViewById(R.id.btn_New_ect)

        btnArray = arrayListOf(
            //btn_Front,
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

        btnCheckedArray = mutableSetOf()

        for (item in btnArray) {
            item.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    item.isChecked = btnCheckedArray.size <= 7
                    btnCheckedArray.add(item)

                } else {
                    btnCheckedArray.remove(item)
                }
            }
        }
    }

    private fun addItemFlexbox() {
        val textView = createFlexItemTextView(this, "HellWorldHellWorldHelloWorld")

        val layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
        layoutParams.rightMargin = dpToPx(4)


        textView.layoutParams = layoutParams
        flexContainer.addView(textView)
    }

    private fun createFlexItemTextView(context: Context, index: String): TextView {
        return TextView(context).apply {
            setBackgroundResource(R.drawable.shape_btn_front_click)
            text = index
            gravity = Gravity.CENTER
        }
    }
}

fun Context.dpToPx(dp: Int): Int
    = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).roundToInt()