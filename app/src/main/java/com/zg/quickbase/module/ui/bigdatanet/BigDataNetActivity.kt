package com.zg.quickbase.module.ui.bigdatanet

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zg.baselibrary.base.BaseActivity
import com.zg.quickbase.databinding.ActivityBigDataNetBinding


class BigDataNetActivity : BaseActivity() {


    private lateinit var binding: ActivityBigDataNetBinding
    private lateinit var mViewModel: AIViewModel

    @SuppressLint("SetTextI18n")
    private val activityLauncher = this.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        it?.run {
            handleResult(this)
        }
    }

    private fun handleResult(uri: Uri) {
        binding.tvInfo.text = "识别中..."
        binding.ivPreview.setImageURI(uri)
        mViewModel.parseImage(this, uri)
    }


    override fun getRoot(): View {
        binding = ActivityBigDataNetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {


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
        mViewModel = ViewModelProvider(this)[AIViewModel::class.java]
        mViewModel.data.observe(this@BigDataNetActivity, Observer {
            val parseData = it ?: return@Observer
            binding.tvInfo.text = "$parseData"
        })
    }


    private fun showDialog() {
//        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
//        builder.setTitle("Please select model")
//        builder.setIcon(R.drawable.ic_dialog_alert)
//        builder.setCancelable(true)
//        builder.setNegativeButton("cancel", null)
//
//        builder.setSingleChoiceItems(
//            PADDLE_MODEL, modelIndex
//        ) { dialog, which ->
//            modelIndex = which
//            loadModel(PADDLE_MODEL[modelIndex])
//            dialog.dismiss()
//        }
//        builder.show()
    }


}