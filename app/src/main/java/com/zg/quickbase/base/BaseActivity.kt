package com.zg.quickbase.base

import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.zg.quickbase.utils.LogUtils

open abstract class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 开启沉浸式
        enableEdgeToEdge()
        getRoot()?.run {
            setContentView(getRoot())
            // 沉浸式间距
//            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//                insets
//            }
//            // 是否让出状态栏高度
            WindowCompat.setDecorFitsSystemWindows(window, true)
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
}
