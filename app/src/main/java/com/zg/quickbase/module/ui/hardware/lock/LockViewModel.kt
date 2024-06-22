package com.zg.quickbase.module.ui.hardware.lock

import android.app.Activity
import android.os.SystemClock
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.zg.baselibrary.base.BaseViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tp.xmaihh.serialport.SerialHelper
import tp.xmaihh.serialport.bean.ComBean
import tp.xmaihh.serialport.stick.AbsStickPackageHelper
import tp.xmaihh.serialport.utils.ByteUtil


class LockViewModel : BaseViewModel() {

    private var serialHelper: SerialHelper? = null
    val mReceivedMsg: MutableLiveData<String> = MutableLiveData<String>()
    val mOpen: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    //十六进制转换为二进制
    fun hexToBinary(value: Int): Long {
        var HexNumber = value
        var decimalNumber = 0
        var count = 0
        var binaryNumber: Long = 0
        //十六进制转十进制
        while (HexNumber != 0) {
            decimalNumber += (HexNumber % 10 * Math.pow(16.0, count.toDouble())).toInt()
            ++count
            HexNumber /= 10
        }

        count = 1
        //十进制转二进制
        while (decimalNumber != 0) {
            binaryNumber += (decimalNumber % 2 * count).toLong()
            decimalNumber /= 2
            count *= 10
        }
        return binaryNumber
    }


    fun binaryPadToEightBits(binaryStr: String): String {
        var str = binaryStr
        // 如果二进制字符串长度小于8，前面填充0
        while (str.length < 8) {
            str = "0$str"
        }
        return str
    }

    fun initSerialConfig(hardwareActivity: LockActivity) {
        //初始化SerialHelper对象，设定串口名称和波特率（此处为接收扫码数据）/dev/ttyS1
        serialHelper = object : SerialHelper("/dev/ttyS1", 9600) {

            override fun onDataReceived(paramComBean: ComBean) {

                try {
                    val time = paramComBean.sRecTime;
                    var rxText = String(paramComBean.bRec);
                    "onDataReceived origin: ${Gson().toJson(paramComBean)}".logI()
                    rxText = ByteUtil.ByteArrToHex(paramComBean.bRec)
                    // 先显示
                    mReceivedMsg.postValue("result $time: $rxText\r\n")
                    // rxText 每两位截取字符串
                    val rxTextHex = rxText.replace(" ", "")
                    // 固定取第一块板子
                    val firstBoardHex = rxTextHex.substring(8, 10).toInt()
                    val firstBoardBinary = hexToBinary(firstBoardHex)
                    val firstBoardBinary8 = binaryPadToEightBits(firstBoardBinary.toString())
                    rxText += "\n 第一块板 hex:$firstBoardHex --> bin:$firstBoardBinary8"
                    rxText +="\n \n"
                    firstBoardBinary8.forEachIndexed { index, c ->
                        when (c) {
                            '1' -> {
                                rxText += "【 ${8-index}关 】, "
                            }
                            '0' -> {
                                rxText += "【 ${8-index}开 】, "
                            }
                        }
                    }
                    "onDataReceived Hex: $rxText".logI()
                    var rText = "result $time: $rxText\r\n"
                    mReceivedMsg.postValue(rText)
                } catch (e: Exception) {
                    hardwareActivity.runOnUiThread {
                        Toast.makeText(hardwareActivity, "onDataReceived异常：$e", Toast.LENGTH_SHORT)
                            .show()
                    }
                }


            }

        }




        /*
         * 默认的BaseStickPackageHelper将接收的数据扩展成64位，一般用不到这么多位
         * 我这里重新设定一个自适应数据位数的
         */
        serialHelper?.stickPackageHelper = AbsStickPackageHelper { inputStream ->
            try {
                val available = inputStream.available()
                if (available > 0) {
                    val buffer = ByteArray(available)
                    val size = inputStream.read(buffer)
                    if (size > 0) {
                        return@AbsStickPackageHelper buffer
                    }
                } else {
                    SystemClock.sleep(50)
                }
            } catch (e: Exception) {
                Toast.makeText(hardwareActivity, "AbsStickPackageHelper异常：$e", Toast.LENGTH_SHORT)
                    .show();
                "AbsStickPackageHelper: $e".logE()
            }
            null
        }
    }

    fun openCOM(activity: Activity) {
        serialHelper?.run {
            if (isOpen) {
                Toast.makeText(activity, "串口已经打开", Toast.LENGTH_SHORT).show()
                return
            }
            GlobalScope.launch {
                "opening".logI()
                try {
                    open()
                } catch (e: Exception) {
                    e.printStackTrace()
                    "openCOM: $e".logE()
                    activity.runOnUiThread {
                        Toast.makeText(activity, "串口打开异常:$e", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Toast.makeText(activity, "串口打开成功", Toast.LENGTH_SHORT).show()

        } ?: let {
            Toast.makeText(activity, "请先初始化串口", Toast.LENGTH_SHORT).show()
        }
    }

    fun closeCOM(activity: Activity) {

        serialHelper?.run {
            if (isOpen) {
                close()
                Toast.makeText(activity, "-串口已经关闭", Toast.LENGTH_SHORT).show()
            }
        } ?: let {
            Toast.makeText(activity, "请先初始化串口", Toast.LENGTH_SHORT).show()
        }


    }

    fun sendMsg(activity: LockActivity, text: String) {
        "sendMsg $text".logI()

        serialHelper?.run {
            if (isOpen) {
                try {
                    sendHex(text.replace(" ", ""))
                    Toast.makeText(activity, "已发送", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(activity, "sendMsg异常：$e", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "请先初始化串口", Toast.LENGTH_SHORT).show()
            }
        } ?: let {
            Toast.makeText(activity, "请先初始化串口", Toast.LENGTH_SHORT).show()
        }
//        serialHelper.send(byte[] bytes);  //byte数组
//        serialHelper.sendHex(String str); //十六进制数
//        serialHelper.sendTxt(String str); //文本
    }

}
