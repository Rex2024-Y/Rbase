package com.zg.baselibrary.base

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.zg.baselibrary.manager.ContextManager
import com.zg.baselibrary.executor.DThreadExecutor
import com.zg.baselibrary.utils.LogUtils

open class BaseViewModel : ViewModel() {


    fun String.logD() {
        LogUtils.logD(this@BaseViewModel.javaClass.simpleName, this)
    }

    fun String.logI() {
        LogUtils.logI(this@BaseViewModel.javaClass.simpleName, this)
    }


    fun String.logE() {
        LogUtils.logE(this@BaseViewModel.javaClass.simpleName, this)
    }

    fun String.toast() {
        DThreadExecutor.getInstance().executeUi {
            Toast.makeText(ContextManager.instance.context, this, Toast.LENGTH_SHORT).show()
        }
    }

    fun String.toastWithLog() {
        logI()
        DThreadExecutor.getInstance().executeUi {
            Toast.makeText(ContextManager.instance.context, this, Toast.LENGTH_SHORT).show()
        }
    }

}
