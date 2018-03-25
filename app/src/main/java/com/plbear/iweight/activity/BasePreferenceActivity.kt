package com.plbear.iweight.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.preference.PreferenceActivity

import com.plbear.iweight.utils.MyLog

/**
 * Created by yanyongjun on 2017/4/1.
 */

class BasePreferenceActivity : PreferenceActivity() {

    internal var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || context == null) {
                MyLog.e(TAG, "onReceive context == null or intent == null")
            }
            val action = intent!!.action
            MyLog.d(TAG, "onReceive action:" + action!!)
            if (BaseActivity.ACTION_EXIT == action) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filter = IntentFilter()
        filter.addAction(BaseActivity.ACTION_EXIT)
        registerReceiver(mReceiver, filter)
        MyLog.d(TAG, "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

    companion object {
        private val TAG = "BasePreferenceActivity"
    }
}
