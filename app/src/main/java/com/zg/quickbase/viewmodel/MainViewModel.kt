package com.zg.quickbase.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val title = "快速开发框架(rex自用)"
    val data = MutableLiveData<String>()

    var listData = arrayListOf(
        MainRvBean("网页相关", 1),
        MainRvBean("人脸识别", 2),
        MainRvBean("AI图片识别", 3),
        MainRvBean("网络框架", 4),
        MainRvBean("界面框架", 5),
        MainRvBean("硬件通信", 6),
        MainRvBean("插件相关", 7),
        MainRvBean("主副屏", 8),


    )

    class MainRvBean// name id的构造方法
        (var name: String, var text: String, var id: Int) {
        constructor(text: String, id: Int) : this("", text, id)
    }
}
