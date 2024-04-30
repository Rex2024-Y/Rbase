package com.zg.quickbase

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityMainBinding
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
                        "${it.name}".toast()
                    }
                }
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