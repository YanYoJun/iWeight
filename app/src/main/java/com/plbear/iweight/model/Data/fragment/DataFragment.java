package com.plbear.iweight.model.Data.fragment;

import com.plbear.iweight.base.BaseFragment;
import com.plbear.iweight.R;
import com.plbear.iweight.model.main.fragment.MainDataFragment;

public class DataFragment extends BaseFragment {
    @Override
    public int getLayout() {
        return R.layout.fragment_data;
    }

    @Override
    public void afterLayout() {
        MainDataFragment weekFrag = (MainDataFragment) getChildFragmentManager().findFragmentById(R.id.frag_week);
        MainDataFragment monthFrag = (MainDataFragment) getChildFragmentManager().findFragmentById(R.id.frag_month);
        MainDataFragment allFrag = (MainDataFragment) getChildFragmentManager().findFragmentById(R.id.frag_all);

        weekFrag.setTag("weekFrag");
        weekFrag.setShowDateNums(7);

        monthFrag.setTag("monthFrag");
        monthFrag.setShowDateNums(30);

        allFrag.setTag("allFrag");
        allFrag.setShowAllData(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}