package com.plbear.iweight.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.plbear.iweight.Utils.ILog;

import static com.plbear.iweight.activity.BaseActivity.ACTION_EXIT;

/**
 * Created by HuHu on 2017/4/1.
 */

public class BasePreferenceActivity extends PreferenceActivity {
    private final static String TAG = "BasePreferenceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_EXIT);
        registerReceiver(mReceiver,filter);
        ILog.d(TAG,"onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null || context == null){
                ILog.e(TAG,"onReceive context == null or intent == null");
            }
            String action = intent.getAction();
            ILog.d(TAG,"onReceive action:"+action);
            if(ACTION_EXIT.equals(action)){
                finish();
            }
        }
    };
}
