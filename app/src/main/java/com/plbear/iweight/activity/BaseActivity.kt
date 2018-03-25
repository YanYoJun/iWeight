package com.plbear.iweight.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import com.plbear.iweight.utils.MyLog

/**
 * Created by yanyongjun on 2018/1/28.
 */
abstract class BaseActivity : Activity() {
    abstract fun getLayout(): Int
    abstract fun afterLayout()

    companion object {
        protected val TAG = this.javaClass.simpleName
        val ACTION_EXIT = "com.plbear.iweight.ACTION_EXIT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        val filter = IntentFilter()
        filter.addAction(ACTION_EXIT)
        registerReceiver(mReceiver, filter)
        MyLog.d(TAG, "onCreate")
        afterLayout()
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
        MyLog.d(TAG, "onDestroy")
    }

    override fun onStart() {
        super.onStart()
        MyLog.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        MyLog.d(TAG, "onResume")
    }

    protected fun exitAll() {
        val intent = Intent(ACTION_EXIT)
        intent.`package` = packageName
        sendBroadcast(intent)
    }

    internal var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || context == null) {
                MyLog.e(TAG, "onReceive context == null or intent == null")
            }
            val action = intent!!.action
            MyLog.d(TAG, "onReceive action:" + action!!)
            if (ACTION_EXIT == action) {
                finish()
            }
        }
    }
}