package com.zg.quickbase.module.ui.bottomnav

import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.zg.quickbase.R
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityBottomNavBinding
import com.zg.quickbase.module.ui.bottomnav.ui.dashboard.DashboardFragment
import com.zg.quickbase.module.ui.bottomnav.ui.home.HomeFragment
import com.zg.quickbase.module.ui.bottomnav.ui.notifications.NotificationsFragment


class BottomNavActivity : BaseActivity() {

    private lateinit var binding: ActivityBottomNavBinding
    val homeFragment = HomeFragment()
    val dashboardFragment = DashboardFragment()
    val notificationsFragment = NotificationsFragment()
    var mFragmentList = arrayListOf(homeFragment, dashboardFragment, notificationsFragment)


    override fun getRoot(): View? {
        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {

        object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return mFragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return mFragmentList[position]
            }
        }.also { binding.viewPager.adapter = it }

        val navView: BottomNavigationView = binding.navView

        navView.setOnItemSelectedListener(object : OnNavigationItemSelectedListener,
            NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
                menuItem.toString().logD()
                when (menuItem.itemId) {
                    R.id.navigation_home -> {
                        binding.viewPager.currentItem = 0
                    }

                    R.id.navigation_dashboard -> {
                        binding.viewPager.currentItem = 1
                    }

                    R.id.navigation_notifications -> {
                        binding.viewPager.currentItem = 2
                    }

                    else -> {
                        binding.viewPager.currentItem = 0
                    }
                }
                return true
            }
        })

    }

    override fun initViewModel() {
    }
}