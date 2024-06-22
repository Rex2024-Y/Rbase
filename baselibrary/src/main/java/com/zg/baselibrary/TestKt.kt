package com.zg.baselibrary

import java.util.Locale
import kotlin.math.abs


object TestKt {
    // main方法 可以run的方法 0103 0400 0000 00fa 33
    @JvmStatic
    fun main(args: Array<String>) {
        // request 01 03 07 D0 00 02 C4 86
        // 0.02
        val sendMsg = "01 03 00 00 00 01"
        val sendMsg2 = "01 03 00 01 00 01"
        val sendMsg3 = "01 06 00 14 01 40"

        println("getCRC:${getCRC(sendMsg)}")
        println("getCRC2:${getCRC(sendMsg2)}")
        println("getCRC3:${getCRC(sendMsg3)}")

        println("温度转化")

//        -25.2=0XFF04 25.2=0X00FC -100.0=0XFC18 500.0=0X1388

        println("hexToTemp FF04:${hexToTempString("FF04")}℃")
        println("hexToTemp 00FC:${hexToTempString("00FC")}℃")
        println("hexToTemp FC18:${hexToTempString("FC18")}℃")
        println("hexToTemp 1388:${hexToTempString("1388")}℃")
        println("tempToTemp 1℃:${tempToHexString("1")}")
        println("tempToTemp 2℃:${tempToHexString("2")}")
        println("tempToTemp -2℃:${tempToHexString("-2")}")
        println("tempToTemp2 -2℃:${tempToHexString2("-2")}")
        println("验证-2℃ 逆转 ffec:${hexToTempString("ffec")}℃")
        println("CRC 01 06 00 14 00 0A:${getCRC("01 06 00 14 00 0A")}")

        println("startsWith 0103:${"01030200FA3807".startsWith("0103")}")


    }

    /**
     * 整数直接转16进制负数 -1 按位取反
     */
    private fun tempToHexString(num: String): String {
        println("")
        println("")
        println("--------------->$num")
        val toFloat = num.toFloat()
        // 将℃转化为0.1℃
        val toInt = (toFloat * 10).toInt()
        if (toInt >= 0) {
            // int 转16进制
            return toInt.toString(16).uppercase().padStart(4, '0')
        } else {
            val temp = abs(toInt) - 1
            println("temp-1:${temp}(0.1温度)")
            // 不足16位进进行0补全
            val tempBinary = temp.toString(2).padStart(16, '0')
            println("binary:${tempBinary}")
            // 按位取反
            val binaryStringInverted = tempBinary.map {
                if (it == '0') '1' else '0'
            }.joinToString("")
            println("binaryInverted:${binaryStringInverted}")
            println("方法2:${tempToHex(toInt)}")

            return binaryStringInverted.toLong(2).toString(16).uppercase()
        }
    }

    /**
     * 16进制带正负转化为10进制
     */
    private fun hexToTempString(hex: String): Float {
        println("")
        println("")
        println("--------------->$hex")
        // 判断16进制字符串是否为正负
        val isNegative = hex.uppercase().startsWith("F")
        // 如果是负数则转为二进制再按位取反再然后得到10进制数值并加上-号
        return if (isNegative) {
            val num = hexToIntTemp(hex)
            val num2 = hexToIntTemp2(hex)
            (-num * 100.00f / 1000)
        } else {
            val num = hex.toInt(16)
            (num * 100.00f / 1000)
        }
    }

    private fun hexToIntTemp2(hex: String): Int {
        // 将 16 进制转换为二进制
        val intNum = hex.toInt(16)
        val value = (intNum.inv() and 0xFFFF) + 1
        val sum = abs(value)
        println("hexToIntTemp2:$sum")
        return sum
    }


    /**
     * 整数直接转16进制负数 -1 按位取反
     */
    private fun tempToHexString2(num: String): String {
        println("")
        println("")
        println("--------------->$num")
        val toFloat = num.toFloat()
        // 将℃转化为0.1℃
        val toInt = (toFloat * 10).toInt()
        if (toInt >= 0) {
            // int 转16进制
            return toInt.toString(16).uppercase().padStart(4, '0')
        } else {
            val temp = abs(toInt) - 1
            println("tempToHex2 temp-1:${temp}(0.1温度)")
            // 不足16位进进行0补全
            val value = (temp.inv() and 0xFFFF)
            println("tempToHex2 value:${value}")
            return value.toString(16).uppercase()
        }
    }


    /**
     * @param 温度
     * 负数转16进制
     */
    private fun tempToHex(toInt: Int): String {
        val temp = abs(toInt) - 1
        println("tempToHex2 temp-1:${temp}(0.1温度)")
        // 不足16位进进行0补全
        val value = (temp.inv() and 0xFFFF)
        println("tempToHex2 value:${value}")
        return value.toString(16).uppercase()
    }



    /**
     * 按位取反+1 获取负数问题
     */
    private fun hexToIntTemp(hex: String): Int {
        // 将 16 进制转换为二进制
        val binaryString = hex.toInt(16).toString(2)
        println("binary:$binaryString")
        // 按位取反
        val binaryStringInverted = binaryString.map {
            if (it == '0') '1' else '0'
        }.joinToString("")
        println("binaryInverted:$binaryStringInverted")
        // 两个二进制字符串相加
        val sum = binaryStringInverted.toInt(2) + 1
        println("+1:$sum")
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


//    fun HexToByteArr(inHex: String): ByteArray {
//        var inHex = inHex
//        var hexlen = inHex.length
//        val result: ByteArray
//        if (ByteUtil.isOdd(hexlen) == 1) {
//            ++hexlen
//            result = ByteArray(hexlen / 2)
//            inHex = "0$inHex"
//        } else {
//            result = ByteArray(hexlen / 2)
//        }
//        var j = 0
//        var i = 0
//        while (i < hexlen) {
//            result[j] = ByteUtil.HexToByte(inHex.substring(i, i + 2))
//            ++j
//            i += 2
//        }
//        return result
//    }

}
