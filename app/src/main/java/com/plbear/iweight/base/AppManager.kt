package com.plbear.iweight.base

import android.app.Activity
import java.util.*

/**
 * Created by yanyongjun on 2018/4/5.
 */
class AppManager {
    companion object {
        private var mInstance: AppManager? = null
        private var mActivities = Stack<Activity>()
        fun getAppManager(): AppManager {
            if (mInstance != null) {
                return mInstance!!
            }
            mInstance = AppManager()
            return mInstance!!
        }
    }

    fun addToStack(activity: Activity) {
        mActivities.push(activity)
    }

    fun removeFromStack(activity: Activity) {
        mActivities.remove(activity)
    }

    fun finshAllActivity() {
        while (!mActivities.isEmpty()) {
            var activity = mActivities.pop()
            activity.finish()
        }
    }
}