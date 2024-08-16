package com.zg.baselibrary.manager

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.zg.baselibrary.R
import com.zg.baselibrary.base.BaseActivity
import com.zg.baselibrary.utils.DateUtils
import com.zg.baselibrary.utils.LogUtils

/**
 * 用于管理全局留样共享数据的单例类
 */
class ClickManager {

    // 内部类单例
    object ClickManagerHolder {
        val instance: ClickManager = ClickManager()
    }

    var mOnLogOutListener: OnLogOutListener? = null
    private var mClickTime = -1L
    var isShowing = false

    var logTimeOutNum = -1

    fun updateClickTime() {
        mClickTime = System.currentTimeMillis()
        LogUtils.logI(TAG, "操作时间更新为:${DateUtils.getDateFormatSS(mClickTime)}")
    }


    fun setOnLogOutListener(listener: OnLogOutListener?) {
        mOnLogOutListener = listener
    }

    fun logout() {
        mOnLogOutListener?.logout()
    }

    fun down() {
        updateClickTime()
    }

    fun checkTimeOut(): Boolean {
        val timeOut = (System.currentTimeMillis() - mClickTime) / 1000
//        if (!isShowing) {
//            LogUtils.logI(
//                TAG, "登录未操作已经过去了：${timeOut}s" +
//                        " 限制：${TIME_OUT_LIMIT} " +
//                        " 限制触发 ${timeOut > TIME_OUT_LIMIT}" +
//                        " 弹窗显示 $isShowing"
//            )
//        }

        return timeOut > TIME_OUT_LIMIT
    }


    val runnable = object : Runnable {
        override fun run() {
            logTimeOutNum--
            tvRight?.text = "继续操作(${logTimeOutNum}S)"
            if (logTimeOutNum <= 0) {
                tvRight?.removeCallbacks(this)
                logout()
                dialog?.dismiss()
                instance.down()
                isShowing = false
            } else {
                tvRight?.postDelayed(this, 1000)
            }

        }

    }

    var tvRight: TextView? = null
    var dialog: AlertDialog? = null
    fun showLogTimeOutDialog(activity: BaseActivity) {
        if (!instance.checkTimeOut()) {
            LogUtils.logE(TAG, "超时已失效忽略")
            return
        }

        logTimeOutNum = TIME_OUT_SHOW_TIME
        val builder = AlertDialog.Builder(activity)
        val view: View = View.inflate(activity, R.layout.dialog_tip, null)
        builder.setView(view)
        dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvContent = view.findViewById<TextView>(R.id.tvContent)
        tvRight = view.findViewById(R.id.tvRight)
        tvTitle.text = ""
        tvContent.text = "系统检测到您已经${TIME_OUT_LIMIT / 60}分钟没有进行任何操作，是否退出登录？"
        tvRight?.run {
            text = "继续操作(${logTimeOutNum}S)"
            post(runnable)
            dialog?.setCanceledOnTouchOutside(false)
            setOnClickListener {
                dialog?.dismiss()
                instance.down()
                isShowing = false
            }

        }


        dialog?.show()
    }


    public interface OnLogOutListener {
        fun logout()
    }


    companion object {
        val instance: ClickManager
            get() = ClickManagerHolder.instance

        val TAG = "ClickManager"

        // 测试期间改为30S 方便测试
        val TIME_OUT_LIMIT_TEST = 30

        //        val TIME_OUT_LIMIT = 5 * 60
        var TIME_OUT_LIMIT = 5 * 60

        val TIME_OUT_SHOW_TIME = 15
    }


}
