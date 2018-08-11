package com.plbear.iweight.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plbear.iweight.R;
import com.plbear.iweight.utils.LogInfo;

public abstract class BaseFragment extends Fragment {
    protected BaseActivity mActivity = null;
    protected String TAG = this.getClass().getSimpleName();
    public abstract int getLayout();

    public abstract void afterLayout();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (BaseActivity)getActivity();
        View v = inflater.inflate(getLayout(), null);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        afterLayout();
    }

    protected void loginfo(String info) {
        LogInfo.i(TAG, info);
    }

    protected void logerror(String error) {
        LogInfo.e(TAG, error);
    }
}
