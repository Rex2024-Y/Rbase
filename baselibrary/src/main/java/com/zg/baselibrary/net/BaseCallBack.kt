package com.zg.baselibrary.net

import com.zg.baselibrary.utils.ToastUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * 统一处理回调结果
 * @param <T>
 */
abstract class BaseCallBack<T> : Callback<HttpResult<T>> {

    private val SUCCESS_CODE = 200
    abstract fun success(response: HttpResult<T>)

    open fun fail(response: Response<HttpResult<T>>) {
        handleError("${response.code()}  ${response.message()}")
    }

    override fun onResponse(call: Call<HttpResult<T>>, response: Response<HttpResult<T>>) {
        // 统一处理
        if (response.isSuccessful) {
            val data = response.body()
            if (data?.code == SUCCESS_CODE) {
                success(data)
            } else {
                handleError("${data?.code} ${data?.msg}")
            }
        } else {
            fail(response)
        }
    }

    override fun onFailure(call: Call<HttpResult<T>>, t: Throwable) {
        handleError("$t")
    }

    private fun handleError(errMsg: String) {
        ToastUtils.showToast("网络异常:$errMsg")
    }
}
