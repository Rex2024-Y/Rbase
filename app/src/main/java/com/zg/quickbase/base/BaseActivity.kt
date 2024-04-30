package com.zg.quickbase.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.zg.quickbase.R

open abstract class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 开启沉浸式
        enableEdgeToEdge()
        getRoot()?.run {
            setContentView(getRoot())
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            // 是否让出状态栏高度
            WindowCompat.setDecorFitsSystemWindows(window,true)
            initViewModel()
            initView()
        }
    }

    abstract fun getRoot(): View?

    abstract fun initView()


    abstract fun initViewModel()

    fun String.toast() {
        Toast.makeText(this@BaseActivity, this, Toast.LENGTH_SHORT).show()
    }
}
