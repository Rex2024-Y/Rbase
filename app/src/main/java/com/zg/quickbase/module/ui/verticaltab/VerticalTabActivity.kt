package com.zg.quickbase.module.ui.verticaltab

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.zg.quickbase.R
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityLeftTabBinding
import com.zg.quickbase.module.ui.bottomnav.ui.dashboard.DashboardFragment
import com.zg.quickbase.module.ui.bottomnav.ui.home.HomeFragment
import com.zg.quickbase.module.ui.bottomnav.ui.notifications.NotificationsFragment
import com.zg.quickbase.viewmodel.MainViewModel


/**
 * 用rv作为左边tab充分自定义
 */
class VerticalTabActivity : BaseActivity() {

    private lateinit var binding: ActivityLeftTabBinding
    val homeFragment = HomeFragment()
    val dashboardFragment = DashboardFragment()
    val notificationsFragment = NotificationsFragment()
    var mFragmentList = arrayListOf(homeFragment, dashboardFragment, notificationsFragment)


    override fun getRoot(): View? {
        binding = ActivityLeftTabBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {


        val rvAdapter = object : BaseQuickAdapter<MainViewModel.MainRvBean, QuickViewHolder>() {
            override fun onCreateViewHolder(
                context: Context,
                parent: ViewGroup,
                viewType: Int
            ): QuickViewHolder {
                return QuickViewHolder(R.layout.item_v_tab_rv, parent)
            }

            override fun onBindViewHolder(
                holder: QuickViewHolder,
                position: Int,
                item: MainViewModel.MainRvBean?
            ) {
                item?.run {
                    holder.setText(R.id.btTitle, text)
                }
            }
        }
        binding.rvLeftTab.adapter = rvAdapter

        val layoutManager =
            LinearLayoutManager(this@VerticalTabActivity, RecyclerView.VERTICAL, false)
        binding.rvLeftTab.setLayoutManager(layoutManager)
        rvAdapter.setOnItemClickListener { adapter, view, position ->
            run {
                binding.viewPager.currentItem = position
            }
        }

        object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return mFragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return mFragmentList[position]
            }
        }.also { binding.viewPager.adapter = it }

        binding.viewPager.isUserInputEnabled = false

        rvAdapter.addAll(
            arrayListOf(
                MainViewModel.MainRvBean("Home", "", 1),
                MainViewModel.MainRvBean("DashBoard", "", 2),
                MainViewModel.MainRvBean("Notifications", "", 3),
            )
        )
    }

    override fun initViewModel() {
    }
}