package com.zg.baselibrary.utils

import java.util.Locale
import kotlin.math.abs

object HardWareUtils {


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

    fun getTempSet(): String {
//        01 03 00 01 00 01 D5 CA
        val start = "$addCode 03 00 14 00 01"
        LogUtils.log("start:$start")
        val crc = getCRC(start)
        LogUtils.log("crc:$crc")
        val instruct = "$start $crc"
        LogUtils.log("getTemp2:---------------> \ninstruct:$instruct")
        return instruct
    }


    fun getLockStatus(): String {
        return "80 01 00 33 B2"
    }

    fun openLockByIndex(index: Int): String {
        if (index < 0) {
            // 全开
            return "8A 01 00 11 9A"
        } else {
            // 单开 8个锁 不用考虑进位
            val start = "8A 01 0$index 11"
            return "$start ${getBCC(start)}"
        }

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

    fun getWeight(): String {
        return "03 03 07 D0 00 02 C5 64"
    }

    fun resetWeightZero():String{
        return "03 10 0B B8 00 02 04 00 00 00 0A 01 F2"
    }
     fun clearWeightPlate():String{
        return "03 10 0B BA 00 02 04 00 00 27 10 1A 10"
    }
    fun confirmWeightReset():String{
        return "03 10 0B B8 00 02 04 00 00 00 14 81 FA"
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
    fun hexToTempString(hex: String): Int {
        LogUtils.log("")
        LogUtils.log("")
        LogUtils.log("--------------->$hex")
        // 判断16进制字符串是否为正负
        val isNegative = hex.startsWith("F")
        // 如果是负数则转为二进制再按位取反再然后得到10进制数值并加上-号
        return if (isNegative) {
            val num = hexToIntTemp(hex)
            (-num * 100 / 1000)
        } else {
            val num = hex.toInt(16)
            (num * 100 / 1000)
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

    /**
     * 数据校验 异或处理
     */
    private fun getBCC(hex: String): String {

        val content = hex.replace(" ", "")
        var a = 0
        for (i in 0 until content.length / 2) {
            a = a xor content.substring(i * 2, i * 2 + 2).toInt(16)
        }
        val result = Integer.toHexString(a)
        return if (result.length == 1) {
            "0$result".uppercase(Locale.getDefault())
        } else {
            result.uppercase(Locale.getDefault())
        }
    }

    fun hexToBinary(hexString: String): String {
        // 将十六进制字符串转换为整数
        val intValue = hexString.toInt(16)
        // 将整数转换为8位二进制字符串
        return String.format("%8s", Integer.toBinaryString(intValue)).replace(' ', '0')
    }


    fun binaryPadToEightBits(binaryStr: String): String {
        var str = binaryStr
        // 如果二进制字符串长度小于8，前面填充0
        while (str.length < 8) {
            str = "0$str"
        }
        return str
    }


    /**
     * 去皮清零
     */


}