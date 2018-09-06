package com.plbear.iweight.model.other;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.plbear.iweight.R;
import com.plbear.iweight.base.BaseActivity;
import com.plbear.iweight.base.MainActivity;
import com.plbear.iweight.utils.PermissionHelper;

/**
 * Created by yanyonigjun on 2018/4/15.
 */

public class SplashActivity extends BaseActivity {
    private final static String TAG = SplashActivity.class.getSimpleName();
    private PermissionHelper mPermissionHelper;
    private Context mContext = null;
    private boolean mIsFirstLoad = false;

    @Override
    public int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public void afterLayout() {
        mContext = this;
        // 当系统为6.0以上时，需要申请权限
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
                Log.e(TAG, "All of requested permissions has been granted, so run app logic.");
            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            Log.e(TAG, "The api level of system is lower than 23, so run app logic directly.");
            startMainActivity();
        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                Log.e(TAG, "All of requested permissions has been granted, so run app logic directly.");
                startMainActivity();
            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                Log.e(TAG, "Some of requested permissions hasn't been granted, so apply permissions first.");
                mPermissionHelper.applyPermissions();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }


    private void startMainActivity() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
            }
        }, 400);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mPermissionHelper.isAllRequestedPermissionGranted()){
            startMainActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 开屏展示界面的 onDestroy() 回调方法中调用
    }
}

