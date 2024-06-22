package com.zg.quickbase.module.ui.tabbase

import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.zg.baselibrary.base.BaseActivity
import com.zg.quickbase.databinding.ActivityTabBaseBinding

class TabBaseActivity : BaseActivity() {

    private lateinit var binding: ActivityTabBaseBinding

    override fun getRoot(): View {
        binding = ActivityTabBaseBinding.inflate(layoutInflater)
        return  binding.root
    }

    override fun initView() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
    }

    override fun initViewModel() {

    }
}