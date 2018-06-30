package com.plbear.iweight.model.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by yanyongjun on 2017/normal_7/22.
 */

public class MainDataFragmentAdapter extends FragmentPagerAdapter {

    private FragmentManager mFm;
    private List<Fragment> mFragLists;

    public MainDataFragmentAdapter(FragmentManager fm, List<Fragment> mFragLists) {
        super(fm);
        mFm = fm;
        this.mFragLists = mFragLists;
    }


    public Fragment getItem(int position) {
        return mFragLists.get(position);
    }


    public int getCount() {
        return mFragLists.size();
    }
}
