package com.zg.quickbase.module.ui.http

import androidx.lifecycle.MutableLiveData
import com.zg.baselibrary.base.BaseViewModel
import com.zg.quickbase.bean.HttpResult
import com.zg.quickbase.net.ApiClient
import com.zg.quickbase.request.LoginParam
import retrofit2.Call
import retrofit2.Response

class HttpViewModel : BaseViewModel() {

    class HttpViewData// 构造方法 response request初始化
        (request: String, response: String) {
        var request: String? = request
        var response: String? = response

    }


    val mViewLiveData: MutableLiveData<HttpViewData> = MutableLiveData<HttpViewData>()

    class MainRvBean// name id的构造方法
        (var name: String, var id: Int) {
    }


    fun loginTest() {
        val loginParam = LoginParam()
        loginParam.account = "test1"
        loginParam.password = "123456"


        mViewLiveData.postValue(HttpViewData("Post request:${loginParam}", ""))

        ApiClient.mainService().login(loginParam).enqueue(object : retrofit2.Callback<HttpResult> {
            override fun onResponse(
                call: Call<HttpResult>,
                response: Response<HttpResult>
            ) {
                val body = response.body()
                "Post response onResponse:${body}".logD()
                mViewLiveData.postValue(
                    HttpViewData(
                        "Post request:${loginParam}",
                        "Post response onResponse:${body}"
                    )
                )

            }

            override fun onFailure(call: Call<HttpResult>, t: Throwable) {
                "Post response onFailure:${t}".logD()
                mViewLiveData.postValue(
                    HttpViewData(
                        "Post request:${loginParam}",
                        "Post response onFailure:${t}"
                    )
                )
            }
        })
    }

    fun queryTest() {
        mViewLiveData.postValue(HttpViewData("Get request:", ""))
        ApiClient.mainService().getQuery(1).enqueue(object : retrofit2.Callback<HttpResult> {
            override fun onResponse(
                call: Call<HttpResult>,
                response: Response<HttpResult>
            ) {
                val body = response.body()
                "Post response onResponse:${body}".logD()
                mViewLiveData.postValue(
                    HttpViewData(
                        "Get request:",
                        "Get response onResponse:${body}"
                    )
                )

            }

            override fun onFailure(call: Call<HttpResult>, t: Throwable) {
                "Post response onFailure:${t}".logD()
                mViewLiveData.postValue(
                    HttpViewData(
                        "Get request:",
                        "Get response onFailure:${t}"
                    )
                )
            }
        })

    }
}
