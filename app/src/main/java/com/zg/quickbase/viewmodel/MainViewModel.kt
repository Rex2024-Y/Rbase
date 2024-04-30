package com.zg.quickbase.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel(){

    val title = "快速开发安卓框架(rex)"
    val data = MutableLiveData<String>()

    var listData = arrayListOf(
        MainRvBean("TAB1", 1),
        MainRvBean("TAB2", 2),
        MainRvBean("TAB3", 2),
    )
    class MainRvBean// name id的构造方法
        (var name: String, var id: Int) {
    }
}
