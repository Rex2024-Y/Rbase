package com.zg.quickbase.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val title = "快速开发框架(rex自用)"
    val data = MutableLiveData<String>()

    var listData = arrayListOf(
        MainRvBean("AI图片识别", 1),
        MainRvBean("功能框架", 2),
        MainRvBean("网络框架", 3),
        MainRvBean("界面框架", 4),
        MainRvBean("硬件通信", 5),

    )

    class MainRvBean// name id的构造方法
        (var name: String, var text: String, var id: Int) {
        constructor(text: String, id: Int) : this("", text, id)
    }
}
