package com.zg.quickbase

import tp.xmaihh.serialport.utils.ByteUtil

object TestKt {
    // main方法 可以run的方法 0103 0400 0000 00fa 33
    @JvmStatic
    fun main(args: Array<String>) {
        // request 01 03 07 D0 00 02 C4 86
        // 0.02
        val sendMsg = "01 03 07 D0 00 02 C4 86"
        val data = "0103 0400 0000 027b f2"
//        // 去掉字符串全部空格
        val string1 = data.replace(" ", "")
        println("item:$string1")
        val string2 = string1.substring(6, 14)
        println("item:$string2")
        // 16进制转10进制
        val num = string2.toInt(16)
        println("weight:${num*100.00f/10000}")
//        val num = sendMsg.replace(" ", "")
//        println("num:${num}")
//
//        println("Hex:${HexToByteArr(num)}")

    }

    fun HexToByteArr(inHex: String): ByteArray {
        var inHex = inHex
        var hexlen = inHex.length
        val result: ByteArray
        if (ByteUtil.isOdd(hexlen) == 1) {
            ++hexlen
            result = ByteArray(hexlen / 2)
            inHex = "0$inHex"
        } else {
            result = ByteArray(hexlen / 2)
        }
        var j = 0
        var i = 0
        while (i < hexlen) {
            result[j] = ByteUtil.HexToByte(inHex.substring(i, i + 2))
            ++j
            i += 2
        }
        return result
    }

}
