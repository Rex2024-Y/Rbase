package com.zg.quickbase.module.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import android.hardware.display.DisplayManager
import android.util.Log
import android.view.Display
import android.view.Surface
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.impl.ImageOutputConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityScreenMainBinding
import com.zg.quickbase.module.ui.bigdata.camera.FaceDetectorHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class ScreenMainActivity : BaseActivity(), FaceDetectorHelper.DetectorListener {

    private lateinit var binding: ActivityScreenMainBinding
    private var faceDetectorHelper: FaceDetectorHelper? = null

    // 合格分数
    private val qualifiedScore = 0.9f

    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundExecutor: ExecutorService
    private var mImageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    var mScreen1: Screen1? = null
    var mScreen2: Screen2? = null
    var mScreen3: Screen3? = null
    private var _delegate: Int = FaceDetectorHelper.DELEGATE_CPU
    private var _threshold: Float =
        FaceDetectorHelper.THRESHOLD_DEFAULT

    val currentDelegate: Int get() = _delegate
    val currentThreshold: Float get() = _threshold
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
                openCamera(mDisplays)
            } else {
                "当前屏幕数量为1 不支持".toast()
            }
        }

        binding.btSwitchCamera.setOnClickListener {
            if (mScreen3 == null) {
                "屏幕3 未初始化".toast()
                return@setOnClickListener
            }

            if (type != 0) {
                "当前并不支持两个摄像头".toast()
                return@setOnClickListener
            }

            if (cameraSelector != CameraSelector.DEFAULT_BACK_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            }
            mScreen3?.switchCamera()
        }

    }

    private fun openCamera(displays: Array<Display>) {
        // 请求相机权限
        if (allPermissionsGranted()) {
            //displays[0] 主屏，displays[1] 副屏
            mScreen3 = Screen3(this, displays[1])
            mScreen3?.show();
        } else {
            // 申请权限
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    //displays[0] 主屏，displays[1] 副屏
                    mScreen3 = Screen3(this, displays[1])
                    mScreen3?.show();
                } else {
                    Toast.makeText(this, "Permission request denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }.launch(Manifest.permission.CAMERA)
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

        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(i, cameraInfo)
            Log.i("testrex", "cameraInfo:${cameraInfo.facing}")
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                // 后置摄像头
                isSupportBack = true
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // 前置摄像头
                isSupportFront = true
            }
        }

        if (isSupportBack && isSupportFront) {
            tip = "检测:Camera前后都支持"
            type = 0
        } else if (isSupportBack && !isSupportFront) {
            tip = "检测:仅支持Camera后置"
            type = 1
        } else if (!isSupportBack && isSupportFront) {
            tip = "检测:仅支持Camera前置支持"
            type = 2
        } else {
            tip = "检测:Camera前后都不支持"
            type = 3
        }

        binding.tvCheckResult.text = "$tip"

        if (type == 0 || type == 1) {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA // 默认使用后置摄像头

        } else if (type == 2) {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA // 默认使用后置摄像头
        }

        // Initialize our background executor
        backgroundExecutor = Executors.newSingleThreadExecutor()
    }

    // 请求权限相关
    private val requiredPermissions = arrayOf(Manifest.permission.CAMERA)
    var isSupportBack = false
    var isSupportFront = false
    var tip = ""
    var type = 0

    // 相机相关
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA // 默认使用后置摄像头
    private var previewRotationTag = 1

    private var previewRotation = Surface.ROTATION_0
    private var previewJxFzTag = 0 // 是否镜像翻转, 0:否、1:是
    private var photoRotationTag = 0 // 0 默认
    private var photoRotation: Int? = null
    var viewFinder: PreviewView? = null

    fun dispatchCamera() {
        viewFinder = mScreen3?.previewView
        startFaceDetector()
    }


    // 启动相机进行人脸检测
    private fun startFaceDetector() {
        // Create the FaceDetectionHelper that will handle the inference
        backgroundExecutor.execute {
            faceDetectorHelper =
                FaceDetectorHelper(
                    context = this,
                    threshold = currentThreshold,
                    currentDelegate = currentDelegate,
                    faceDetectorListener = this,
                    runningMode = RunningMode.LIVE_STREAM
                )

            // Wait for the views to be properly laid out
            mScreen3?.previewView?.post {
                startCamera()
            }
        }
    }


    // 调起相机
    private fun startCamera() {
        viewFinder?.implementationMode =
            PreviewView.ImplementationMode.COMPATIBLE // 指定模式，以便支持旋转
        viewFinder?.scaleType = PreviewView.ScaleType.FILL_START
        // start
        startCamera(cameraSelector, previewRotation)
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera(
        selector: CameraSelector,
        @ImageOutputConfig.RotationValue rotation: Int
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                // 当ProcessCameraProvider准备就绪时
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // 预览用例
                val preview = Preview.Builder()
                    .setTargetRotation(rotation)
                    .build()
                    .also {
                        it.setSurfaceProvider(viewFinder?.surfaceProvider)
                    }

                // 图像捕获用例
                // ImageAnalysis. Using RGBA 8888 to match how our models work
                imageAnalyzer = ImageAnalysis.Builder()
                    // .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setTargetRotation(rotation)
                    // .setTargetRotation(_binding.viewFinder.display.rotation)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()
                    // The analyzer can then be assigned to the instance
                    .also {
                        it.setAnalyzer(
                            backgroundExecutor,
                            this::detectLivestreamFrame
                        )
                    }

                // 拍照用例
                mImageCapture = ImageCapture.Builder()
                    .setTargetRotation(rotation)
                    .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                    .build()

                "cameraInfo:${tip}".logI()
                "$tip".toast()

                cameraProvider.unbindAll()
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this,
                    selector,
                    preview,
                    imageAnalyzer,
                    mImageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
                "Camera initialization error:$e".toast()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onError(error: String, errorCode: Int) {
        "onError $error".logE()
    }

    private fun detectLivestreamFrame(imageProxy: ImageProxy) {
        faceDetectorHelper?.detectLivestreamFrame(imageProxy, previewJxFzTag)
    }


    // Update UI after faces have been detected. Extracts original image height/width
    // to scale and place bounding boxes properly through OverlayView
    override fun onResults(resultBundle: FaceDetectorHelper.ResultBundle) {
        runOnUiThread {
            // Pass necessary information to OverlayView for drawing on the canvas
            val detectionResult = resultBundle.results[0]
            //            "onResults: $detectionResult".logI()
            // 此处可添加抓图、上传服务器比对的逻辑
            try {
                detectionResult?.let {
                    for (detection in it.detections()) {
                        val score = detection.categories()[0].score()
                        if (score > qualifiedScore) {
                            //  "分数达标了: $score".logI()
                            // takeStaticPhoto(score)
                            // 后续讨论上传频率 暂用按钮测试
                        }
                    }
                }
            } catch (e: Exception) {
                "分数解析异常: $e".logE()
            }


            // 绘制标识人脸的矩形框
            mScreen3?.overlay?.setResults(
                detectionResult,
                resultBundle.inputImageHeight,
                resultBundle.inputImageWidth
            )
            // Force a redraw
            mScreen3?.overlay?.invalidate()
        }
    }


    override fun onResume() {
        super.onResume()
        backgroundExecutor.execute {
            if (faceDetectorHelper?.isClosed() == true) {
                faceDetectorHelper?.setupFaceDetector()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        faceDetectorHelper?.run {
            _delegate = currentDelegate
            _threshold = threshold
            // Close the face detector and release resources
            backgroundExecutor.execute { clearFaceDetector() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Shut down our background executor.
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE,
            TimeUnit.NANOSECONDS
        )
    }


}