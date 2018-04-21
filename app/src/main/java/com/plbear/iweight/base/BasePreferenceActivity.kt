package com.plbear.iweight.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceActivity
import com.plbear.iweight.utils.LogInfo

/**
 * Created by yanyongjun on 2017/normal_4/normal_1.
 */

class BasePreferenceActivity : PreferenceActivity() {
    protected val TAG = this.javaClass.simpleName

    internal var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || context == null) {
                logerror("onReceive context == null or intent == null")
            }
            val action = intent!!.action
            loginfo("onReceive action:" + action!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginfo("onCreate")
        AppManager.getAppManager().addToStack(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.getAppManager().removeFromStack(this)
    }

    fun loginfo(info: String) {
        LogInfo.i(TAG, info)
    }

    fun logerror(error: String) {
        LogInfo.e(TAG, error)
    }
}
