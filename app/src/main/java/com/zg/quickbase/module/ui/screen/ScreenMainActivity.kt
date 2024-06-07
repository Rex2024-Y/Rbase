package com.zg.quickbase.module.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Surface
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.impl.ImageOutputConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityScreenMainBinding


class ScreenMainActivity : BaseActivity() {

    private lateinit var binding: ActivityScreenMainBinding

    var mScreen1: Screen1? = null
    var mScreen2: Screen2? = null
    var mScreen3: Screen3? = null

    override fun getRoot(): View? {
        binding = ActivityScreenMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        val mDisplayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val mDisplays = mDisplayManager.displays
        binding.btCheckScreenSize.setOnClickListener {
            "当前屏幕数量为：${mDisplays.size}".toast()
        }

        binding.btShowScreen1.setOnClickListener {
            if (mDisplays.size > 1) {
                //displays[0] 主屏，displays[1] 副屏
                hideAll()

                mScreen1 = Screen1(this, mDisplays[1]);
                mScreen1?.show();
            } else {
                "当前屏幕数量为1 不支持".toast()
            }
        }

        binding.btShowScreen2.setOnClickListener {
            if (mDisplays.size > 1) {
                hideAll()
                //displays[0] 主屏，displays[1] 副屏
                mScreen2 = Screen2(this, mDisplays[1]);
                mScreen2?.show();
            } else {
                "当前屏幕数量为1 不支持".toast()
            }
        }

        binding.btShowScreen3.setOnClickListener {
            if (mDisplays.size > 1) {
                hideAll()


                // 请求相机权限
                if (allPermissionsGranted()) {
                    //displays[0] 主屏，displays[1] 副屏
                    mScreen3 = Screen3(this, mDisplays[1])
                    mScreen3?.show();
                } else {
                    // 申请权限
                    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                        if (isGranted) {
                            //displays[0] 主屏，displays[1] 副屏
                            mScreen3 = Screen3(this, mDisplays[1])
                            mScreen3?.show();
                        } else {
                            Toast.makeText(this, "Permission request denied", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }.launch(Manifest.permission.CAMERA)
                }
            } else {
                "当前屏幕数量为1 不支持".toast()
            }
        }
    }

    private fun hideAll() {

        mScreen1?.hide()
        mScreen2?.hide()
        mScreen3?.hide()
    }

    // 检测是否拥有所需权限
    private fun allPermissionsGranted() = requiredPermissions.all {
        PermissionChecker.checkSelfPermission(
            this,
            it
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    override fun initViewModel() {
    }

    companion object {
        // 请求权限相关
        private val requiredPermissions = arrayOf(Manifest.permission.CAMERA)

        // 相机相关
        private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA // 默认使用后置摄像头
        private var previewRotationTag = 1

        private var previewRotation = Surface.ROTATION_0
        private var previewJxFzTag = 0 // 是否镜像翻转, 0:否、1:是
        private var photoRotationTag = 0 // 0 默认
        private var photoRotation: Int? = null
        var viewFinder: PreviewView? = null

        fun dispatchCamera(
            previewView: PreviewView,
            activity: ScreenMainActivity
        ) {
            viewFinder = previewView
            startCamera(activity)
        }





        // 调起相机
        private fun startCamera(activity: ScreenMainActivity) {
            viewFinder?.implementationMode =
                PreviewView.ImplementationMode.COMPATIBLE // 指定模式，以便支持旋转
            viewFinder?.scaleType = PreviewView.ScaleType.FILL_START
            // start
            startCamera(cameraSelector, previewRotation, activity)
        }

        @SuppressLint("RestrictedApi")
        private fun startCamera(
            selector: CameraSelector,
            @ImageOutputConfig.RotationValue rotation: Int, activity: ScreenMainActivity
        ) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

            cameraProviderFuture.addListener({
                // 当ProcessCameraProvider准备就绪时
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // 预览用例
                val preview = Preview.Builder()
                    .setTargetRotation(rotation)
                    .build()
                    .also {
                        it.setSurfaceProvider(viewFinder?.surfaceProvider)
                    }



                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        activity,
                        selector,
                        preview
                    )

                } catch (exc: Exception) {
                    exc.printStackTrace()
                    Toast.makeText(activity, "Camera initialization error", Toast.LENGTH_SHORT)
                        .show()
                }
            }, ContextCompat.getMainExecutor(activity))
        }




    }
}