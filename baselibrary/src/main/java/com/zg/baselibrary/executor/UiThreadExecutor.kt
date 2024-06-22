package com.zg.baselibrary.executor

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

class UiThreadExecutor : Executor {
    private val mainHandler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // 当前线程是UI线程，直接运行
            command.run()
        } else {
            // 切换到UI线程执行
            mainHandler.post(command)
        }
    }

    companion object {
        private const val TAG = "UiThreadExecutor"
    }
}