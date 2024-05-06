package com.zg.quickbase.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val title = "快速开发框架(rex自用)"
    val data = MutableLiveData<String>()

    var listData = arrayListOf(
        MainRvBean("BigDataNetActivity", "在线识别", 1),
        MainRvBean("BigDataActivity", "离线识别", 2),
        MainRvBean("LoginActivity", "登录", 3),
        MainRvBean("TabActivity", "顶部导航", 4),
        MainRvBean("BottomNavActivity", "底部导航", 5),
        MainRvBean("VerticalTabActivity", "左边导航", 6),
        MainRvBean("HttpActivity", "网络", 7),

        )

    class MainRvBean// name id的构造方法
        (var name: String, var text: String, var id: Int) {
    }
}
