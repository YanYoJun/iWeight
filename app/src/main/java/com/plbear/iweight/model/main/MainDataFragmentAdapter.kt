package com.plbear.iweight.model.main

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by yanyongjun on 2017/7/22.
 */

class MainDataFragmentAdapter(fm: FragmentManager, private val mFragLists: List<Fragment>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return mFragLists[position]
    }

    override fun getCount(): Int {
        return mFragLists.size
    }
}
