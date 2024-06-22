package com.zg.baselibrary.base

import androidx.lifecycle.ViewModel
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

}
