package com.zg.quickbase.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val title = "快速开发框架(rex自用)"
    val data = MutableLiveData<String>()

    var listData = arrayListOf(
        MainRvBean("BigDataActivity", 1),
        MainRvBean("LoginActivity", 2),
        MainRvBean("TabActivity", 3),
        MainRvBean("BottomNavActivity", 4),
        MainRvBean("VerticalTabActivity", 5),
        MainRvBean("HttpActivity", 6),

    )

    class MainRvBean// name id的构造方法
        (var name: String, var id: Int) {
    }
}
