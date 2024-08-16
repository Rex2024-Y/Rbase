package com.zg.baselibrary.executor

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

class DThreadExecutor {


    private val mainHandler = Handler(Looper.getMainLooper())

    // IO线程池
    val ioExecutor =
        Executors.newFixedThreadPool(3)

    // 单线程
    val singleExecutor = Executors.newSingleThreadExecutor();
    fun executeUi(command: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // 当前线程是UI线程，直接运行
            command.run()
        } else {
            // 切换到UI线程执行
            mainHandler.post(command)
        }
    }

    fun executeUi(command: Runnable, delay: Long) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // 当前线程是UI线程，直接运行
            mainHandler.postDelayed(command, delay)
        } else {
            // 切换到UI线程执行
            mainHandler.postDelayed(command, delay)
        }
    }

    fun executeIo(command: Runnable) {
        ioExecutor.execute(command)
    }


    fun executeOne(command: Runnable) {
        singleExecutor.execute(command)
    }

    companion object {

        private const val TAG = "UiThreadExecutor"
        fun getInstance() = Holder.mInstance

        object Holder {
            val mInstance: DThreadExecutor = DThreadExecutor()
        }
    }
}