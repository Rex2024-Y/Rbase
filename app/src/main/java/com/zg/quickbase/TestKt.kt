package com.zg.quickbase

object TestKt {
    // main方法 可以run的方法 0103 0400 0000 00fa 33
    @JvmStatic
    fun main(args: Array<String>) {
        // request 01 03 07 D0 00 02 C4 86
        // 0.02
        val data = "0103 0400 0000 027b f2"
        // 去掉字符串全部空格
        val string1 = data.replace(" ", "")
        println("item:$string1")
        val string2 = string1.substring(6, 14)
        println("item:$string2")
        // 16进制转10进制
        val num = string2.toInt(16)
        println("weight:$num")
    }

}