package com.zg.baselibrary.utils

import java.util.Locale
import kotlin.math.abs

object TempUtils {


    val addCode = "02"
    val DATA_START = "${addCode}03"
    val CONTROLLER_START = "${addCode}06"


    fun getTemp1(): String {
//        01 03 00 00 00 01 84 0A
        // 功能地址前缀
        val start = "$addCode 03 00 00 00 01"
        LogUtils.log("start:$start")
        val crc = getCRC(start)
        LogUtils.log("crc:$crc")
        val instruct = "$start $crc"
        LogUtils.log("getTemp1:---------------> \ninstruct:$instruct")
        return instruct
    }

    fun getTemp2(): String {
//        01 03 00 01 00 01 D5 CA
        val start = "$addCode 03 00 01 00 01"
        LogUtils.log("start:$start")
        val crc = getCRC(start)
        LogUtils.log("crc:$crc")
        val instruct = "$start $crc"
        LogUtils.log("getTemp2:---------------> \ninstruct:$instruct")
        return instruct
    }

    /**
     * 业务举例 设置温度封装
     * @param 温度只支持整数（结合实际需求也可以支持小数）
     */
    fun setTemp(temp: String): String {
        // 功能地址前缀
        val header = "$addCode 06 00 14"
        val tempToHexString = tempToHexString(temp)
        val start = "$header$tempToHexString"
        LogUtils.log("start:$start")
        val crc = getCRC(start)
        LogUtils.log("crc:$crc")
        val instruct = "$start $crc"
        LogUtils.log("setTemp:$temp---------------> \ninstruct:$instruct")
        return instruct
    }


    /**
     * 整数直接转16进制负数 -1 按位取反 单位℃
     */
    private fun tempToHexString(num: String): String {
        LogUtils.log("")
        LogUtils.log("")
        LogUtils.log("--------------->$num")
        val toFloat = num.toFloat()
        // 将℃转化为0.1℃
        val toInt = (toFloat * 10).toInt()
        if (toInt >= 0) {
            // int 转16进制
            return toInt.toString(16).uppercase().padStart(4, '0')
        } else {
            val temp = abs(toInt) - 1
            LogUtils.log("temp-1:${temp}(0.1温度)")
            // 不足16位进进行0补全
            val tempBinary = temp.toString(2).padStart(16, '0')
            LogUtils.log("binary:${tempBinary}")
            // 按位取反
            val binaryStringInverted = tempBinary.map {
                if (it == '0') '1' else '0'
            }.joinToString("")
            LogUtils.log("binaryInverted:${binaryStringInverted}")

            return binaryStringInverted.toLong(2).toString(16).uppercase()
        }
    }

    /**
     * 16进制带正负转化为10进制
     */
    fun hexToTempString(hex: String): Float {
        LogUtils.log("")
        LogUtils.log("")
        LogUtils.log("--------------->$hex")
        // 判断16进制字符串是否为正负
        val isNegative = hex.startsWith("F")
        // 如果是负数则转为二进制再按位取反再然后得到10进制数值并加上-号
        return if (isNegative) {
            val num = hexToIntTemp(hex)
            (-num * 100.00f / 1000)
        } else {
            val num = hex.toInt(16)
            (num * 100.00f / 1000)
        }
    }

    /**
     * 按位取反+1 获取负数问题
     */
    fun hexToIntTemp(hex: String): Int {
        // 将 16 进制转换为二进制
        val binaryString = hex.toInt(16).toString(2)
        LogUtils.log("binary:$binaryString")
        // 按位取反
        val binaryStringInverted = binaryString.map {
            if (it == '0') '1' else '0'
        }.joinToString("")
        LogUtils.log("binaryInverted:$binaryStringInverted")
        // 两个二进制字符串相加
        val sum = binaryStringInverted.toInt(2) + 1
        LogUtils.log("+1:$sum")
        return sum
    }


    /**
     * MODBUS协议 CRC16校验码
     */
    fun getCRC(data: String): String {
        var data = data
        data = data.replace(" ", "")
        val len = data.length
        if (len % 2 != 0) {
            return "0000"
        }
        val num = len / 2
        val para = ByteArray(num)
        for (i in 0 until num) {
            val value = data.substring(i * 2, 2 * (i + 1)).toInt(16)
            para[i] = value.toByte()
        }
        return getCRC(para)
    }

    /**
     * 计算CRC16校验码
     *
     * @param bytes
     * 字节数组
     * @return [String] 校验码
     * @since 1.0
     */
    fun getCRC(bytes: ByteArray): String {
        // CRC寄存器全为1
        var CRC = 0x0000ffff
        // 多项式校验值
        val POLYNOMIAL = 0x0000a001
        var i: Int
        var j: Int
        i = 0
        while (i < bytes.size) {
            CRC = CRC xor (bytes[i].toInt() and 0x000000ff)
            j = 0
            while (j < 8) {
                if (CRC and 0x00000001 != 0) {
                    CRC = CRC shr 1
                    CRC = CRC xor POLYNOMIAL
                } else {
                    CRC = CRC shr 1
                }
                j++
            }
            i++
        }
        // 结果转换为16进制
        var result = Integer.toHexString(CRC).uppercase(Locale.getDefault())
        if (result.length != 4) {
            val sb = StringBuffer("0000")
            result = sb.replace(4 - result.length, 4, result).toString()
        }
        //高位在前地位在后
        //return result.substring(2, 4) + " " + result.substring(0, 2);
        // 交换高低位，低位在前高位在后
        return result.substring(2, 4) + " " + result.substring(0, 2)
    }

}