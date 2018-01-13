package com.plbear.iweight.model.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by HuHu on 2017/7/22.
 */

public class MainDataFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragLists;

    public MainDataFragmentAdapter(FragmentManager fm, List<Fragment> fragList) {
        super(fm);
        mFragLists = fragList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragLists.get(position);
    }

    @Override
    public int getCount() {
        return mFragLists.size();
    }
}
