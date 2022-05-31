package com.example.portfolian.view

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.TextView
import br.tiagohm.markdownview.MarkdownView
import com.example.portfolian.R
import org.w3c.dom.Text


class MarkdownDialog(context: Context) {
    private var mContext = context

    private val dialog = Dialog(context)
    private lateinit var header: TextView
    private lateinit var headerMD: MarkdownView
    private lateinit var blockQuote: TextView
    private lateinit var blockQuoteMD: MarkdownView
    private lateinit var list: TextView
    private lateinit var listMD: MarkdownView
    private lateinit var link: TextView
    private lateinit var linkMD: MarkdownView

    fun showDialog() {
        dialog.setContentView(R.layout.dialog_markdown)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        header = dialog.findViewById(R.id.tv_HeaderEx)
        headerMD = dialog.findViewById(R.id.md_HeaderEx)

        blockQuote = dialog.findViewById(R.id.tv_BlockQuoteEx)
        blockQuoteMD = dialog.findViewById(R.id.md_BlockQuoteEx)

        list = dialog.findViewById(R.id.tv_ListEx)
        listMD = dialog.findViewById(R.id.md_ListEx)

        link = dialog.findViewById(R.id.tv_LinkEx)
        linkMD = dialog.findViewById(R.id.md_LinkEx)

        headerMD.loadMarkdown("${header.text}")
        blockQuoteMD.loadMarkdown("${blockQuote.text}")
        listMD.loadMarkdown("${list.text}")
        linkMD.loadMarkdown("${link.text}")

    }

}
