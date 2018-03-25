package com.plbear.iweight.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by yanyongjun on 2018/1/13.
 */
open class SPUtils {
    companion object {
        private var SP: SharedPreferences? = null
        private val TAG = "SPUtils"
        fun getSp(): SharedPreferences {
            if (SP != null) {
                return SP!!
            }
            SP = PreferenceManager.getDefaultSharedPreferences(App.getAppContext())
            return SP!!
        }
    }
}