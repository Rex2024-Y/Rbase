package com.zg.quickbase

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.zg.quickbase.module.ui.tabbase.TabBaseActivity
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityMainBinding
import com.zg.quickbase.module.ui.bottomnav.BottomNavActivity
import com.zg.quickbase.module.ui.login.LoginActivity
import com.zg.quickbase.module.ui.verticaltab.VerticalTabActivity
import com.zg.quickbase.viewmodel.MainViewModel


class MainActivity : BaseActivity() {

    var mBinding: ActivityMainBinding? = null
    lateinit var mViewModel: MainViewModel

    override fun getRoot(): View? {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        return mBinding?.root
    }

    override fun initViewModel() {
        mViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mViewModel.run {
            mAdater.addAll(listData)

        }
    }

    override fun initView() {
        mBinding?.run {
            tvName.text = mViewModel.title
            rvMain.adapter = mAdater
            val layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            rvMain.setLayoutManager(layoutManager)
            mAdater.setOnItemClickListener { adapter, view, position ->
                run {
                    mAdater.getItem(position)?.let {
                        onItemClick(it, position)
                    }
                }
            }
        }
    }

    private fun onItemClick(it: MainViewModel.MainRvBean, position: Int) {
        "onItemClick".logD()
        when (it.name) {

            "LoginActivity" -> {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }

            "TabActivity" -> {
                startActivity(Intent(this@MainActivity, TabBaseActivity::class.java))
            }

            "BottomNavActivity" -> {
                startActivity(Intent(this@MainActivity, BottomNavActivity::class.java))
            }

            "VerticalTabActivity" -> {
                startActivity(Intent(this@MainActivity, VerticalTabActivity::class.java))
            }

            else -> {
                "未定义事件".toast()
            }
        }
    }


    val mAdater = object : BaseQuickAdapter<MainViewModel.MainRvBean, QuickViewHolder>() {

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): QuickViewHolder {
            return QuickViewHolder(R.layout.item_main_rv, parent)
        }

        override fun onBindViewHolder(
            holder: QuickViewHolder,
            position: Int,
            item: MainViewModel.MainRvBean?
        ) {
            item?.run {
                holder.setText(R.id.btTitle, name)
            }
        }
    }


}