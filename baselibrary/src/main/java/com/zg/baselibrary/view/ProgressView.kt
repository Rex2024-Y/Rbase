package com.zg.baselibrary.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.zg.baselibrary.R
import com.zg.baselibrary.utils.LogUtils.log
import com.zg.baselibrary.utils.ScreenUtils.dp2px

/**
 * 项目定制ui
 */
class ProgressView : LinearLayout {
    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private var mX = -1
    var minX = 0
    var maxX = 0
    var mProgress = 30

    private var mOnCustomProgressChangeListener: OnCustomProgressChangeListener? = null
    private var pbBackground: View? = null
    private var pbProgress: View? = null
    private var ivThumb: View? = null
    private val dp7 = dp2px(context, 7f).toInt()

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        inflate(context, R.layout.seekbar_custom_ui, this)
        pbBackground = findViewById(R.id.pbBackground)
        pbProgress = findViewById(R.id.pbProgress)
        ivThumb = findViewById(R.id.ivThumb)
        ivThumb?.post {
            // 初始化参数
            minX = dp7 + ivThumb!!.width / 2
            maxX = pbBackground!!.width - minX
            log("ProgressView min:$minX")
            log("ProgressView max:$maxX")
            // 初始化
            setProgress(mProgress)
        }
        pbBackground?.setOnTouchListener { v: View?, event: MotionEvent ->
            mX = event.x.toInt()
            updateX()
            mOnCustomProgressChangeListener?.onProgressChange(mProgress)
            true
        }
    }

    private fun updateX() {
        if (mX < minX) {
            mX = minX
        } else if (mX > maxX) {
            mX = maxX
        }
        // 拖动圆中心和手对齐
        val trueX = (mX - ivThumb!!.width / 2).toFloat()
        ivThumb!!.x = trueX

        // 进度和手半圆加间距对齐
        val layoutParams = pbProgress!!.layoutParams
        layoutParams.width = (trueX + ivThumb!!.width + dp7).toInt()
        pbProgress!!.setLayoutParams(layoutParams)
        // 根据比例算出进度
        mProgress = (mX - minX) * 100 / (maxX - minX)
    }

    fun setProgress(progress: Int) {
        // 根据进度算出比例
        mX = progress * (maxX - minX) / 100 + minX
        updateX()
    }

    fun setOnProgressChangeListener(onProgressChangeListener: OnCustomProgressChangeListener?) {
        this.mOnCustomProgressChangeListener = onProgressChangeListener
    }
}

public interface OnCustomProgressChangeListener {
    fun onProgressChange(progress: Int)
}
