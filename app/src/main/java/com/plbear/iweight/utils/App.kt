package com.plbear.iweight.utils

import android.app.Application
import android.content.Context

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
        MyLog.e("App","onCreate")
        mContext = applicationContext
        super.onCreate()
    }
}