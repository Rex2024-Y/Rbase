package com.zg.baselibrary.utils

import android.widget.Toast
import com.zg.baselibrary.ContextManager
import com.zg.baselibrary.executor.UiThreadExecutor
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
object ToastUtils {
    fun showToast(msg: String?) {
        UiThreadExecutor().execute {
            Toast.makeText(ContextManager.instance.context, msg, Toast.LENGTH_SHORT).show()

        }
    }

    fun showLongToast(msg: String?) {
        UiThreadExecutor().execute {
            Toast.makeText(ContextManager.instance.context, msg, Toast.LENGTH_LONG).show()
        }
    }
}
