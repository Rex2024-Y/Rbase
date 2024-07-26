package com.zg.quickbase.module.ui.function

import android.R.attr.label
import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import android.view.View
import com.zg.baselibrary.base.BaseActivity
import com.zg.baselibrary.utils.PermissionUtils
import com.zg.quickbase.databinding.ActivityExcelBinding
import com.zg.quickbase.module.ui.function.ext.ExcelBean
import jxl.Workbook
import jxl.format.Alignment
import jxl.format.Colour
import jxl.format.VerticalAlignment
import jxl.write.Label
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WriteException
import java.io.File
import java.io.IOException
import java.util.Date


/**
 * 展示两个网络请求demo
 */
class ExcelActivity : BaseActivity() {

    private lateinit var binding: ActivityExcelBinding


    override fun getRoot(): View {

        binding = ActivityExcelBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.btInstall.setOnClickListener {
            val verifyAllPermissions = PermissionUtils.verifyAllPermissions(this)
            if (verifyAllPermissions) {
                testExcel()
            }
        }

    }

    private fun testExcel() {


        val path = Environment.getExternalStorageDirectory().absolutePath
        Log.d("ExcelActivity", "path:$path")
        val directory =
            "$path/excel"
        val fileName = "/0726配餐simple.xlsx"
        val abFilePath = "$directory$fileName"
        Log.d("ExcelActivity", "directory:$directory")
        Log.d("ExcelActivity", "abFilePath:$abFilePath")


        val fileDirectory = File(directory)
        val file = File(abFilePath)


        try {
            if (!fileDirectory.exists()) {
                fileDirectory.mkdirs()
                Log.e("ExcelActivity", "路径不存在创建路径")
            }
            file.createNewFile();
        } catch (e: IOException) {
            Log.e("ExcelActivity", "createNewFile e:$e")
            e.printStackTrace()
            "创建文件失败$e".toast()
            return
        }
        Log.d("ExcelActivity", "文件创建成功:${file.absolutePath} ${file.exists()}")
        if (!file.exists()) {
            "文件不存在".toast()
            return
        }

        exportExcel(abFilePath)
    }

    fun exportExcel(filePath: String) {
        try {

            val dataList = arrayListOf<ExcelBean>()
            val titleList =
                arrayListOf("ID", "名称", "日期", "时间戳", "标题5", "标题6", "标题7", "标题8")
            for (i in 0..60) {
                dataList.add(
                    ExcelBean(
                        "$i",
                        "名字$i",
                        System.currentTimeMillis().toString(),
                        "${Date()}"
                    )
                )
            }
            val workbook = Workbook.createWorkbook(File(filePath))
            // 设置sheet名称
            val sheet = workbook.createSheet("0726配餐", 0)


            // 合并单元格
            sheet.mergeCells(0, 0, titleList.size - 1, 0)
            sheet.mergeCells(0, 1, titleList.size - 1, 1)

            // 调整列宽
            sheet.setRowView(0, 800)
            sheet.setRowView(1, 600)
            sheet.setRowView(2, 500)

            //创建字体，参数1：字体样式，参数2：字号，参数3：粗体
            val font = WritableFont(WritableFont.createFont("宋体"), 18, WritableFont.BOLD);
            val wcTop = WritableCellFormat(font)
            wcTop.run {
                // 垂直居中
                alignment = Alignment.CENTRE
                verticalAlignment = VerticalAlignment.CENTRE
                wrap = true
            }
            // 顶部标题
            sheet.addCell(Label(0, 0, "陪餐记录导出表", wcTop))

            // 次级标题
            val wcTop2 = WritableCellFormat(
                WritableFont(
                    WritableFont.createFont("宋体"),
                    12
                )
            )
            wcTop2.run {
                alignment = Alignment.CENTRE
                verticalAlignment = VerticalAlignment.CENTRE
                wrap = true
            }
            //X月X日-Y月Y日
            sheet.addCell(Label(0, 1, "(0801 - 0815)", wcTop2))


            // 内容标题栏
            val startRow = 3

            titleList.forEachIndexed { index, title ->
                run {

                    val wc = WritableCellFormat(
                        WritableFont(
                            WritableFont.createFont("宋体"),
                            12,
                            WritableFont.BOLD
                        )
                    )
                    wc.run {
                        verticalAlignment = VerticalAlignment.CENTRE
                        wrap = true
                    }
                    sheet.addCell(Label(index, startRow - 1, title, wc))
                }
            }

            // 内容
            dataList.forEachIndexed { index, excelBean ->
                run {
                    val wc = WritableCellFormat()
                    wc.wrap = true

                    val valueList = arrayListOf(
                        excelBean.id,
                        excelBean.name,
                        excelBean.time,
                        excelBean.date,
                        "A",
                        "B",
                        "C",
                        "D"
                    )
                    valueList.forEachIndexed { indexC, value ->
                        sheet.addCell(Label(indexC, index + startRow, value, wc))
                    }

                }
            }
            workbook.write()
            workbook.close()
            "导出成功:$filePath".toast()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: WriteException) {
            e.printStackTrace()
        }
    }


    override fun initViewModel() {

    }

}