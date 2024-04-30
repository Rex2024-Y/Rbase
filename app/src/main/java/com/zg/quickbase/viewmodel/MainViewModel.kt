package com.zg.quickbase.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val title = "快速开发安卓框架(rex)"
    val data = MutableLiveData<String>()

    var listData = arrayListOf(
        MainRvBean("LoginActivity", 1),
        MainRvBean("TabActivity", 2),
        MainRvBean("BottomNavActivity", 3),
        MainRvBean("VerticalTabActivity", 4),
        MainRvBean("HttpActivity", 5),

    )

    class MainRvBean// name id的构造方法
        (var name: String, var id: Int) {
    }
}
