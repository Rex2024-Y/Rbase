package com.zg.baselibrary.net

import com.zg.baselibrary.utils.LogUtils
import com.zg.baselibrary.utils.ToastUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * 统一处理回调结果
 * @param <T>
 */
abstract class BaseCallBackNoHttpResult<T> : Callback<T> {


    abstract fun success(response: T)

    open fun fail(msg: String) {
        handleError(msg)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        // 统一处理
        if (response.isSuccessful) {
            val data = response.body()
            if (response.isSuccessful) {
                if (data != null) {
                    success(data)
                }
            } else {
                fail("")
            }
        } else {
            fail(response.message())
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        LogUtils.logE(TAG, "onFailure:${t}")
        if (t.toString().contains(ERR_JSON_TAG)) {
            LogUtils.logE(TAG, "onFailure:${t}")
//            success(res)
            ToastUtils.showToast("没有更多数据")
            return
        }
        handleError("$t")
    }

    private fun handleError(errMsg: String) {
        ToastUtils.showToast("$errMsg")
    }

    companion object {
        const val SUCCESS_CODE = 200
        const val ERR_JSON_TAG = "JsonSyntaxException"
        const val TAG = "BaseCallBack"
    }

}
