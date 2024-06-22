package com.zg.baselibrary.base

import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.zg.baselibrary.R
import com.zg.baselibrary.utils.LogUtils


open abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 开启沉浸式
        enableEdgeToEdge()
        // 设置全屏模式
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        getRoot()?.run {
            setContentView(getRoot())
            // 是否让出状态栏高度
            WindowCompat.setDecorFitsSystemWindows(window, false)
            initViewModel()
            initView()
        }
    }

    abstract fun getRoot(): View?

    abstract fun initView()


    abstract fun initViewModel()

    fun String.toast() {
        runOnUiThread {
            Toast.makeText(this@BaseActivity, this, Toast.LENGTH_SHORT).show()
        }
    }

    fun String.logI() {
        LogUtils.logI(this@BaseActivity.javaClass.simpleName, this)
    }

    fun String.logD() {
        LogUtils.logD(this@BaseActivity.javaClass.simpleName, this)
    }

    fun String.logE() {
        LogUtils.logE(this@BaseActivity.javaClass.simpleName, this)
    }


    /**
     * 界面跳转封装
     */
    fun Class<*>.start() {
        startActivity(
            Intent(
                this@BaseActivity,
                this
            )
        )
    }

    /**
     * 界面跳转封装
     */
    fun Class<*>.start(url: String) {
        val intent = Intent(
            this@BaseActivity,
            this
        )
        intent.putExtra(KEY_URL, url)
        startActivity(
            intent
        )
    }

    fun showListDialog(
        title: String?,
        items: Array<String>,
        listener: OnClickListener
    ): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setItems(items, listener)
        val dialog = builder.create()
        dialog.show()
        return dialog
    }

    fun showCheckDialog(
        title: String?,
        content: String,
        listener: OnClickListener
    ): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(content)
        builder.setPositiveButton("确定", listener)
        builder.setNegativeButton("取消", null)
        val dialog = builder.create()
        dialog.show()
        return dialog
    }


    fun showInputDialog(
        text: String?,
        hint: String?,
        leftText: String,
        rightText: String,
        leftOnClickListener: DialogClickListener? = null,
        rightOnClickListener: DialogClickListener? = null,
    ): AlertDialog {
        return showInputDialog(
            text,
            hint,
            leftText,
            rightText,
            true,
            leftOnClickListener,
            rightOnClickListener
        )
    }


    fun showInputDialog(
        text: String?,
        hint: String?,
        leftText: String,
        rightText: String,
        dismiss: Boolean,
        leftOnClickListener: DialogClickListener? = null,
        rightOnClickListener: DialogClickListener? = null,
    ): AlertDialog {
        val builder = AlertDialog.Builder(this)
        val view: View = View.inflate(this, R.layout.dialog_input_text, null)
        builder.setView(view)
        val dialog = builder.create()
        val etContent = view.findViewById<EditText>(R.id.etContent)
        val tvLeft = view.findViewById<TextView>(R.id.tvLeft)
        val tvRight = view.findViewById<TextView>(R.id.tvRight)
        etContent.setText(text)
        etContent.setHint(hint)
        tvLeft.text = leftText
        tvRight.text = rightText
        dialog.setCanceledOnTouchOutside(true)
        tvLeft.setOnClickListener {
            leftOnClickListener?.onClick(dialog, "")
            dialog.dismiss()
        }

        tvRight.setOnClickListener {
            val text = etContent.text.trim().toString()
            rightOnClickListener?.onClick(dialog, text)
            if (dismiss) dialog.dismiss()
        }

        dialog.show()
        return dialog
    }


    fun showTipDialog(
        title: String?,
        content: String?,
        listener: View.OnClickListener? = null
    ): AlertDialog {
        val builder = AlertDialog.Builder(this)
        val view: View = View.inflate(this, R.layout.dialog_tip, null)
        builder.setView(view)
        val dialog = builder.create()
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvContent = view.findViewById<TextView>(R.id.tvContent)
        val tvRight = view.findViewById<TextView>(R.id.tvRight)


        tvTitle.text = title
        tvContent.text = content

        dialog.setCanceledOnTouchOutside(false)
        listener?.run {
            dialog.dismiss()
            tvRight.setOnClickListener(this)
        } ?: let {
            tvRight.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
        return dialog
    }

    fun showChooseDialog(
        title: String?,
        content: String?,
        leftText: String,
        rightText: String,
        leftOnClickListener: DialogClickListener? = null,
        rightOnClickListener: DialogClickListener? = null,
    ): AlertDialog {
        val builder = AlertDialog.Builder(this)
        val view: View = View.inflate(this, R.layout.dialog_choose, null)
        builder.setView(view)
        val dialog = builder.create()
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvContent = view.findViewById<TextView>(R.id.tvContent)
        val tvLeft = view.findViewById<TextView>(R.id.tvLeft)
        val tvRight = view.findViewById<TextView>(R.id.tvRight)


        tvTitle.text = title
        tvContent.text = content
        tvLeft.text = leftText
        tvRight.text = rightText

        dialog.setCanceledOnTouchOutside(true)
        tvLeft.setOnClickListener {
            dialog.dismiss()
            leftOnClickListener?.onClick(dialog, "")
        }
        tvRight.setOnClickListener {
            dialog.dismiss()
            rightOnClickListener?.onClick(dialog, "")
        }
        dialog.show()
        return dialog
    }


    fun showListDialog(
        items: Array<String>,
        listener: OnClickListener
    ): AlertDialog {
        val dialogB = AlertDialog.Builder(this@BaseActivity)
        dialogB.run {
            setTitle("请选择")
            setCancelable(true)
            setItems(items, listener)

        }
        dialogB.show()
        return dialogB.create()

    }


    public interface DialogClickListener {
        fun onClick(dialog: AlertDialog, text: String)
    }

    companion object {
        val KEY_URL = "url"
    }
}
