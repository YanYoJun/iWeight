package com.plbear.iweight.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.plbear.iweight.utils.LogInfo;

import butterknife.ButterKnife;

/**
 * Created by yanyongjun on 2018/6/11.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public abstract int getLayout();

    public abstract void afterLayout();

    protected String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int layout = getLayout();
        if (layout != 0) {
            setContentView(layout);
            ButterKnife.bind(this);
        }
        loginfo("onCreate");
        AppManager.getAppManager().addToStack(this);
        afterLayout();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginfo("onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeFromStack(this);
        loginfo("onDestory");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginfo("onResume");
    }

    protected void loginfo(String info) {
        LogInfo.i(TAG, info);
    }

    protected void logerror(String error) {
        LogInfo.e(TAG, error);
    }

    protected void exitAll() {
        AppManager.getAppManager().finishAllActivity();
    }

    @Override
    public void finish() {
        AppManager.getAppManager().removeFromStack(this);
        super.finish();
    }
}
