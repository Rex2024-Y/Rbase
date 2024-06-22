package com.zg.baselibrary

import android.annotation.SuppressLint
import android.content.Context

/**
 * 静态内部类单例
 */
class ContextManager private constructor() {
    private var mContext: Context? = null

    private object ContextManagerHolder {
        @SuppressLint("StaticFieldLeak")
        val instance = ContextManager()
    }

    fun init(context: Context) {
        mContext = context
    }

    val context: Context
        get() {
            mContext?.run {
                return this
            }
            throw RuntimeException("ContextManager must be init first")
        }

    companion object {
        val instance: ContextManager
            get() = ContextManagerHolder.instance
    }
}
