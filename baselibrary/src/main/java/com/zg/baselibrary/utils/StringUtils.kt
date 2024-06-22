package com.zg.baselibrary.utils


object StringUtils {

    /**
     * 保留两位有效数字
     */
    fun money2f(value: Float): String {
        return "${(value * 100).toInt() / 100.00f}"
    }


}
