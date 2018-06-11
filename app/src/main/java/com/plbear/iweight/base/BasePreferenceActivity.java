package com.plbear.iweight.base;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

import com.plbear.iweight.utils.LogInfo;

import butterknife.ButterKnife;

/**
 * Created by yanyongjun on 2018/6/11.
 */

public class BasePreferenceActivity extends PreferenceActivity {
    protected String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addToStack(this);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeFromStack(this);
    }

    protected void loginfo(String info) {
        LogInfo.i(TAG, info);
    }

    protected void logerror(String error) {
        LogInfo.e(TAG, error);
    }

}
