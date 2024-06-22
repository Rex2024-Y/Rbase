package com.zg.quickbase.module.ui.bigdata

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.zg.baselibrary.base.BaseActivity
import com.zg.quickbase.databinding.ActivityBigDataBinding
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class BigDataActivity : BaseActivity() {


    private lateinit var binding: ActivityBigDataBinding
    private var loadResultSuccess = false
    private var mTfliteInterpreter: Interpreter? = null
    private var modelIndex = 0
    private val resultLabel = arrayListOf<String>()
    private val PADDLE_MODEL = arrayOf(
        "mobilenet_v3",
        "mobilenet_v1_1.0_224",
        "model"
    )
//    val output = Array(1) { FloatArray(36) }

    private val ddims = intArrayOf(1, 3, 224, 224)
    val labelProbArray = Array(1) { FloatArray(1001) }

    @SuppressLint("SetTextI18n")
    private val activityLauncher = this.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        it?.run {
            handleResult(this)
        }
    }


    override fun getRoot(): View {
        binding = ActivityBigDataBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {


        binding.btMode.setOnClickListener {
            showDialog()
        }

        binding.btChoose.setOnClickListener {
            // 检查文件写入权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                // 已经具有权限
                activityLauncher.launch("image/*");
            } else {
                // 无权限，需要请求权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 999)
                }
            }

        }
    }


    override fun initViewModel() {
        readCacheLabelFromLocalFile()
    }

    private fun handleResult(uri: Uri) {
        binding.ivPreview.setImageURI(uri)

        val bitmapFromUri = getBitmapFromUri(uri, this)
        binding.tvInfo.text = "[Uri]:  ${uri}\n\n bitmapFromUri:${bitmapFromUri} "
        predictImage(bitmapFromUri)

    }

    private fun getBitmapFromUri(
        uri: Uri,
        context: Context
    ): Bitmap {
        val contentResolver = context.contentResolver
        return BitmapFactory.decodeStream(
            contentResolver.openInputStream(
                uri
            )
        )
    }


    private fun predictImage(bitmap: Bitmap) {
        "predictImage".logD()

        val inputData = getScaledMatrix(bitmap, ddims)
        try {
            val start = System.currentTimeMillis()
            mTfliteInterpreter?.run(inputData, labelProbArray)
            val end = System.currentTimeMillis()
            val time = end - start
            val results = labelProbArray[0]
            val r = getMaxResult(results)
            "下标: $r".logD()
            "标签总数量: ${results.size}".logD()
            "probability: ${labelProbArray.size}".logD()

//            if (resultLabel.size <= r) {
//                binding.tvInfo.text = "out if index $r , ${resultLabel.size} ${results.size}"
//                return
//            }
            val showText = "result：$r\nname：${resultLabel[r]}\nprobability：" +
                    "${results[r]}\ntime：$time ms"
            binding.tvInfo.text = showText
        } catch (e: Exception) {
            "解析错误 $e".logE()
        }
    }

    /**
     * Memory-map the model file in Assets.
     */
    @Throws(IOException::class)
    private fun loadModelFile(model: String): MappedByteBuffer {
        val fileDescriptor = applicationContext.assets.openFd("$model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


    // load infer model
    private fun loadModel(model: String) {
        try {
            mTfliteInterpreter = Interpreter(loadModelFile(model))
            "$model model load success".toast()
            "$model model load success".logD()
            mTfliteInterpreter?.setNumThreads(4)
            loadResultSuccess = true
        } catch (e: IOException) {
            "$model model load fail".toast()
            "$model model load fail".logE()
            loadResultSuccess = false
            e.printStackTrace()
        }
    }

    private fun showDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Please select model")
        builder.setIcon(R.drawable.ic_dialog_alert)
        builder.setCancelable(true)
        builder.setNegativeButton("cancel", null)

        builder.setSingleChoiceItems(
            PADDLE_MODEL, modelIndex
        ) { dialog, which ->
            modelIndex = which
            loadModel(PADDLE_MODEL[modelIndex])
            dialog.dismiss()
        }
        builder.show()
    }


    private fun readCacheLabelFromLocalFile() {
        try {
            val assetManager = applicationContext.assets
            val reader = BufferedReader(InputStreamReader(assetManager.open("cacheLabel.txt")))
            var readLine: String? = null
            while (reader.readLine().also { readLine = it } != null) {
                readLine?.run {
                    resultLabel.add(this)
                }
            }
            reader.close()
            "resultLabel:$resultLabel".logD()
        } catch (e: Exception) {
            Log.e("labelCache", "error $e")
        }
    }


    //    // get max probability label
    private fun getMaxResult(result: FloatArray): Int {
        var probability = result[0]
        var r = 0
        for (i in result.indices) {
            if (probability < result[i]) {
                probability = result[i]
                r = i
            }
        }
        return r
    }


    /**
     * 将bitmap转化为ByteBuffer
     */
    private fun getScaledMatrix(bitmap: Bitmap, ddims: IntArray): ByteBuffer {
        val imgData =
            ByteBuffer.allocateDirect(ddims[0] * ddims[1] * ddims[2] * ddims[3] * 4).apply {
                order(ByteOrder.nativeOrder())
            }
        // get image pixel
        val pixels = IntArray(ddims[2] * ddims[3])
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, ddims[2], ddims[3], false)
        scaledBitmap.getPixels(
            pixels,
            0,
            scaledBitmap.width,
            0,
            0,
            scaledBitmap.width,
            scaledBitmap.height
        )

        var pixel = 0
        for (i in 0 until ddims[2]) {
            for (j in 0 until ddims[3]) {
                val value = pixels[pixel++]
                imgData.putFloat((((value shr 16) and 0xFFf) - 128f) / 128f)
                imgData.putFloat((((value shr 8) and 0xFFf) - 128f) / 128f)
                imgData.putFloat(((value and 0xFFf) - 128f) / 128f)
            }
        }

//        scaledBitmap.recycle()
        return imgData
    }


}