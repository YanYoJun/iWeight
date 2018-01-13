package com.plbear.iweight.Utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by yanyongjun on 2018/1/13.
 */
open class SPUtils {
    companion object {
        private var SP: SharedPreferences? = null
        fun getSP(context: Context): SharedPreferences? {
            if (SP != null) {
                return SP
            }
            SP = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
            return SP
        }
    }
}