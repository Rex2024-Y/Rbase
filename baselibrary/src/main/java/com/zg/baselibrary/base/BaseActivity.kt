package com.zg.baselibrary.base

import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.zg.baselibrary.R
import com.zg.baselibrary.constants.LiveDataBusKey
import com.zg.baselibrary.manager.ClickManager
import com.zg.baselibrary.utils.LiveDataBus
import com.zg.baselibrary.utils.LogUtils


open abstract class BaseActivity : AppCompatActivity() {

    var isShowing = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            "${this@BaseActivity}页面发生了重启".logE()
        }
        // 开启沉浸式
        enableEdgeToEdge()
        // 设置全屏模式
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        getRoot()?.run {
            setContentView(getRoot())
            // 是否让出状态栏高度
            WindowCompat.setDecorFitsSystemWindows(window, false)
            initViewModel()
            initView()
            initOther()
        }

        // 隐藏虚拟按键
        hideSystemUI()
    }

    private fun initOther() {
        // 全局观察者模式 可以到处发消息 页面到达才会安全生效
        LiveDataBus.instance.with(LiveDataBusKey.KEY_LOGIN_TIME_OUT, Int::class.java)
            .observe(
                this
            ) { value ->
                run {
                    if (ClickManager.instance.checkTimeOut()) {
                        ClickManager.instance.showLogTimeOutDialog(this@BaseActivity)
                    }
                }
            }
    }

    var logTimeOut = -1


    private fun hideSystemUI() {
        // 隐藏导航栏和状态栏
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions

        // 当触摸屏被触摸时，隐藏虚拟按键
        decorView.setOnTouchListener { view, motionEvent ->
            if (view.systemUiVisibility != View.SYSTEM_UI_FLAG_FULLSCREEN) {
                view.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            }
            false
        }
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        "onRestoreInstanceState ${savedInstanceState != null}".logE()
    }

    abstract fun getRoot(): View?

    abstract fun initView()


    abstract fun initViewModel()

    fun String.toast() {
        runOnUiThread {
            Toast.makeText(this@BaseActivity, this, Toast.LENGTH_SHORT).show()
        }
    }

    fun String.log() {
        LogUtils.log(this)
    }

    fun String.logI() {
        LogUtils.logI(this@BaseActivity.javaClass.simpleName, this)
    }

    fun String.logD() {
        LogUtils.logD(this@BaseActivity.javaClass.simpleName, this)
    }

    fun String.logE() {
        LogUtils.logE(this@BaseActivity.javaClass.simpleName, this)
    }


    /**
     * 界面跳转封装
     */
    fun Class<*>.start() {
        startActivity(
            Intent(
                this@BaseActivity,
                this
            )
        )
    }

    /**
     * 界面跳转封装
     * String 类型
     */
    fun Class<*>.start(url: String) {
        val intent = Intent(
            this@BaseActivity,
            this
        )
        intent.putExtra(KEY_URL, url)
        startActivity(
            intent
        )
    }

    /**
     * 界面跳转封装
     * Int 类型
     */
    fun Class<*>.start(id: Int) {
        val intent = Intent(
            this@BaseActivity,
            this
        )
        intent.putExtra(KEY_ID, id)
        startActivity(
            intent
        )
    }

    fun showListDialog(
        title: String?,
        items: Array<String>,
        listener: OnClickListener,
    ): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setItems(items, listener)
        val dialog = builder.create()
        dialog.show()
        return dialog
    }

    fun showCheckDialog(
        title: String?,
        content: String,
        listener: OnClickListener,
    ): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(content)
        builder.setPositiveButton("确定", listener)
        builder.setNegativeButton("取消", null)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        dialog.show()
        return dialog
    }


    fun showInputDialog(
        text: String?,
        hint: String?,
        leftText: String,
        rightText: String,
        leftOnClickListener: DialogClickListener? = null,
        rightOnClickListener: DialogClickListener? = null,
    ): AlertDialog {
        return showInputDialog(
            text,
            hint,
            leftText,
            rightText,
            true,
            leftOnClickListener,
            rightOnClickListener
        )
    }


    fun showInputDialog(
        text: String?,
        hint: String?,
        leftText: String,
        rightText: String,
        dismiss: Boolean,
        leftOnClickListener: DialogClickListener? = null,
        rightOnClickListener: DialogClickListener? = null,
    ): AlertDialog {
        val builder = AlertDialog.Builder(this)
        val view: View = View.inflate(this, R.layout.dialog_input_text, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val etContent = view.findViewById<EditText>(R.id.etContent)
        val tvLeft = view.findViewById<TextView>(R.id.tvLeft)
        val tvRight = view.findViewById<TextView>(R.id.tvRight)
        etContent.setText(text)
        etContent.setHint(hint)
        tvLeft.text = leftText
        tvRight.text = rightText
        dialog.setCanceledOnTouchOutside(true)
        tvLeft.setOnClickListener {
            leftOnClickListener?.onClick(dialog, "")
            ClickManager.instance.down()
            dialog.dismiss()
        }

        tvRight.setOnClickListener {
            val text = etContent.text.trim().toString()
            rightOnClickListener?.onClick(dialog, text)
            ClickManager.instance.down()
            if (dismiss) dialog.dismiss()
        }

        dialog.show()
        return dialog
    }


    fun showTipDialog(
        title: String?,
        content: String?,
        listener: View.OnClickListener? = null,
    ): AlertDialog {
        val builder = AlertDialog.Builder(this)
        val view: View = View.inflate(this, R.layout.dialog_tip, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvContent = view.findViewById<TextView>(R.id.tvContent)
        val tvRight = view.findViewById<TextView>(R.id.tvRight)


        tvTitle.text = title
        tvContent.text = content

        dialog.setCanceledOnTouchOutside(false)
        listener?.run {
            dialog.dismiss()
            ClickManager.instance.down()
            tvRight.setOnClickListener(this)
        } ?: let {
            tvRight.setOnClickListener {
                dialog.dismiss()
                ClickManager.instance.down()
            }
        }

        dialog.show()
        return dialog
    }

    fun showChooseDialog(
        content: String?,
        leftText: String,
        rightText: String,
        leftOnClickListener: DialogClickListener? = null,
        rightOnClickListener: DialogClickListener? = null,
    ): AlertDialog {
        val builder = AlertDialog.Builder(this)
        val view: View = View.inflate(this, R.layout.dialog_choose, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

//        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvContent = view.findViewById<TextView>(R.id.tvContent)
        val tvLeft = view.findViewById<TextView>(R.id.tvLeft)
        val tvRight = view.findViewById<TextView>(R.id.tvRight)
//        tvTitle.text = title
        tvContent.text = content
        tvLeft.text = leftText
        tvRight.text = rightText

        dialog.setCanceledOnTouchOutside(true)
        tvLeft.setOnClickListener {
            ClickManager.instance.down()
            leftOnClickListener?.onClick(dialog, "") ?: let {
                dialog.dismiss()
            }
        }
        tvRight.setOnClickListener {
            dialog.dismiss()
            ClickManager.instance.down()
            rightOnClickListener?.onClick(dialog, "")
        }
        dialog.show()
        return dialog
    }


    fun showListDialog(
        items: Array<String>,
        listener: OnClickListener,
    ): AlertDialog {
        val dialogB = AlertDialog.Builder(this@BaseActivity)
        dialogB.run {
            setTitle("请选择")
            setCancelable(true)
            setItems(items, listener)

        }
        dialogB.show()
        return dialogB.create()

    }

    var loadingDialog: AlertDialog? = null
    fun showLoading(title: String? = null): AlertDialog? {
        val builder = AlertDialog.Builder(this@BaseActivity)
        val view: View = View.inflate(this, R.layout.dialog_loading, null)

        title?.run {
            val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
            tvTitle.text = title
        }
        builder.setView(view)
        loadingDialog = builder.create()
        loadingDialog?.show()
        loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return loadingDialog

    }


    var mProgressDialog: AlertDialog? = null
    var tvProgressContent: TextView? = null
    var tvProgressProgress: TextView? = null
    var progressBar: ProgressBar? = null
    fun showProgressDialog(title: String? = null): AlertDialog? {
        val builder = AlertDialog.Builder(this@BaseActivity)
        val view: View = View.inflate(this, R.layout.dialog_progress, null)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        tvProgressContent = view.findViewById(R.id.tvContent)
        tvProgressProgress = view.findViewById(R.id.tvProgress)
        progressBar = view.findViewById(R.id.pb)
        title?.run {
            tvTitle.text = title
        }
        builder.setView(view)
        mProgressDialog = builder.create()
        mProgressDialog?.setCanceledOnTouchOutside(false)
        mProgressDialog?.show()
        mProgressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return mProgressDialog

    }


    fun dismissLoading() {
        loadingDialog?.dismiss()
    }

    fun isLoading(): Boolean {
        return loadingDialog?.isShowing == true
    }

    override fun onStop() {
        super.onStop()
        dismissLoading()
        isShowing = false
    }

    override fun onResume() {
        super.onResume()
        isShowing = true
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            ClickManager.instance.down()
            // 可以在这里添加日志记录或其他处理
        }

        // 继续正常的事件分发
        return super.dispatchTouchEvent(ev)
    }

    public interface DialogClickListener {
        fun onClick(dialog: AlertDialog, text: String)
    }

    companion object {
        val KEY_URL = "url"
        val KEY_BOX_POSITION = "key_box_position"
        val KEY_ID = "key_id"
        val KEY_POSITION = "key_position"
        val KEY_WEIGHT = "key_weight"
        val KEY_SAMPLE_IFNO = "key_sample_ifno"
        val KEY_AVATAR = "key_avatar"

    }
}
