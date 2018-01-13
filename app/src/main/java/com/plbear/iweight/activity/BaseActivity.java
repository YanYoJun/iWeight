package com.plbear.iweight.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.plbear.iweight.Utils.ILog;

/**
 * Created by yanyongjun on 16/11/5.
 */

public abstract class BaseActivity extends Activity {
    protected String TAG = "BaseActivity";
    public static final String ACTION_EXIT = "com.plbear.iweight.ACTION_EXIT";

    protected void setTag(String tag) {
        TAG = tag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_EXIT);
        registerReceiver(mReceiver, filter);
        ILog.d(TAG, "onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        ILog.d(TAG, "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        ILog.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ILog.d(TAG, "onResume");
    }

    protected void exitAll() {
        Intent intent = new Intent(ACTION_EXIT);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || context == null) {
                ILog.e(TAG, "onReceive context == null or intent == null");
            }
            String action = intent.getAction();
            ILog.d(TAG, "onReceive action:" + action);
            if (ACTION_EXIT.equals(action)) {
                finish();
            }
        }
    };
}
