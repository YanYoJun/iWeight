package com.plbear.iweight.base

import android.app.Application
import android.content.Context
import android.util.Log
import com.plbear.iweight.utils.LogInfo

/**
 * Created by yanyongjun on 2018/1/28.
 */
class App : Application() {
    companion object {
        private lateinit var mContext: Context
        fun getAppContext(): Context {
            return mContext
        }
    }

    override fun onCreate() {
        mContext = applicationContext
        super.onCreate()
    }
}