package com.zg.quickbase.base

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.zg.quickbase.utils.LogUtils

open class BaseViewModel : ViewModel(){

    fun String.logD(){
        LogUtils.logD(this@BaseViewModel.javaClass.simpleName,this)
    }

    fun String.logI(){
        LogUtils.logI(this@BaseViewModel.javaClass.simpleName,this)
    }


    fun String.logE(){
        LogUtils.logE(this@BaseViewModel.javaClass.simpleName,this)
    }

}
