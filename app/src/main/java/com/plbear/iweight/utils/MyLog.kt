package com.plbear.iweight.utils

import android.util.Log

/**
 * Created by yanyongjun on 2018/1/28.
 */
class MyLog {
    companion object {
        private val TAG = "iWeight"

        fun d(tag: String, msg: String) {
            Log.e(TAG, tag + ":" + msg)
        }

        fun e(tag: String, msg: String) {
            Log.e(TAG, tag + ":" + msg)
        }

        fun i(tag: String, msg: String) {
            Log.e(TAG, tag + ":" + msg)
        }
    }
}