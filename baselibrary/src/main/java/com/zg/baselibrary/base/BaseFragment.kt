package com.zg.baselibrary.base

import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.zg.baselibrary.utils.LogUtils

open class BaseFragment : Fragment() {

    fun String.toast() {
        activity?.runOnUiThread {
            Toast.makeText(activity, this, Toast.LENGTH_SHORT).show()
        }
    }

    fun String.logI() {
        LogUtils.logI(this@BaseFragment.javaClass.simpleName, this)
    }

    fun String.logD() {
        LogUtils.logD(this@BaseFragment.javaClass.simpleName, this)
    }

    fun String.logE() {
        LogUtils.logE(this@BaseFragment.javaClass.simpleName, this)
    }


    /**
     * 界面跳转封装
     */
    fun Class<*>.start() {
        activity?.let {
            startActivity(
                Intent(
                    it,
                    this
                )
            )
        }

    }
}
