package com.example.wasteoftime

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class InfoPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                Log.wtf("Tab Shown", "fragment 1")
                Fragment_1()
            }
            else -> {
                Log.wtf("Tab Shown", "fragment 2")
                return Fragment_2()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Why"
            else -> {
                return "How"
            }
        }
    }
}