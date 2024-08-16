package com.zg.baselibrary.utils

import android.widget.Toast
import com.zg.baselibrary.manager.ContextManager
import com.zg.baselibrary.executor.DThreadExecutor
object ToastUtils {
    fun showToast(msg: String?) {
        DThreadExecutor.getInstance().executeUi {
            Toast.makeText(ContextManager.instance.context, msg, Toast.LENGTH_SHORT).show()

        }
    }

    fun showLongToast(msg: String?) {
        DThreadExecutor.getInstance().executeUi {
            Toast.makeText(ContextManager.instance.context, msg, Toast.LENGTH_LONG).show()
        }
    }
}
