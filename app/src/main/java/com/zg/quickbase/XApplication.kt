package com.zg.quickbase

import android.app.Application
import android.util.Log
import com.zg.baselibrary.manager.ContextManager
import java.util.Date

class XApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextManager.instance.init(this)
        Thread.setDefaultUncaughtExceptionHandler(MyUncaughtExceptionHandler())
    }

    interface OnErrListener {
        fun onErr(msg: String?)
    }

    private inner class MyUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, throwableOrigin: Throwable) {
            // 在这里处理异常，例如记录日志、弹出错误对话框等
            // 以下代码仅为示例，请根据实际需求编写
            // 打印异常信息
            throwableOrigin.printStackTrace()
            val crashInfos = StringBuffer()
            //添加分隔行
            crashInfos.append("\r\n")
            //添加crash时间
            crashInfos.append("Crash Time")
            crashInfos.append("=")
            crashInfos.append(Date(System.currentTimeMillis()))
            crashInfos.append("\r\n")

            errMsg =  "$throwableOrigin ${Log.getStackTraceString(throwableOrigin)}"
            Log.d("XApplication", "uncaughtException:$throwableOrigin")
            Log.e("XApplication", "errMsg:$errMsg")
            onErrListener?.onErr(errMsg)
            // 如果需要，可以重启应用
            // 注意：这里不应该放任何重启逻辑，因为这可能会造成无限循环重启
            // 如果确实需要重启，请添加重启的条件判断，例如重启次数限制等
            // 如果是重启应用，可以使用如下代码
            /*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Process.killProcessQuietly(Process.myPid());
                System.exit(1);
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            */
        }
    }

    companion object {
        var errMsg = ""
        var onErrListener: OnErrListener? = null
    }
}
